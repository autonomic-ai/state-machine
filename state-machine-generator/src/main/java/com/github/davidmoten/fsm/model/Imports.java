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

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

final class Imports {

    private final Map<String, String> map = new HashMap<>();

    public String add(Class<?> cls) {
        return add(cls.getName().replace("$", "."));
    }

    public String add(String className) {
        int i = className.lastIndexOf('.');
        String simpleName;
        if (i == -1)
            simpleName = className;
        else
            simpleName = className.substring(i + 1, className.length());
        String c = map.get(simpleName);
        if (c == null) {
            map.put(simpleName, className);
            return simpleName;
        } else if (c.equals(className)) {
            return simpleName;
        } else {
            return simpleName;
        }
    }

    public String importsAsString() {
        return map.values().stream().sorted().map(c -> "import " + c + ";")
                .collect(Collectors.joining("\n"));
    }

}
