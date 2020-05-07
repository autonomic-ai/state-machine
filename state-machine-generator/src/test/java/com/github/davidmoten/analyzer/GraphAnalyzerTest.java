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
package com.github.davidmoten.analyzer;

import com.github.davidmoten.exceptions.NoSuchGraphNodeException;
import com.github.davidmoten.fsm.graph.Graph;
import com.github.davidmoten.fsm.graph.GraphAnalyzer;
import com.github.davidmoten.fsm.model.StateMachineDefinition;
import com.github.davidmoten.resources.analyzer.StateMachineDefinitions;
import com.github.davidmoten.resources.analyzer.execution.Execution;
import org.junit.Test;

public class GraphAnalyzerTest {

    @Test
    public void graphAnalyzerTest() {

        StateMachineDefinition<Execution> stateMachineDefinition =
                StateMachineDefinitions.createExecutionStateMachine();

        Graph graph = stateMachineDefinition.getGraph();

        GraphAnalyzer analyzer = new GraphAnalyzer(graph);

        assert (analyzer.isReachable("INSTALLING", "INSTALLING"));
        assert (analyzer.isReachable("STARTED", "SUCCEEDED"));
        assert (!analyzer.isReachable("INSTALLING", "STARTED"));
        assert (analyzer.isReachable("DOWNLOAD_QUEUED", "DOWNLOAD_TIMEOUT"));
        assert (analyzer.isReachable("DOWNLOAD_QUEUED", "SUCCEEDED"));
        assert (!analyzer.isReachable("SUCCEEDED", "DOWNLOAD_QUEUED"));
        // reachable by virtue of failure
        assert (analyzer.isReachable("DOWNLOAD_TIMEOUT", "DOWNLOAD_QUEUED"));
    }

    @Test(expected = NoSuchGraphNodeException.class)
    public void invalidFromStateTest() {

        StateMachineDefinition<Execution> stateMachineDefinition =
                StateMachineDefinitions.createExecutionStateMachine();
        Graph graph = stateMachineDefinition.getGraph();

        GraphAnalyzer analyzer = new GraphAnalyzer(graph);

        // not a valid from state
        analyzer.isReachable("DUMMY_STATE", "INSTALLING");
    }

    @Test(expected = NoSuchGraphNodeException.class)
    public void invalidToStateTest() {

        StateMachineDefinition<Execution> stateMachineDefinition =
                StateMachineDefinitions.createExecutionStateMachine();
        Graph graph = stateMachineDefinition.getGraph();

        GraphAnalyzer analyzer = new GraphAnalyzer(graph);

        // not a valid to state
        analyzer.isReachable("STARTED", "DUMMY_STATE");
    }
}
