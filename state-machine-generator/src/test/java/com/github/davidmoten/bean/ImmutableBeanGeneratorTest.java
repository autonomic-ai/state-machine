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
package com.github.davidmoten.bean;

import static org.junit.Assert.assertTrue;

import com.github.davidmoten.bean.annotation.GenerateImmutable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.junit.Assert;
import org.junit.Test;

public final class ImmutableBeanGeneratorTest {

    @Test
    public void testAnnotationFound() {
        Assert.assertTrue(Example.class.isAnnotationPresent(GenerateImmutable.class));
    }

    /**
     * @throws IOException
     */
    @Test
    public void testJavaParser() throws IOException {
        String code = new String(
                Files.readAllBytes(
                        new File("src/test/java/com/github/davidmoten/bean/Example.java").toPath()),
                StandardCharsets.UTF_8);
        System.out.println(ImmutableBeanGenerator.generate(code).generatedCode());
    }

    @Test
    public void testScanAndGenerate() {
        ImmutableBeanGenerator.scanAndGenerate(new File("src/test/java"), new File("target/gen"));
        assertTrue(
                new File("target/gen/com/github/davidmoten/bean/immutable/Example.java").exists());
    }
}
