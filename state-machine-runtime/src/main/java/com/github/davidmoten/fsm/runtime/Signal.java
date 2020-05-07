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
package com.github.davidmoten.fsm.runtime;

import com.github.davidmoten.fsm.runtime.rx.ClassId;
import java.util.Optional;

public final class Signal<T, Id> {

    private final Class<T> cls;
    private final Id id;
    private final Event<? super T> event;
    private final Optional<Long> time;

    private Signal(Class<T> cls, Id id, Event<? super T> event, Optional<Long> time) {
        this.cls = cls;
        this.id = id;
        this.event = event;
        this.time = time;
    }

    public static <T, Id> Signal<T, Id> create(Class<T> cls, Id id, Event<? super T> event) {
        return new Signal<T, Id>(cls, id, event, Optional.empty());
    }

    public static <T, Id> Signal<T, Id> create(Class<T> cls, Id id, Event<? super T> event,
            long time) {
        return new Signal<T, Id>(cls, id, event, Optional.of(time));
    }

    public static <T, Id> Signal<T, Id> create(Class<T> cls, Id id, Event<? super T> event,
            Optional<Long> time) {
        return new Signal<T, Id>(cls, id, event, time);
    }

    public static <T, Id> Signal<T, Id> create(ClassId<T, Id> c, Event<? super T> event,
            Optional<Long> time) {
        return new Signal<T, Id>(c.cls(), c.id(), event, time);
    }

    public static <T, Id> Signal<T, Id> create(ClassId<T, Id> c, Event<? super T> event) {
        return new Signal<T, Id>(c.cls(), c.id(), event, Optional.empty());
    }

    public Class<T> cls() {
        return cls;
    }

    public Event<? super T> event() {
        return event;
    }

    public Id id() {
        return id;
    }

    public Optional<Long> time() {
        return time;
    }

    public boolean isImmediate() {
        return !time.isPresent();
    }

    public Signal<T, Id> now() {
        return create(cls, id, event);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Signal [cls=");
        builder.append(cls);
        builder.append(", id=");
        builder.append(id);
        builder.append(", event=");
        builder.append(event);
        builder.append(", time=");
        builder.append(time.flatMap(x -> Optional.of(x + "")).orElse("empty"));
        builder.append("]");
        return builder.toString();
    }

}
