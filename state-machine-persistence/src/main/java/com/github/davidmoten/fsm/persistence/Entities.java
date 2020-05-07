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
package com.github.davidmoten.fsm.persistence;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.github.davidmoten.fsm.persistence.Persistence.EntityAndState;
import com.github.davidmoten.fsm.persistence.Persistence.EntityWithId;
import com.github.davidmoten.fsm.persistence.exceptions.EntitiesNotSetException;

public interface Entities {

    <T> Optional<EntityAndState<T>> getWithState(Class<T> cls, String id);

    <T> Optional<T> get(Class<T> cls, String id);

    <T> List<EntityWithId<T>> get(Class<T> cls);

    <T> Set<EntityWithId<T>> get(Class<T> cls, String name, String value);

    <T> Set<EntityWithId<T>> getOr(Class<T> cls, Iterable<Property> properties);

    <T> Set<EntityWithId<T>> getAnd(Class<T> cls, Iterable<Property> properties);

    <T> List<EntityWithId<T>> get(Class<T> cls, String name, String value, String rangeName, long rangeStart,
            boolean startInclusive, long rangeEnd, boolean endInclusive, int limit, Optional<String> lastId);

    static final ThreadLocal<Entities> current = new ThreadLocal<Entities>();

    public static void set(Entities entities) {
        current.set(entities);
    }

    public static Entities get() {
        Entities e = current.get();
        if (e == null) {
            throw new EntitiesNotSetException();
        }
        return e;
    }

    public static void clear() {
        current.remove();
    }

}
