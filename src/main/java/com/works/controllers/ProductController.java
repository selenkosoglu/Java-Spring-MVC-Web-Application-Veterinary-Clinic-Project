package com.works.controllers;

import com.works.entities.Category;
import com.works.entities.Product;
import com.works.properties.ProductInterlayer;
import com.works.repositories.CategoryRepository;
import com.works.repositories.ProductRepository;
import com.works.utils.Util;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/product")
public class ProductController {

    final ProductRepository productRepository;
    final CategoryRepository categoryRepository;

    public ProductController(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }


    @GetMapping("")
    public String product(Model model, ProductInterlayer productInterlayer) {
        model.addAttribute("isValidProduct", false);
        model.addAttribute("isError", false);
        model.addAttribute("productInterlayer", productInterlayer);
        model.addAttribute("categoryList", categoryRepository.findAll());
        return "product";
    }

    Product productUpdate = new Product();
    Integer processType = null;

    @PostMapping("/insert")
    public String productInsert(@Valid @ModelAttribute("productInterlayer") ProductInterlayer productInterlayer, BindingResult bindingResult, Model model) {
        boolean isValidProduct = false;
        System.out.println("pro " + productInterlayer);
        if (!bindingResult.hasErrors()) {
            System.out.println("if ????? if if if");
            try {
                System.out.println("trya girdin mi");
                Product product = new Product();
                product.setProduct_name(productInterlayer.getProduct_name());
                product.setProduct_unit(productInterlayer.getProduct_unit());
                product.setProduct_kdv(productInterlayer.getProduct_kdv());
                product.setProduct_alis(productInterlayer.getProduct_alis());
                product.setProduct_satis(productInterlayer.getProduct_satis());
                product.setProduct_stokMiktari(productInterlayer.getProduct_stokMiktari());
                product.setProduct_statu(productInterlayer.getProduct_statu());
                Optional<Category> optionalCategory = categoryRepository.findById(productInterlayer.getCategory());
                if (optionalCategory.isPresent()) {
                    product.setCategory(optionalCategory.get());
                }
                Boolean temp = product.getProduct_statu() == null ? true : false;
                product.setProduct_statu(temp);
                if (processType == 1) {
                    productUpdate = new Product();
                } else {
                    product.setProduct_id(productUpdate.getProduct_id());
                }
                System.out.println("SAVE??????");
                productRepository.saveAndFlush(product);
                System.out.println("FLUSH??????");
                productUpdate = new Product();
                return "redirect:/product";
            } catch (DataIntegrityViolationException ex) {
                Util.log("DataIntegrityViolationException Error : " + ex, this.getClass());
                isValidProduct = true;
            } catch (Exception ex) {
                Util.log("productInsert Error : " + ex, this.getClass());
            }
        }
        model.addAttribute("isValidProduct", isValidProduct);
        model.addAttribute("isError", true);
        model.addAttribute("categoryList", categoryRepository.findAll());
        return "product";
    }

    @GetMapping("/getAllProduct")
    @ResponseBody
    public List<Product> getAllProduct() {
        return productRepository.findAll();
    }

    @GetMapping("/update/{index}")
    @ResponseBody
    public List<Object> updateProduct(@PathVariable String index) {
        List<Object> info = new ArrayList<>();
        try {
            Integer productId = Integer.parseInt(index);

            if (productId > 0) {
                productUpdate = productRepository.findById(productId).get();
                info.add(true);
                info.add(productUpdate);
            } else {
                info.add(false);
            }
        } catch (Exception ex) {
            Util.log("updateProduct Error : " + ex, this.getClass());
        }
        return info;
    }

    @GetMapping("/search/{strSearch}")
    @ResponseBody
    public List<Product> getProductSearch(@PathVariable String strSearch) {
        try {
            return productRepository.findByProduct_nameContainsAllIgnoreCase(strSearch.trim());
        } catch (Exception ex) {
            Util.log("productSearch Error : " + ex, this.getClass());
        }
        return null;
    }

    @GetMapping("/delete/{index}")
    @ResponseBody
    public Boolean deleteProduct(@PathVariable String index) {
        try {
            Integer productId = Integer.parseInt(index);
            if (productId > 0) {
                productRepository.deleteById(productId);
                productUpdate = new Product();
            }
        } catch (Exception ex) {
            Util.log("deleteProduct Error : " + ex, this.getClass());
        }
        return true;
    }

    @GetMapping("/processtype/{strIndex}")
    @ResponseBody
    public boolean processType(@PathVariable String strIndex) {
        try {
            Integer index = Integer.parseInt(strIndex);
            if (index != 3) {
                processType = index;
            }
        } catch (Exception ex) {
            Util.log("processTypeError Error : " + ex, this.getClass());
        }
        return true;
    }


}