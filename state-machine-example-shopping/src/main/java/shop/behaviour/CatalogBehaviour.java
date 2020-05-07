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
package shop.behaviour;

import com.github.davidmoten.fsm.example.generated.CatalogBehaviourBase;
import com.github.davidmoten.fsm.example.generated.CatalogStateMachine;
import com.github.davidmoten.fsm.example.shop.catalog.immutable.Catalog;
import com.github.davidmoten.fsm.example.shop.catalog.immutable.Change;
import com.github.davidmoten.fsm.example.shop.catalog.immutable.Create;
import com.github.davidmoten.fsm.example.shop.catalogproduct.immutable.CatalogProduct;
import com.github.davidmoten.fsm.example.shop.catalogproduct.immutable.ChangeQuantity;
import com.github.davidmoten.fsm.runtime.Signaller;

public final class CatalogBehaviour extends CatalogBehaviourBase<String> {

    @Override
    public CatalogStateMachine<String> create(String id) {
        return CatalogStateMachine.create(id, this);
    }

    @Override
    public Catalog onEntry_Created(Signaller<Catalog, String> signaller, String id, Create event, boolean replaying) {
        return Catalog //
                .createWithCatalogId(event.catalogId()) //
                .name(event.name());
    }

    @Override
    public Catalog onEntry_Changed(Signaller<Catalog, String> signaller, Catalog catalog, String id, Change event,
            boolean replaying) {
        System.out.println("catalog changed quantity " + event.quantityDelta());
        String cpId = CatalogProduct.idFrom(catalog.catalogId(), event.productId());
        signaller.signal(CatalogProduct.class, cpId,
                com.github.davidmoten.fsm.example.shop.catalogproduct.immutable.Create //
                        .createWithCatalogId(id) //
                        .productId(event.productId()) //
                        .quantity(0) //
                        .price(event.price()));
        signaller.signal(CatalogProduct.class, cpId, ChangeQuantity //
                .createWithQuantityDelta(event.quantityDelta()));
        return catalog;
    }

}
