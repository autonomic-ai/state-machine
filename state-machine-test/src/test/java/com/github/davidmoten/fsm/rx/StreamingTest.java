/*-
 * ‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾
 * The Apache Software License, Version 2.0
 * ——————————————————————————————————————————————————————————————————————————————
 * Copyright (C) 2013 - 2020 Autonomic, LLC - All rights reserved
 * ——————————————————————————————————————————————————————————————————————————————
 * Proprietary and confidential.
 * 
 * NOTICE:  All information contained herein is, and remains the property of
 * Autonomic, LLC and its suppliers, if any.  The intellectual and technical
 * concepts contained herein are proprietary to Autonomic, LLC and its suppliers
 * and may be covered by U.S. and Foreign Patents, patents in process, and are
 * protected by trade secret or copyright law. Dissemination of this information
 * or reproduction of this material is strictly forbidden unless prior written
 * permission is obtained from Autonomic, LLC.
 * 
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * ______________________________________________________________________________
 */
package com.github.davidmoten.fsm.rx;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.davidmoten.fsm.example.generated.MicrowaveBehaviour;
import com.github.davidmoten.fsm.example.generated.MicrowaveBehaviourBase;
import com.github.davidmoten.fsm.example.generated.MicrowaveStateMachine;
import com.github.davidmoten.fsm.example.microwave.Microwave;
import com.github.davidmoten.fsm.example.microwave.event.ButtonPressed;
import com.github.davidmoten.fsm.example.microwave.event.DoorOpened;
import com.github.davidmoten.fsm.example.microwave.event.TimerTimesOut;
import com.github.davidmoten.fsm.runtime.Signal;
import com.github.davidmoten.fsm.runtime.Signaller;
import com.github.davidmoten.fsm.runtime.rx.Processor;

import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.TestSubscriber;

public class StreamingTest {

    @Test
    public void testJsonInputToStateMachineIssue1() throws InterruptedException {

        // JSON source stream (could contain other messages about other
        // Microwaves with different ids which will be processed concurrently by
        // the Processor)
        Flowable<String> messages = Flowable.just(
                "{\"cls\": \"Microwave\", \"id\": \"1\",\"event\": \"ButtonPressed\"}",
                "{\"cls\": \"Microwave\", \"id\": \"1\",\"event\": \"DoorOpened\"}",
                "{\"cls\": \"Microwave\", \"id\": \"1\",\"event\": \"ButtonPressed\"}");

        Flowable<Signal<?, String>> signals = messages //
                .map(msg -> toSignal(msg));

        // special scheduler that we will use to schedule signals and to process
        // events
        Scheduler scheduler = Schedulers.from(Executors.newFixedThreadPool(3));

        // create the signal processor
        Processor<String> processor = createProcessor(scheduler, signals);

        // using a test subscriber because has easy assert methods on it
        TestSubscriber<Object> ts = TestSubscriber.create();

        // subscribe to the stream of entity states that is produced from the
        // signals stream
        processor //
                .flowable() //
                // just output the states
                .map(esm -> esm.state()) //
                .subscribe(ts);

        // wait for processing to finish (is running asynchronously using the
        // scheduler)
        Thread.sleep(1000);

        // assert that things happened as we expected
        ts.assertValues( //
                MicrowaveStateMachine.State.COOKING,
                MicrowaveStateMachine.State.COOKING_INTERRUPTED,
                MicrowaveStateMachine.State.COOKING_INTERRUPTED);

    }

    private static Signal<?, String> toSignal(String msg) {
        JsonNode t = readTree(msg);
        String id = t.get("id").asText();
        String event = t.get("event").asText();
        if ("ButtonPressed".equals(event)) {
            return Signal.create(Microwave.class, id, new ButtonPressed());
        } else if ("DoorOpened".equals(event)) {
            return Signal.create(Microwave.class, id, new DoorOpened());
        } else
            throw new RuntimeException("event not recognized: " + event);
    }

    private static JsonNode readTree(String s) {
        ObjectMapper m = new ObjectMapper();
        try {
            return m.readTree(s);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Processor<String> createProcessor(Scheduler scheduler,
            Flowable<Signal<?, String>> signals) {
        MicrowaveBehaviour<String> behaviour = createMicrowaveBehaviour();

        // build a processor
        Processor<String> processor = Processor //
                .behaviour(Microwave.class, behaviour) //
                .processingScheduler(scheduler) //
                .signalScheduler(scheduler) //
                .signals(signals) //
                .preTransition((m, event, state) -> System.out.println("[preTransition] "
                        + event.getClass().getSimpleName() + ": " + m.state() + " -> " + state)) //
                .postTransition(m -> System.out.println("[postTransition] " + m.state())) //
                .build();
        return processor;
    }

    private static MicrowaveBehaviourBase<String> createMicrowaveBehaviour() {
        return new MicrowaveBehaviourBase<String>() {

            @Override
            public MicrowaveStateMachine<String> create(String id) {
                return MicrowaveStateMachine.create(Microwave.fromId(id), id, this,
                        MicrowaveStateMachine.State.READY_TO_COOK);
            }

            @Override
            public Microwave onEntry_Cooking(Signaller<Microwave, String> signaller,
                    Microwave microwave, String id, ButtonPressed event, boolean isReplay) {
                signaller.signalToSelf(new TimerTimesOut(), 30, TimeUnit.SECONDS);
                return microwave;
            }

        };
    }

}
