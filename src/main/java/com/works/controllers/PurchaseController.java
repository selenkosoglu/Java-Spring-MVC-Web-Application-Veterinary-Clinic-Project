package com.works.controllers;

import com.works.entities.*;
import com.works.entities.projections.PurchaseInfo;
import com.works.properties.PurchaseInterlayer;
import com.works.repositories.ProductRepository;
import com.works.repositories.PurchaseRepository;
import com.works.repositories.SupplierRepository;
import com.works.utils.Util;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/purchase")
public class PurchaseController {

    final ProductRepository productRepository;
    final SupplierRepository supplierRepository;
    final PurchaseRepository purchaseRepository;

    public PurchaseController(ProductRepository productRepository, SupplierRepository supplierRepository, PurchaseRepository purchaseRepository) {
        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
        this.purchaseRepository = purchaseRepository;
    }

    @GetMapping("")
    public String purchase(Model model, PurchaseInterlayer purchaseInterlayer) {
        model.addAttribute("purchaseInterlayer", purchaseInterlayer);
        model.addAttribute("isError", false);
        return "purchase";
    }


    @PostMapping("/insertPurchase")
    public String insertPurchase(@Valid @ModelAttribute("purchaseInterlayer") PurchaseInterlayer purchaseInterlayer, BindingResult bindingResult, Model model) {
        System.out.println("Posta giriyor musun??");
        if (!bindingResult.hasErrors()) {
            Purchase purchase = new Purchase();
            purchase.setPurchase_detail(purchaseInterlayer.getPNote());
            purchase.setPurchase_code(UUID.randomUUID().toString());
            purchase.setPurchase_number(purchaseInterlayer.getPurchase_number());
            purchase.setPurchase_type(purchaseInterlayer.getPurchase_type());
            Optional<Product> optProduct = productRepository.findById(purchaseInterlayer.getProduct_id());
            purchase.setProduct(optProduct.get());
            if (optProduct.isPresent()) {
                optProduct.get().setProduct_stokMiktari(optProduct.get().getProduct_stokMiktari() + purchaseInterlayer.getPurchase_number());
                productRepository.saveAndFlush(optProduct.get());
            }
            Optional<Supplier> optSupplier = supplierRepository.findById(purchaseInterlayer.getSupplier_id());
            purchase.setSupplier(optSupplier.get());
            purchase.setPurchase_total(optProduct.get().getProduct_alis() * purchaseInterlayer.getPurchase_number());


            purchaseRepository.save(purchase);
            return "redirect:/purchase";
        }
        model.addAttribute("isError", true);
        return "purchase";
    }

    @GetMapping("/getSupplierList")
    @ResponseBody
    public List<Supplier> getSupplierList() {
        try {
            return supplierRepository.findAll();
        } catch (Exception e) {
            Util.log("PurchaseController Error : " + e, this.getClass());
        }
        return null;
    }

    @GetMapping("/getProductList")
    @ResponseBody
    public List<Product> getProductList() {
        try {
            return productRepository.findAll();
        } catch (Exception e) {
            Util.log("PurchaseController Error : " + e, this.getClass());
        }
        return null;
    }

    @GetMapping("/getRows")
    @ResponseBody
    public List<PurchaseInfo> getRows() {
        try {
            return purchaseRepository.getRows();
        } catch (Exception e) {
            Util.log("PurchaseController Error : " + e, this.getClass());
        }
        return null;
    }

    @GetMapping("/getRowsSearching/{search}")
    @ResponseBody
    public List<PurchaseInfo> getRows(@PathVariable String search) {
        try {
            return purchaseRepository.getRowsSearching(search);
        } catch (Exception e) {
            Util.log("PurchaseController Error : " + e, this.getClass());
        }
        return null;
    }

    @GetMapping("/delete/{stPurchaseId}")
    @ResponseBody
    public Boolean delete(@PathVariable String stPurchaseId) {
        Boolean feedBack = false;
        try {
            Integer purchaseId = Integer.parseInt(stPurchaseId);
            feedBack = purchaseRepository.existsById(purchaseId);
            if (feedBack == true) {
                Optional<Purchase> optionalPurchase = purchaseRepository.findById(purchaseId);
                if (optionalPurchase.isPresent()) {
                    optionalPurchase.get().getProduct().setProduct_stokMiktari(optionalPurchase.get().getProduct().getProduct_stokMiktari() - optionalPurchase.get().getPurchase_number());

                    productRepository.saveAndFlush(optionalPurchase.get().getProduct());
                }
                purchaseRepository.deleteById(purchaseId);
            }
        } catch (Exception e) {
            Util.log("PurchaseController Error : " + e, this.getClass());
        }
        return feedBack;
    }


}