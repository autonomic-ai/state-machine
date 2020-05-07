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
package com.github.davidmoten.fsm.maven;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.github.davidmoten.bean.ImmutableBeanGenerator;

@Mojo(name = "generate-immutable", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class ImmutableBeanGeneratorMojo extends AbstractMojo {
    

    @Parameter(name = "sourceDirectory", required = true)
    File sourceDirectory;
    
    @Parameter(name = "generatedSourceDirectory", required = true)
    File generatedSourceDirectory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        ImmutableBeanGenerator.scanAndGenerate(sourceDirectory, generatedSourceDirectory);
    }

}
