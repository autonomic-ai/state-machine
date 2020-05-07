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
package com.github.davidmoten.resources.analyzer;

import com.github.davidmoten.fsm.model.State;
import com.github.davidmoten.fsm.model.StateMachineDefinition;
import com.github.davidmoten.resources.analyzer.execution.DownloadQueued;
import com.github.davidmoten.resources.analyzer.execution.DownloadSucceeded;
import com.github.davidmoten.resources.analyzer.execution.DownloadTimeout;
import com.github.davidmoten.resources.analyzer.execution.Downloading;
import com.github.davidmoten.resources.analyzer.execution.Execution;
import com.github.davidmoten.resources.analyzer.execution.Failed;
import com.github.davidmoten.resources.analyzer.execution.Installing;
import com.github.davidmoten.resources.analyzer.execution.Started;
import com.github.davidmoten.resources.analyzer.execution.Succeeded;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class StateMachineDefinitions implements Supplier<List<StateMachineDefinition<?>>> {
    public List<StateMachineDefinition<?>> get() {
        return Arrays.asList(
                createExecutionStateMachine());
    }

    public static StateMachineDefinition<Execution> createExecutionStateMachine() {
        StateMachineDefinition<Execution> m = StateMachineDefinition.create(Execution.class);

        State<Execution, Started> started =
                m.createState("STARTED", Started.class).initial();

        State<Execution, DownloadQueued> downloadQueued =
                m.createState("DOWNLOAD_QUEUED", DownloadQueued.class);

        State<Execution, Downloading> downloading =
                m.createState("DOWNLOADING", Downloading.class);

        State<Execution, DownloadSucceeded> downloadSucceeded =
                m.createState("DOWNLOAD_SUCCEEDED", DownloadSucceeded.class, true);

        State<Execution, DownloadTimeout> downloadTimeout =
                m.createState("DOWNLOAD_TIMEOUT", DownloadTimeout.class);

        State<Execution, Installing> installing =
                m.createState("INSTALLING", Installing.class);

        State<Execution, Failed> failed =
                m.createState("FAILED", Failed.class);
        State<Execution, Succeeded> succeeded =
                m.createState("SUCCEEDED", Succeeded.class);

        started.to(downloadQueued)
                .to(downloading)
                .to(downloadSucceeded)
                .to(installing)
                .to(succeeded);

        downloading.to(downloadTimeout);

        downloadTimeout.to(downloadQueued);

        installing.to(failed);

        return m;
    }
}
