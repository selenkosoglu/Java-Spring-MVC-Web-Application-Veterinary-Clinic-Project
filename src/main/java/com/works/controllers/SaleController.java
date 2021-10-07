package com.works.controllers;

import com.works.entities.*;
import com.works.entities.projections.SaleInfo;
import com.works.properties.SaleInterlayer;
import com.works.repositories.CustomerRepository;
import com.works.repositories.ProductRepository;
import com.works.repositories.SaleRepository;
import com.works.repositories.StoreRepository;
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
@RequestMapping("/sale")
public class SaleController {

    final CustomerRepository cRepo;
    final SaleRepository sRepo;
    final ProductRepository pRepo;
    final StoreRepository storeRepository;

    public SaleController(CustomerRepository cRepo, SaleRepository sRepo, ProductRepository pRepo, StoreRepository storeRepository) {
        this.cRepo = cRepo;
        this.sRepo = sRepo;
        this.pRepo = pRepo;
        this.storeRepository = storeRepository;
    }

    @GetMapping("")
    public String sale(Model model, SaleInterlayer saleInterlayer) {
        model.addAttribute("saleInterlayer", saleInterlayer);
        model.addAttribute("isError", false);
        return "sale";
    }

    @GetMapping("/getAmount/{productId}")
    @ResponseBody
    public Integer getAmount(@PathVariable String productId) {
        try {
            Integer pid = Integer.parseInt(productId);
            Optional<Product> result = pRepo.findById(pid);
            if (result.isPresent()) {
                return result.get().getProduct_stokMiktari();
            } else {
                return 0;
            }
        } catch (Exception e) {
            Util.log("SaleController getAmount Error : " + e, this.getClass());
            return 0;
        }
    }

    @PostMapping("/insertSale")
    public String insertSale(@Valid @ModelAttribute("saleInterlayer") SaleInterlayer saleInterlayer, BindingResult bindingResult, Model model) {
        if (!bindingResult.hasErrors()) {
            Sale sale = new Sale();
            sale.setSale_code(UUID.randomUUID().toString());
            sale.setSale_detail(saleInterlayer.getPNote());
            sale.setSale_number(saleInterlayer.getPAmount());
            sale.setSale_type(saleInterlayer.getPPaymentType());

            Optional<Product> optProduct = pRepo.findById(saleInterlayer.getPid());
            if (optProduct.isPresent()) {
                optProduct.get().setProduct_stokMiktari(optProduct.get().getProduct_stokMiktari() - sale.getSale_number());
                sale.setProduct(optProduct.get());
                pRepo.saveAndFlush(optProduct.get());
            }
            Optional<Customer> optCustomer = cRepo.findById(saleInterlayer.getCid());
            if (optCustomer.isPresent()) {
                sale.setCustomer(optCustomer.get());
            }
            if (optProduct.isPresent() && optCustomer.isPresent()) {
                //TUTAR HESAPLAMA
                Integer kdvTutarı = (optProduct.get().getProduct_satis() * optProduct.get().getProduct_kdv()) / 100;
                Integer birimFiyatKDVli = optProduct.get().getProduct_satis() + kdvTutarı;
                Integer araFiyat = birimFiyatKDVli * saleInterlayer.getPAmount();
                Integer indirimOranı = 0;
                try {
                    indirimOranı = Integer.parseInt(optCustomer.get().getCu_rateOfDiscount());
                } catch (Exception e) {
                    Util.log("SaleController Error : " + e, this.getClass());
                }
                Integer total = araFiyat - (araFiyat * indirimOranı) / 100;
                sale.setSale_total(total);
            }

            sRepo.save(sale);
            return "redirect:/sale";
        }
        model.addAttribute("isError", true);
        return "sale";
    }

    @GetMapping("/getCustomerList")
    @ResponseBody
    public List<Customer> getCustomersCorrect() {
        try {
            return cRepo.findAll();
        } catch (Exception e) {
            Util.log("SaleController Error : " + e, this.getClass());
        }
        return null;
    }

    @GetMapping("/getProductList")
    @ResponseBody
    public List<Product> getProductsCorrect() {
        try {
            return pRepo.findAll();
        } catch (Exception e) {
            Util.log("SaleController Error : " + e, this.getClass());
        }
        return null;
    }

    @GetMapping("/getRows")
    @ResponseBody
    public List<SaleInfo> getRows() {
        try {
            return sRepo.getRows();
        } catch (Exception e) {
            Util.log("SaleController Error : " + e, this.getClass());
        }
        return null;
    }

    @GetMapping("/getRowsSearching/{search}")
    @ResponseBody
    public List<SaleInfo> getRows(@PathVariable String search) {
        try {
            return sRepo.getRowsSearching(search.trim());
        } catch (Exception e) {
            Util.log("SaleController Error : " + e, this.getClass());
        }
        return null;
    }

    @GetMapping("/delete/{stSaleId}")
    @ResponseBody
    public Boolean delete(@PathVariable String stSaleId) {
        Boolean feedBack = false;
        try {
            Integer saleId = Integer.parseInt(stSaleId);
            feedBack = sRepo.existsById(saleId);
            if (feedBack == true) {
                Optional<Sale> optSale = sRepo.findById(saleId);
                if (optSale.isPresent()) {
                    optSale.get().getProduct().setProduct_stokMiktari(optSale.get().getProduct().getProduct_stokMiktari() + optSale.get().getSale_number());
                    pRepo.saveAndFlush(optSale.get().getProduct());
                }
                sRepo.deleteById(saleId);
            }
        } catch (Exception e) {
            Util.log("SaleController Error : " + e, this.getClass());
        }
        return feedBack;
    }

}