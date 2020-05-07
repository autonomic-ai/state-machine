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
package shop;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.davidmoten.fsm.example.shop.catalog.immutable.Catalog;
import com.github.davidmoten.fsm.example.shop.catalogproduct.immutable.CatalogProduct;
import com.github.davidmoten.fsm.example.shop.product.immutable.ChangeDetails;
import com.github.davidmoten.fsm.example.shop.product.immutable.Product;
import com.github.davidmoten.fsm.persistence.Property;

@Controller
public class ShopController {

    @Autowired
    private PersistenceService persistence;

    @RequestMapping("/catalogs")
    public String catalogs(Model model) {
        model.addAttribute("catalogs", persistence.get().get(Catalog.class));
        return "catalogs";
    }

    @RequestMapping("/catalog/{catalogId}/products")
    public String catalogProducts(@PathVariable("catalogId") String catalogId, Model model) {
        model.addAttribute("catalogProducts", persistence.get() //
                .getOr(CatalogProduct.class, Property.list("catalogId", catalogId)));
        return "catalogProducts";
    }

    @RequestMapping("/product/{productId}")
    public String product(@PathVariable("productId") String productId, Model model) {
        model.addAttribute("product", persistence.get() //
                .get(Product.class, productId).get());
        return "product";
    }

    @RequestMapping("/product/{productId}/edit")
    public String editProduct(@PathVariable("productId") String productId, Model model) {
        model.addAttribute("product", persistence.get() //
                .get(Product.class, productId).get());
        return "productEdit";
    }

    @RequestMapping(method=RequestMethod.POST, value="/product/{productId}/save")
    public String saveProduct(@PathVariable("productId") String productId, //
            @RequestParam("name") String name, @RequestParam("description") String description, Model model) {
        persistence.get().signal(Product.class, productId, //
                ChangeDetails //
                        .createWithName(name) //
                        .description(description) //
                        .tags(Collections.emptyList()));
        // sending signal is async so don't do a lookup straight away
        model.addAttribute("product", //
                Product. //
                        createWithProductId(productId) //
                        .name(name) //
                        .description(description) //
                        .tags(Collections.emptyList()));
        return "product";
    }

    @RequestMapping("/catalog/{catalogId}/products/search")
    public String catalogProductsRange(@PathVariable("catalogId") String catalogId, @RequestParam("name") String name,
            @RequestParam("value") String value, @RequestParam("rangeName") List<String> rangeNames,
            @RequestParam("start") int start, @RequestParam("end") int end, @RequestParam("limit") int limit,
            @RequestParam Optional<String> lastId, Model model) {
        model.addAttribute("catalogProducts", persistence.get() //
                .get(CatalogProduct.class, name, value, Property.combineNames(rangeNames), start, true, end, false,
                        limit, lastId));
        return "catalogProducts";
    }

}
