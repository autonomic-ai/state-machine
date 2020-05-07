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
package com.github.davidmoten.fsm.model;

import com.github.davidmoten.exceptions.InvalidStateNameException;
import com.github.davidmoten.resources.analyzer.StateMachineDefinitions;
import com.github.davidmoten.resources.analyzer.execution.DownloadSucceeded;
import com.github.davidmoten.resources.analyzer.execution.Execution;
import com.github.davidmoten.resources.analyzer.execution.Failed;
import com.github.davidmoten.resources.analyzer.execution.Started;
import com.github.davidmoten.resources.analyzer.execution.Succeeded;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class StateMachineTest {

    @Test(expected = InvalidStateNameException.class)
    public void stateMachineTerminalTest() {

        StateMachineDefinition<Execution> stateMachineDefinition =
                StateMachineDefinitions.createExecutionStateMachine();

        State<Execution, DownloadSucceeded> downloadSucceeded =
                (State<Execution, DownloadSucceeded>) stateMachineDefinition
                        .getState("DOWNLOAD_SUCCEEDED");
        State<Execution, Succeeded> succeeded =
                (State<Execution, Succeeded>) stateMachineDefinition.getState("SUCCEEDED");
        State<Execution, Started> started =
                (State<Execution, Started>) stateMachineDefinition.getState("STARTED");
        State<Execution, Failed> failed =
                (State<Execution, Failed>) stateMachineDefinition.getState("FAILED");

        assert (!downloadSucceeded.isTerminal());
        assert (!started.isTerminal());

        assert (succeeded.isTerminal());
        assert (failed.isTerminal());

        assert (stateMachineDefinition.isReachable("STARTED", "DOWNLOAD_SUCCEEDED"));
        assert (stateMachineDefinition.isReachable("DOWNLOAD_STARTED", "DOWNLOAD_SUCCEEDED"));
        assert (!stateMachineDefinition.isReachable("FAILED", "STARTED"));

        assert (stateMachineDefinition.isTerminal("FAILED"));
        assert (stateMachineDefinition.isTerminal("SUCCEEDED"));
        assert (!stateMachineDefinition.isTerminal("DOWNLOAD_SUCCEEDED"));

        assert (stateMachineDefinition.triggerQueueRemoval("DOWNLOAD_SUCCEEDED"));
        assert (stateMachineDefinition.triggerQueueRemoval("SUCCEEDED"));
        assert (!stateMachineDefinition.triggerQueueRemoval("STARTED"));

        // throws invalid state name exception
        stateMachineDefinition.isReachable("meow", "cookiemonster");


    }

}
