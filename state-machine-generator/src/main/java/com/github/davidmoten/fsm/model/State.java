package com.github.davidmoten.fsm.model;

import java.io.File;
import java.util.List;
import java.util.Optional;

import com.github.davidmoten.fsm.graph.GraphEdge;
import com.github.davidmoten.fsm.runtime.Event;

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

    public State(StateMachineDefinition<T> m, String name, Class<R> eventClass, boolean triggerQueueRemoval) {
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
     * Some states trigger the removal of commands from the queue even though the state is not
     * the final state in the state machine. All states that are terminal will also trigger queue
     * removal.
     * @return
     */
    public boolean triggerQueueRemoval() {
        return this.isTerminal() || this.triggerQueueRemoval;
    }

    public boolean isTerminal() {
        // If there are no edges extending from this node, this state is terminal
        List<GraphEdge> edges = m.getGraphAnalyzer().getConnectedEdges(name);
        if(edges.size() == 0) {
            return true;
        }

        return false;
    }

    public void generateClasses(File directory, String pkg) {
        m.generateClasses(directory, pkg);
    }

}
