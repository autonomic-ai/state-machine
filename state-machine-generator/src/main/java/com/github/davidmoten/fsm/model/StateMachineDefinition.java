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

import static com.github.davidmoten.fsm.Util.camelCaseToSpaced;

import com.github.davidmoten.bean.annotation.GenerateImmutable;
import com.github.davidmoten.exceptions.InvalidStateNameException;
import com.github.davidmoten.fsm.graph.Graph;
import com.github.davidmoten.fsm.graph.GraphAnalyzer;
import com.github.davidmoten.fsm.graph.GraphEdge;
import com.github.davidmoten.fsm.graph.GraphNode;
import com.github.davidmoten.fsm.graph.GraphmlWriter;
import com.github.davidmoten.fsm.graph.NodeOptions;
import com.github.davidmoten.fsm.runtime.Event;
import com.github.davidmoten.fsm.runtime.EventVoid;
import com.github.davidmoten.guavamini.Preconditions;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class StateMachineDefinition<T> {

    private final Class<T> cls;
    private final List<Transition<T, ? extends Event<? super T>, ? extends Event<? super T>>> transitions =
            new ArrayList<>();
    private final Set<State<T, ? extends Event<? super T>>> states = new HashSet<>();
    private final State<T, EventVoid> initialState;
    private Graph graph;
    private GraphAnalyzer graphAnalyzer;

    private StateMachineDefinition(Class<T> cls) {
        Preconditions.checkArgument(!cls.isAnnotationPresent(GenerateImmutable.class),
                "cannot base a state machine definition on a class that is annotated with @GenerateImmutable, use the generated immutable class instead");
        this.cls = cls;
        this.initialState = new State<T, EventVoid>(this, "Initial", EventVoid.class);
    }

    public static <T> StateMachineDefinition<T> create(Class<T> cls) {
        return new StateMachineDefinition<T>(cls);
    }

    public Class<T> cls() {
        return cls;
    }

    /**
     * Method for building a State and adding it to this State Machine Definition.
     * 
     * @param name - The name of the state
     * @param eventClass - The class this event belongs to
     * @param triggerQueueRemoval - Whether this state triggers removal of the command from queue,
     *        regardless of whether it is a terminal state.
     * @return The created state
     */
    public <R extends Event<? super T>> State<T, R> createState(String name, Class<R> eventClass,
            boolean triggerQueueRemoval) {
        Preconditions.checkArgument(!eventClass.isAnnotationPresent(GenerateImmutable.class),
                "cannot base a state on an event that is annotated with @GenerateImmutable, use the generated immutable class instead");
        Preconditions.checkNotNull(name);
        if (name.equals("Initial")) {
            name = name.concat("_1");
        }
        State<T, R> state = new State<T, R>(this, name, eventClass, triggerQueueRemoval);
        states.add(state);
        return state;
    }

    /**
     * Method for building a State and adding it to this State Machine Definition.
     * 
     * @param name - The name of the state
     * @param eventClass - The class this event belongs to
     * @return The created state
     */
    public <R extends Event<? super T>> State<T, R> createState(String name, Class<R> eventClass) {
        return this.createState(name, eventClass, false);
    }

    public boolean triggerQueueRemoval(String stateName) {
        State state = this.getState(stateName);

        if (state == null) {
            throw new InvalidStateNameException("Invalid state name: " + stateName);
        }

        return state.triggerQueueRemoval();
    }

    public final State<T, ? extends Event<? super T>> getState(String name) {
        return states.stream()
                .filter(s -> s.name().equals(name))
                .findFirst()
                .orElse(null);
    }

    public StateBuilder createState(String name) {
        return new StateBuilder(name);
    }

    public final class StateBuilder {

        final String name;

        StateBuilder(String name) {
            this.name = name;
        }

        /**
         * Sets the event type used to transition <i>to</i> this state.
         * 
         * @param cls the type of event
         * @return the {@code State}
         */
        public <R extends Event<? super T>> State<T, R> event(Class<R> cls) {
            return createState(name, cls);
        }
    }

    public <R extends Event<? super T>, S extends Event<? super T>> StateMachineDefinition<T> addTransition(
            State<T, R> state, State<T, S> other) {
        Transition<T, R, S> transition = new Transition<T, R, S>(state, other);
        for (Transition<T, ?, ?> t : transitions) {
            if (t.from() == state && t.to() == other) {
                throw new IllegalArgumentException(
                        "the transition already exists: " + state.name() + " -> " + other.name());
            }
        }
        transitions.add(transition);
        return this;
    }

    <S extends Event<? super T>> StateMachineDefinition<T> addInitialTransition(State<T, S> other) {
        Transition<T, EventVoid, S> transition =
                new Transition<T, EventVoid, S>(initialState, other);
        transitions.add(transition);
        states.add(initialState);
        states.add(other);
        return this;
    }

    public void generateClasses(File directory, String pkg) {
        new Generator<T>(this, directory, pkg).generate();
    }

    public List<Transition<T, ? extends Event<? super T>, ? extends Event<? super T>>> transitions() {
        return transitions;
    }

    public String documentationHtml() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintWriter out = new PrintWriter(bytes);
        out.println("<html/>");
        out.println("<head>");
        out.println("<style>");
        out.println("table {border-collapse: collapse;}\n"
                + "table, th, td {border: 1px solid black;}");
        out.println(".transition {background-color: #ADE2A7}");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        // states
        out.println("<h2>States</h2>");
        Comparator<State<T, ?>> comparator = (a, b) -> a.name().compareTo(b.name());
        states.stream().sorted(comparator)
                .forEach(state -> out.println("<p class=\"state\"><b>" + state.name()
                        + "</b> [" + state.eventClass().getSimpleName() + "]</p>"
                        + state.documentation().orElse("")));

        // events
        out.println("<h2>Events</h2>");
        states.stream().filter(state -> !state.isInitial())
                .map(state -> state.eventClass().getSimpleName()).distinct()
                .sorted()
                .forEach(event -> out
                        .println("<p class=\"event\"><i>" + camelCaseToSpaced(event) + "</i></p>"));

        // transition table
        // state onEntry template

        out.println("<h2>Transitions</h2>");
        out.println("<table>");
        out.print("<tr><th/>");
        states.stream().sorted(comparator).forEach(state -> {
            out.print("<th>" + state.name() + "</th>");
        });
        out.println("</tr>");
        states.stream().sorted(comparator).forEach(state -> {
            out.print("<tr><th>" + state.name() + "</th>");
            states.stream().sorted(comparator).forEach(st -> {
                boolean hasTransition = transitions.stream()
                        .anyMatch(t -> t.from().name().equals(state.name())
                                && t.to().name().equals(st.name()));
                if (hasTransition) {
                    out.print(
                            "<td class=\"transition\">"
                                    + camelCaseToSpaced(st.eventClass().getSimpleName()) + "</td>");
                } else {
                    out.print("<td></td>");
                }
            });
            out.println("</tr>");
        });
        out.println("</table>");

        out.println("</body>");
        out.println("</html>");
        out.close();
        return new String(bytes.toByteArray(), StandardCharsets.UTF_8);
    }

    public String graphml(Function<GraphNode, NodeOptions> options, boolean includeDocumentation) {
        final Graph graph = this.getGraph();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintWriter out = new PrintWriter(bytes);
        new GraphmlWriter().printGraphml(out, graph, options, includeDocumentation);
        return new String(bytes.toByteArray(), StandardCharsets.UTF_8);
    }

    public boolean hasCreationTransition() {
        return transitions().stream().filter(t -> t.from().isCreationDestination()).findAny()
                .isPresent();
    }

    /**
     * Get the Graph representation of this state machine definition
     * 
     * @return A Graph generated from this StateMachineDefinition
     */
    public synchronized final Graph getGraph() {
        if (this.graph == null) {
            this.graph = this.buildGraph();
        }

        return this.graph;
    }

    /**
     * Get a GraphAnalyzer for this state machine definition
     * 
     * @return
     */
    public synchronized final GraphAnalyzer getGraphAnalyzer() {
        if (this.graphAnalyzer == null) {
            this.graphAnalyzer = new GraphAnalyzer(getGraph());
        }
        return this.graphAnalyzer;
    }

    public boolean isReachable(String fromStateName, String toStateName)
            throws InvalidStateNameException {
        try {
            return this.getGraphAnalyzer().isReachable(fromStateName, toStateName);
        } catch (Exception e) {
            throw new InvalidStateNameException(
                    "Invalid state(s) passed: " + fromStateName + ", " + toStateName);
        }
    }

    public boolean isTerminal(String stateName) throws InvalidStateNameException {

        State state = this.getState(stateName);

        if (state == null) {
            throw new InvalidStateNameException("Invalid state name: " + stateName);
        }

        return state.isTerminal();
    }

    private Graph buildGraph() {
        List<GraphNode> nodes = states.stream().map(GraphNode::new).collect(Collectors.toList());
        Map<String, GraphNode> map = nodes.stream()
                .collect(Collectors.toMap(node -> node.state().name(), node -> node));
        List<GraphEdge> edges = transitions.stream().map(t -> {
            GraphNode from = map.get(t.from().name());
            GraphNode to = map.get(t.to().name());
            return new GraphEdge(from, to, t.to().eventClass().getSimpleName());
        }).collect(Collectors.toList());
        return new Graph(nodes, edges);
    }
}
