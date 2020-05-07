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
package com.github.davidmoten.fsm.example.microwave;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class Microwave {

    private final int serialNumber;

    @JsonCreator
    public Microwave(@JsonProperty("serialNumber") int serialNumber) {
        this.serialNumber = serialNumber;
    }

    public int serialNumber() {
        return serialNumber;
    }

    public static String idFromSerialNumber(int n) {
        return String.valueOf(n);
    }

    public static Microwave fromId(String id) {
        return new Microwave(Integer.parseInt(id));
    }

    @Override
    public String toString() {
        return "Microwave [serialNumber=" + serialNumber + "]";
    }

}
