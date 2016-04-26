package com.github.davidmoten.fsm.runtime.rx;

import java.util.HashMap;
import java.util.Map;

import com.github.davidmoten.fsm.runtime.EntityStateMachine;

import rx.functions.Func1;
import rx.functions.Func2;

public class StateMachineFactory<Id> implements Func2<Class<?>, Id, EntityStateMachine<?>> {

    private final Map<Class<?>, Func1<? super Id, EntityStateMachine<?>>> map;

    private StateMachineFactory(Map<Class<?>, Func1<? super Id, EntityStateMachine<?>>> map) {
        this.map = map;
    }

    public static <T, Id> Builder<Id> add(Class<T> cls,
            Func1<? super Id, ? extends EntityStateMachine<T>> factory) {
        return new Builder<Id>().add(cls, factory);
    }

    public static final class Builder<Id> {

        private final Map<Class<?>, Func1<? super Id, EntityStateMachine<?>>> map = new HashMap<>();

        @SuppressWarnings("unchecked")
        public <T> Builder<Id> add(Class<T> cls,
                Func1<? super Id, ? extends EntityStateMachine<T>> factory) {
            map.put(cls, (Func1<? super Id, EntityStateMachine<?>>) (Func1<? super Id, ?>) factory);
            return this;
        }

        public StateMachineFactory<Id> build() {
            return new StateMachineFactory<Id>(map);
        }

    }

    @Override
    public EntityStateMachine<?> call(Class<?> cls, Id id) {
        Func1<? super Id, EntityStateMachine<?>> f = map.get(cls);
        if (f != null) {
            return f.call(id);
        } else {
            throw new RuntimeException("state machine factory not defined for " + cls);
        }
    }

}
