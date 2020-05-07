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
package com.github.davidmoten.fsm.runtime.rx;

import com.github.davidmoten.fsm.runtime.EntityBehaviour;
import io.reactivex.functions.Function;
import java.util.HashMap;
import java.util.Map;

public class StateMachineFactory<Id> implements Function<Class<?>, EntityBehaviour<?, Id>> {

    private final Map<Class<?>, Function<Class<?>, EntityBehaviour<?, Id>>> map;

    private StateMachineFactory(Map<Class<?>, Function<Class<?>, EntityBehaviour<?, Id>>> map) {
        this.map = map;
    }

    public static <T> Builder2<T> cls(Class<T> cls) {
        return new Builder2<T>(cls, new Builder<Object>());
    }

    public static final class Builder2<T> {

        private final Class<T> cls;
        private final Builder<Object> builder;

        private Builder2(Class<T> cls, Builder<Object> builder) {
            this.cls = cls;
            this.builder = builder;
        }

        @SuppressWarnings("unchecked")
        public <Id> Builder<Id> hasFactory(
                Function<Class<?>, ? extends EntityBehaviour<T, Id>> factory) {
            return ((Builder<Id>) builder).add(cls, factory);
        }
    }

    public static final class Builder<Id> {

        private final Map<Class<?>, Function<Class<?>, EntityBehaviour<?, Id>>> map =
                new HashMap<>();

        private Builder() {
            // prevent instantiation publicly
        }

        @SuppressWarnings("unchecked")
        private <T> Builder<Id> add(Class<T> cls,
                Function<Class<?>, ? extends EntityBehaviour<T, Id>> factory) {
            map.put(cls,
                    (Function<Class<?>, EntityBehaviour<?, Id>>) (Function<? super Id, ?>) factory);
            return this;
        }

        @SuppressWarnings("unchecked")
        public <T> Builder2<T> cls(Class<T> cls) {
            return new Builder2<T>(cls, (Builder<Object>) this);
        }

        public StateMachineFactory<Id> build() {
            return new StateMachineFactory<Id>(map);
        }

    }

    @Override
    public EntityBehaviour<?, Id> apply(Class<?> cls) throws Exception {
        Function<Class<?>, EntityBehaviour<?, Id>> f = map.get(cls);
        if (f != null) {
            return f.apply(cls);
        } else {
            throw new RuntimeException("state machine factory not defined for " + cls);
        }
    }

}
