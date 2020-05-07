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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.davidmoten.guavamini.Preconditions;

public final class Property {

    private final String name;
    private final String value;

    private Property(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String name() {
        return name;
    }

    public String value() {
        return value;
    }

    public static Property create(String name, String value) {
        return new Property(name, value);
    }

    public static List<Property> list(String name, Collection<String> values) {
        return values.stream() //
                .map(x -> Property.create(name, x)) //
                .collect(Collectors.toList());
    }

    public static List<Property> list(String... items) {
        Preconditions.checkArgument(items.length % 2 == 0);
        List<Property> list = new ArrayList<>();
        for (int i = 0; i < items.length / 2; i++) {
            list.add(Property.create(items[2 * i], items[2 * i + 1]));
        }
        return list;
    }

    public static List<Property> concatenate(@SuppressWarnings("unchecked") List<Property>... lists) {
        List<Property> list = new ArrayList<>();
        for (List<Property> x : lists) {
            list.addAll(x);
        }
        return list;
    }

    public static String combineNames(String... names) {
        return combineNames(Arrays.stream(names));
    }

    public static String combineNames(List<String> names) {
        return combineNames(names.stream());
    }

    public static String combineNames(Stream<String> names) {
        return names.collect(Collectors.joining("|"));
    }

}
