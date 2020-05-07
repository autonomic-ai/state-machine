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

        StateMachineDefinition<Execution> stateMachineDefinition = StateMachineDefinitions.createExecutionStateMachine();

        State<Execution, DownloadSucceeded> downloadSucceeded =
            (State<Execution, DownloadSucceeded>) stateMachineDefinition.getState("DOWNLOAD_SUCCEEDED");
        State<Execution, Succeeded> succeeded =
            (State<Execution, Succeeded>) stateMachineDefinition.getState("SUCCEEDED");
        State<Execution, Started> started =
            (State<Execution, Started>) stateMachineDefinition.getState("STARTED");
        State<Execution, Failed> failed =
            (State<Execution, Failed>) stateMachineDefinition.getState("FAILED");

        assert(!downloadSucceeded.isTerminal());
        assert(!started.isTerminal());

        assert(succeeded.isTerminal());
        assert(failed.isTerminal());

        assert(stateMachineDefinition.isReachable("STARTED", "DOWNLOAD_SUCCEEDED"));
        assert(stateMachineDefinition.isReachable("DOWNLOAD_STARTED", "DOWNLOAD_SUCCEEDED"));
        assert(!stateMachineDefinition.isReachable("FAILED", "STARTED"));

        assert(stateMachineDefinition.isTerminal("FAILED"));
        assert(stateMachineDefinition.isTerminal("SUCCEEDED"));
        assert(!stateMachineDefinition.isTerminal("DOWNLOAD_SUCCEEDED"));

        assert(stateMachineDefinition.triggerQueueRemoval("DOWNLOAD_SUCCEEDED"));
        assert(stateMachineDefinition.triggerQueueRemoval("SUCCEEDED"));
        assert(!stateMachineDefinition.triggerQueueRemoval("STARTED"));

        // throws invalid state name exception
        stateMachineDefinition.isReachable("meow", "cookiemonster");


    }

}
