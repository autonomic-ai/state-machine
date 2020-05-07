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
package com.github.davidmoten.fsm.example.shop;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import com.github.davidmoten.fsm.example.shop.basket.immutable.Basket;
import com.github.davidmoten.fsm.example.shop.basket.immutable.Change;
import com.github.davidmoten.fsm.example.shop.basket.immutable.Checkout;
import com.github.davidmoten.fsm.example.shop.basket.immutable.Clear;
import com.github.davidmoten.fsm.example.shop.basket.immutable.Payment;
import com.github.davidmoten.fsm.example.shop.basket.immutable.Timeout;
import com.github.davidmoten.fsm.example.shop.catalog.immutable.Catalog;
import com.github.davidmoten.fsm.example.shop.catalogproduct.immutable.CatalogProduct;
import com.github.davidmoten.fsm.example.shop.product.immutable.Product;
import com.github.davidmoten.fsm.model.State;
import com.github.davidmoten.fsm.model.StateMachineDefinition;
import com.github.davidmoten.fsm.runtime.Create;

public final class StateMachineDefinitions implements Supplier<List<StateMachineDefinition<?>>> {

    @Override
    public List<StateMachineDefinition<?>> get() {
        return Arrays.asList(createBasketStateMachine(), //
                createCatalogStateMachine(), //
                createProductStateMachine(), //
                createCatalogProductStateMachine());

    }

    private static StateMachineDefinition<Basket> createBasketStateMachine() {
        StateMachineDefinition<Basket> m = StateMachineDefinition.create(Basket.class);
        State<Basket, Create> created = m.createState("Created") //
                .event(Create.class)
                .documentation("<pre>onEntry/ \n" //
                        + "send Clear to self \n" //
                        + "</pre>");
        State<Basket, Clear> empty = m.createState("Empty") //
                .event(Clear.class);
        State<Basket, Change> changed = m.createState("Changed") //
                .event(Change.class)
                .documentation("<pre>onEntry/ \n" //
                        + "update changed items \n" //
                        + "if empty then\n" //
                        + "  send Clear to self\n" //
                        + "send Timeout to self in 1 day " //
                        + "</pre>");
        State<Basket, Checkout> checkedOut = m.createState("CheckedOut") //
                .event(Checkout.class) //
                .documentation("<pre>onEntry/\n" //
                        + "send Timeout to self in 1 day " //
                        + "</pre>");
        State<Basket, Payment> paid = m.createState("Paid") //
                .event(Payment.class) //
                .documentation("<pre>onEntry/ \n" //
                        + "create order\n" //
                        + "send order to Order \n" //
                        + "</pre>");
        State<Basket, Timeout> timedOut = m.createState("TimedOut") //
                .event(Timeout.class);
        created.initial() //
                .to(empty //
                        .from(changed) //
                        .from(timedOut //
                                .from(changed) //
                                .from(checkedOut))) //
                .to(changed.from(changed)) //
                .to(checkedOut) //
                .to(paid);
        return m;
    }

    private static StateMachineDefinition<Catalog> createCatalogStateMachine() {
        StateMachineDefinition<Catalog> m = StateMachineDefinition.create(Catalog.class);
        State<Catalog, com.github.davidmoten.fsm.example.shop.catalog.immutable.Create> created = m
                .createState("Created", com.github.davidmoten.fsm.example.shop.catalog.immutable.Create.class);
        State<Catalog, com.github.davidmoten.fsm.example.shop.catalog.immutable.Change> changed = m
                .createState("Changed", com.github.davidmoten.fsm.example.shop.catalog.immutable.Change.class);
        created.initial().to(changed).to(changed);
        return m;
    }

    private static StateMachineDefinition<Product> createProductStateMachine() {
        StateMachineDefinition<Product> m = StateMachineDefinition.create(Product.class);
        State<Product, com.github.davidmoten.fsm.example.shop.product.immutable.Create> created = m
                .createState("Created", com.github.davidmoten.fsm.example.shop.product.immutable.Create.class);
        State<Product, com.github.davidmoten.fsm.example.shop.product.immutable.ChangeDetails> changed = m
                .createState("Changed", com.github.davidmoten.fsm.example.shop.product.immutable.ChangeDetails.class);
        created.initial().to(changed).to(changed);
        return m;
    }

    private static StateMachineDefinition<CatalogProduct> createCatalogProductStateMachine() {
        StateMachineDefinition<CatalogProduct> m = StateMachineDefinition.create(CatalogProduct.class);
        State<CatalogProduct, com.github.davidmoten.fsm.example.shop.catalogproduct.immutable.Create> created = m
                .createState("Created", com.github.davidmoten.fsm.example.shop.catalogproduct.immutable.Create.class);
        State<CatalogProduct, com.github.davidmoten.fsm.example.shop.catalogproduct.immutable.ChangeQuantity> changedQuantity = m
                .createState("ChangedQuantity",
                        com.github.davidmoten.fsm.example.shop.catalogproduct.immutable.ChangeQuantity.class);
        State<CatalogProduct, com.github.davidmoten.fsm.example.shop.catalogproduct.immutable.ChangeProductDetails> changedProductDetails = m
                .createState("ChangedProductDetails",
                        com.github.davidmoten.fsm.example.shop.catalogproduct.immutable.ChangeProductDetails.class);
        created.initial() //
                .to(changedQuantity) //
                .to(changedQuantity) //
                .to(changedProductDetails) //
                .to(changedProductDetails.from(created));

        return m;
    }
}
