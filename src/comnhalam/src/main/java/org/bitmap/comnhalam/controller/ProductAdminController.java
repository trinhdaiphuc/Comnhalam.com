package org.bitmap.comnhalam.controller;

import org.bitmap.comnhalam.model.Product;
import org.bitmap.comnhalam.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ProductAdminController {

    @Autowired
    private ProductRepository productRepository;

    @RequestMapping("/admin/products")
    public String listProducts(Model model) {
        model.addAttribute("products", productRepository.findAll());
        return "admin/products";
    }

    @RequestMapping("/admin/active-product/{id}/{checked}")
    @ResponseBody
    public String activeProduct(@PathVariable("id") String id,
                                @PathVariable("checked") String checked) {
        Long nId = null;

        try {
            nId = Long.parseUnsignedLong(id);
        } catch (NumberFormatException e) {
            return e.getMessage();
        }

        try {
            Product product = productRepository.findById(nId).orElseThrow(() -> new Exception("Product not found"));
            if(checked.equals("yes")) {
                product.setEnabled(true);
            } else {
                product.setEnabled(false);
            }
            productRepository.save(product);
        } catch (Exception e) {
            return e.getMessage();
        }

        return "success";
    }

}
