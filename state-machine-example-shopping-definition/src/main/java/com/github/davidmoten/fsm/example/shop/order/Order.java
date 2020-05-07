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
package com.github.davidmoten.fsm.example.shop.order;

import java.math.BigDecimal;
import java.util.List;

public final class Order {

    public final String id;
    public final String customerId;
    public final String address;
    public final String phoneNumber;

    public final List<OrderItem> items;
    public final BigDecimal shippingCost;
    public final BigDecimal discount;

    public Order(String id, String customerId, String address, String phoneNumber, List<OrderItem> items,
            BigDecimal shippingCost, BigDecimal discount) {
        this.id = id;
        this.customerId = customerId;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.items = items;
        this.shippingCost = shippingCost;
        this.discount = discount;
    }

}
