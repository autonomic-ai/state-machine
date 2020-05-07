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

import java.util.Set;

import com.github.davidmoten.fsm.example.generated.ProductBehaviourBase;
import com.github.davidmoten.fsm.example.generated.ProductStateMachine;
import com.github.davidmoten.fsm.example.shop.catalogproduct.immutable.CatalogProduct;
import com.github.davidmoten.fsm.example.shop.catalogproduct.immutable.ChangeProductDetails;
import com.github.davidmoten.fsm.example.shop.product.immutable.ChangeDetails;
import com.github.davidmoten.fsm.example.shop.product.immutable.Create;
import com.github.davidmoten.fsm.example.shop.product.immutable.Product;
import com.github.davidmoten.fsm.persistence.Entities;
import com.github.davidmoten.fsm.persistence.Persistence.EntityWithId;
import com.github.davidmoten.fsm.persistence.Property;
import com.github.davidmoten.fsm.runtime.Signaller;

public final class ProductBehaviour extends ProductBehaviourBase<String> {

    @Override
    public ProductStateMachine<String> create(String id) {
        return ProductStateMachine.create(id, this);
    }

    @Override
    public Product onEntry_Created(Signaller<Product, String> signaller, String id, Create event, boolean replaying) {
        return Product //
                .createWithProductId(event.productId()) //
                .name(event.name()) //
                .description(event.description()) //
                .tags(event.tags());
    }

    @Override
    public Product onEntry_Changed(Signaller<Product, String> signaller, Product product, String id,
            ChangeDetails event, boolean replaying) {
        // do an index-based search (using entity properties set by
        // propertiesFactory)
        Set<EntityWithId<CatalogProduct>> set = Entities.get() //
                .getOr(CatalogProduct.class, //
                        Property.list("productId", product.productId()));
        System.out.println(set);
        for (EntityWithId<CatalogProduct> cp : set) {
            signaller.signal(CatalogProduct.class, //
                    cp.id, //
                    ChangeProductDetails //
                            .createWithProductName(event.name()) //
                            .productDescription(event.description()) //
                            .tags(event.tags()));
        }
        return Product //
                .createWithProductId(product.productId()) //
                .name(event.name()) //
                .description(event.description()).tags(event.tags());
    }
}
