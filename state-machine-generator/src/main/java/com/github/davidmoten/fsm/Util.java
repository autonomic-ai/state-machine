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
package com.github.davidmoten.fsm;

import com.github.davidmoten.guavamini.annotations.VisibleForTesting;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class Util {

    private Util() {
        // prevent instantiation
    }

    public static String lowerFirst(String s) {
        return s.substring(0, 1).toLowerCase() + s.substring(1);
    }

    public static String upperFirst(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    public static String toClassSimpleName(String name) {
        return upperFirst(toJavaIdentifier(name));
    }

    public static String toJavaIdentifier(String name) {

        StringBuilder s = new StringBuilder();
        boolean capitalize = false;
        for (int i = 0; i < name.length(); i++) {
            char ch = name.charAt(i);
            if ((i != 0 && !Character.isJavaIdentifierStart(ch))
                    || !Character.isJavaIdentifierPart(ch)) {
                capitalize = true;
            } else if (capitalize) {
                s.append(Character.toUpperCase(ch));
                capitalize = false;
            } else
                s.append(ch);
        }
        return lowerFirst(s.toString());
    }

    public static String toJavaConstantIdentifier(String name) {
        StringBuilder s = new StringBuilder();
        boolean funnyCharacter = false;
        for (int i = 0; i < name.length(); i++) {
            char ch = name.charAt(i);
            if ((i == 0 && !Character.isJavaIdentifierStart(ch))
                    || (i > 0 && !Character.isJavaIdentifierPart(ch))) {
                funnyCharacter = true;
            } else if (funnyCharacter) {
                s.append("_");
                s.append(Character.toUpperCase(ch));
                funnyCharacter = false;
            } else
                s.append(Character.toUpperCase(ch));
        }
        return s.toString();
    }

    @VisibleForTesting
    static boolean isLettersAndDigits(String s) {
        return Pattern.compile("[0-9a-zA-Z]*").matcher(s).matches();
    }

    public static String camelCaseToLowerUnderscore(String s) {
        if (s.toUpperCase().equals(s) && isLettersAndDigits(s)) {
            return s.toLowerCase();
        }

        StringBuilder b = new StringBuilder();
        b.append(s.charAt(0));
        boolean underscoreAdded = false;
        boolean lastCharacterUppercase = false;
        for (int i = 1; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (!Character.isLetterOrDigit(ch)) {
                if (!underscoreAdded)
                    b.append('_');
                underscoreAdded = true;
                lastCharacterUppercase = false;
            } else if (Character.isUpperCase(ch)) {
                if (!underscoreAdded && !lastCharacterUppercase) {
                    b.append("_");
                }
                b.append(ch);
                underscoreAdded = false;
                lastCharacterUppercase = true;
            } else {
                b.append(ch);
                underscoreAdded = false;
                lastCharacterUppercase = false;
            }
        }
        return b.toString().toLowerCase();
    }

    public static String toTableName(String className) {
        return camelCaseToLowerUnderscore(className);
    }

    public static String toTableIdName(String className) {
        return toTableName(className) + "_id";
    }

    public static String toColumnName(String attributeName) {
        return camelCaseToLowerUnderscore(attributeName);
    }

    public static String getPackage(String className) {
        if (!className.contains("."))
            return className;
        else
            return className.substring(0, className.lastIndexOf("."));
    }

    public static String getSimpleClassName(String className) {
        if (!className.contains("."))
            return className;
        else
            return className.substring(className.lastIndexOf(".") + 1, className.length());
    }

    public static String camelCaseToSpaced(String s) {
        return s.chars().mapToObj(ch -> {
            if (ch >= 'A' && ch <= 'Z') {
                return " " + (char) ch;
            } else {
                return "" + (char) ch;
            }
        }).collect(Collectors.joining(""));
    }

}
