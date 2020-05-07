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

import java.util.List;
import java.util.Optional;

public interface EntityStateMachine<T, Id> extends ObjectState<T> {

    boolean transitionOccurred();

    Optional<? extends EntityState<T>> previousState();

    EntityStateMachine<T, Id> signal(Event<? super T> event);

    List<Event<? super T>> signalsToSelf();

    List<Signal<?, ?>> signalsToOther();

    Class<T> cls();

    EntityStateMachine<T, Id> withSearch(Search<Id> search);

    EntityStateMachine<T, Id> withClock(Clock clock);

    Clock clock();

    Optional<Event<? super T>> event();

    Id id();

    EntityStateMachine<T, Id> replaying();

    EntityStateMachine<T, Id> withPreTransition(
            Action3<? super EntityStateMachine<T, Id>, ? super Event<? super T>, ? super EntityState<T>> action);

}
