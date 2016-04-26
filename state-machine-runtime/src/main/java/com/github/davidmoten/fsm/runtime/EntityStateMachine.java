package com.github.davidmoten.fsm.runtime;

import java.util.List;

public interface EntityStateMachine<T> extends ObjectState<T> {

    boolean transitionOccurred();

    EntityStateMachine<T> signal(Event<?> event);

    List<Event<?>> signalsToSelf();

    List<Signal<?, ?>> signalsToOther();

    Class<T> cls();
}
