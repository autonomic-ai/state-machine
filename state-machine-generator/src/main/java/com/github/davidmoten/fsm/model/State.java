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

import com.github.davidmoten.fsm.graph.GraphEdge;
import com.github.davidmoten.fsm.runtime.Event;
import java.io.File;
import java.util.List;
import java.util.Optional;

public final class State<T, R extends Event<? super T>> {

    private final String name;
    private final Class<R> eventClass;
    private final StateMachineDefinition<T> m;
    private boolean isCreationDestination;
    private boolean triggerQueueRemoval;
    private Optional<String> documentation = Optional.empty();

    public State(StateMachineDefinition<T> m, String name, Class<R> eventClass) {
        this(m, name, eventClass, false);
    }

    public State(StateMachineDefinition<T> m, String name, Class<R> eventClass,
            boolean triggerQueueRemoval) {
        this.m = m;
        this.name = name;
        this.eventClass = eventClass;
        this.triggerQueueRemoval = triggerQueueRemoval;
    }

    public Class<R> eventClass() {
        return eventClass;
    }

    public String name() {
        return name;
    }

    public Optional<String> documentation() {
        return documentation;
    }

    public StateMachineDefinition<?> stateMachine() {
        return m;
    }

    public <S extends Event<? super T>> State<T, S> to(State<T, S> other) {
        m.addTransition(this, other);
        return other;
    }

    public <S extends Event<? super T>> State<T, R> from(State<T, S> other) {
        m.addTransition(other, this);
        return this;
    }

    public State<T, R> initial() {
        isCreationDestination = true;
        m.addInitialTransition(this);
        return this;
    }

    public State<T, R> documentation(String html) {
        this.documentation = Optional.of(html);
        return this;
    }

    public boolean isCreationDestination() {
        return isCreationDestination;
    }

    public boolean isInitial() {
        return name().equals("Initial");
    }

    /**
     * Some states trigger the removal of commands from the queue even though the state is not the
     * final state in the state machine. All states that are terminal will also trigger queue
     * removal.
     * 
     * @return
     */
    public boolean triggerQueueRemoval() {
        return this.isTerminal() || this.triggerQueueRemoval;
    }

    public boolean isTerminal() {
        // If there are no edges extending from this node, this state is terminal
        List<GraphEdge> edges = m.getGraphAnalyzer().getConnectedEdges(name);
        if (edges.size() == 0) {
            return true;
        }

        return false;
    }

    public void generateClasses(File directory, String pkg) {
        m.generateClasses(directory, pkg);
    }

}
