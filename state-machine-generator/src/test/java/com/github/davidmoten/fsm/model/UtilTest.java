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

import static com.github.davidmoten.fsm.Util.camelCaseToLowerUnderscore;
import static com.github.davidmoten.fsm.Util.toColumnName;
import static org.junit.Assert.assertEquals;

import com.github.davidmoten.fsm.Util;
import com.github.davidmoten.junit.Asserts;
import org.junit.Test;

public class UtilTest {

    @Test
    public void testToTableName() {
        assertEquals("hello_there", camelCaseToLowerUnderscore("HelloThere"));
    }

    @Test
    public void testToTableNameSequenceOfCapitals() {
        assertEquals("hello_there", camelCaseToLowerUnderscore("HelloTHERE"));
    }

    @Test
    public void testToTableNameSequenceOfCapitalsFollowedByLowerCase() {
        assertEquals("hello_there", camelCaseToLowerUnderscore("HelloTHEre"));
    }

    @Test
    public void testToColumnName() {
        assertEquals("a", toColumnName("a"));
        assertEquals("ab", toColumnName("ab"));
        assertEquals("a_b", toColumnName("aB"));
        assertEquals("a_b", toColumnName("a_b"));
        assertEquals("a_b", toColumnName("a b"));
        assertEquals("a_b_c", toColumnName("a b c"));
        assertEquals("a_b_two", toColumnName("A B two"));
    }

    @Test
    public void testToJavaConstantIdentifierEndsWithDigit() {
        assertEquals("STATE1", Util.toJavaConstantIdentifier("State1"));
    }

    @Test
    public void testToJavaConstantIdentifierHasSpaces() {
        String s = Util.toJavaConstantIdentifier("State 1");
        System.out.println(s);
        assertEquals("STATE_1", s);
    }

    @Test
    public void testCamelCaseToUnderscoreForAllCapitalsJustConvertsLowerToUpper() {
        assertEquals("mmsi", Util.camelCaseToLowerUnderscore("MMSI"));
        assertEquals("id", Util.camelCaseToLowerUnderscore("ID"));
        assertEquals("i_d", Util.camelCaseToLowerUnderscore("I D"));
    }

    @Test
    public void isUtilityClass() {
        Asserts.assertIsUtilityClass(Util.class);
    }

}
