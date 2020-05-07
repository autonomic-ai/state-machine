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

import java.util.Optional;

import com.github.davidmoten.fsm.example.generated.CatalogProductBehaviourBase;
import com.github.davidmoten.fsm.example.generated.CatalogProductStateMachine;
import com.github.davidmoten.fsm.example.shop.catalogproduct.immutable.CatalogProduct;
import com.github.davidmoten.fsm.example.shop.catalogproduct.immutable.ChangeProductDetails;
import com.github.davidmoten.fsm.example.shop.catalogproduct.immutable.ChangeQuantity;
import com.github.davidmoten.fsm.example.shop.catalogproduct.immutable.Create;
import com.github.davidmoten.fsm.example.shop.product.immutable.Product;
import com.github.davidmoten.fsm.persistence.Entities;
import com.github.davidmoten.fsm.runtime.Signaller;

public final class CatalogProductBehaviour extends CatalogProductBehaviourBase<String> {

    @Override
    public CatalogProductStateMachine<String> create(String id) {
        return CatalogProductStateMachine.create(id, this);
    }

    @Override
    public CatalogProduct onEntry_Created(Signaller<CatalogProduct, String> signaller, String id, Create event,
            boolean replaying) {
        System.out.println("creating catalogproduct");
        // lookup product within the transaction
        Optional<Product> product = Entities.get().get(Product.class, event.productId());
        if (product.isPresent()) {
            return CatalogProduct.createWithCatalogId(event.catalogId()) //
                    .productId(event.productId()) //
                    .name(product.get().name()) //
                    .description(product.get().description()) //
                    .quantity(event.quantity()) //
                    .price(event.price()) //
                    .tags(product.get().tags());
        } else {
            throw new RuntimeException("product not found " + event.productId());
        }
    }

    @Override
    public CatalogProduct onEntry_ChangedQuantity(Signaller<CatalogProduct, String> signaller, CatalogProduct c,
            String id, ChangeQuantity event, boolean replaying) {
        System.out.println("changing quantity catalogproduct");
        CatalogProduct result = c.withQuantity(c.quantity() + event.quantityDelta());
        return result;
    }

    @Override
    public CatalogProduct onEntry_ChangedProductDetails(Signaller<CatalogProduct, String> signaller, CatalogProduct c,
            String id, ChangeProductDetails event, boolean replaying) {
        return c //
                .withName(event.productName()) //
                .withDescription(event.productDescription()) //
                .withTags(event.tags());
    }

}
