package com.github.davidmoten.analyzer;

import com.github.davidmoten.exceptions.NoSuchGraphNodeException;
import com.github.davidmoten.fsm.model.StateMachineDefinition;
import com.github.davidmoten.resources.analyzer.StateMachineDefinitions;
import com.github.davidmoten.resources.analyzer.execution.Execution;
import com.github.davidmoten.fsm.graph.Graph;
import com.github.davidmoten.fsm.graph.GraphAnalyzer;

import org.junit.Test;

public class GraphAnalyzerTest {

    @Test
    public void graphAnalyzerTest() {

        StateMachineDefinition<Execution> stateMachineDefinition = StateMachineDefinitions.createExecutionStateMachine();

        Graph graph = stateMachineDefinition.getGraph();

        GraphAnalyzer analyzer = new GraphAnalyzer(graph);

        assert(analyzer.isReachable("INSTALLING", "INSTALLING"));
        assert(analyzer.isReachable("STARTED", "SUCCEEDED"));
        assert(!analyzer.isReachable("INSTALLING", "STARTED"));
        assert(analyzer.isReachable("DOWNLOAD_QUEUED", "DOWNLOAD_TIMEOUT"));
        assert(analyzer.isReachable("DOWNLOAD_QUEUED", "SUCCEEDED"));
        assert(!analyzer.isReachable("SUCCEEDED", "DOWNLOAD_QUEUED"));
        // reachable by virtue of failure
        assert(analyzer.isReachable("DOWNLOAD_TIMEOUT", "DOWNLOAD_QUEUED"));
    }

    @Test(expected = NoSuchGraphNodeException.class)
    public void invalidFromStateTest() {

        StateMachineDefinition<Execution> stateMachineDefinition = StateMachineDefinitions.createExecutionStateMachine();
        Graph graph = stateMachineDefinition.getGraph();

        GraphAnalyzer analyzer = new GraphAnalyzer(graph);

        // not a valid from state
        analyzer.isReachable("DUMMY_STATE", "INSTALLING");
    }

    @Test(expected = NoSuchGraphNodeException.class)
    public void invalidToStateTest() {

        StateMachineDefinition<Execution> stateMachineDefinition = StateMachineDefinitions.createExecutionStateMachine();
        Graph graph = stateMachineDefinition.getGraph();

        GraphAnalyzer analyzer = new GraphAnalyzer(graph);

        // not a valid to state
        analyzer.isReachable("STARTED", "DUMMY_STATE");
    }
}
