package com.works.controllers;

import com.works.entities.Customer;
import com.works.entities.PaymentIn;
import com.works.entities.PaymentOut;
import com.works.entities.projections.AllPayInOutInfo;
import com.works.entities.projections.DebtorCustomerInfo;
import com.works.entities.projections.PayInPayOutInfo;
import com.works.properties.PayInInterlayer;
import com.works.repositories.CustomerRepository;
import com.works.repositories.PaymentInRepository;
import com.works.repositories.PaymentOutRepository;
import com.works.utils.Util;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/payment")
public class PaymentController {
    final PaymentInRepository paymentInRepository;
    final PaymentOutRepository paymentOutRepository;
    final CustomerRepository cRepo;

    public PaymentController(PaymentInRepository paymentInRepository, PaymentOutRepository paymentOutRepository, CustomerRepository cRepo) {
        this.paymentInRepository = paymentInRepository;
        this.paymentOutRepository = paymentOutRepository;
        this.cRepo = cRepo;
    }

    @GetMapping("")
    public String payment(Model model, PayInInterlayer payInInterlayer, PaymentOut paymentOut){
        model.addAttribute("payInInterlayer", payInInterlayer);
        model.addAttribute("paymentOut", paymentOut);
        model.addAttribute("isError1", false);
        model.addAttribute("isError2", false);

        //model.addAttribute("getAllPay", paymentInRepository.getAllPay());

        return "payment";
    }

    @PostMapping("/insertPayIn")
    public String insertPayIn(@Valid @ModelAttribute("payInInterlayer") PayInInterlayer payInInterlayer, BindingResult bindingResult, PaymentOut paymentOut, Model model) {
        if (!bindingResult.hasErrors()) {
            PaymentIn paymentIn = new PaymentIn();
            paymentIn.setPin_detail(payInInterlayer.getPin_detail());
            paymentIn.setPin_price(payInInterlayer.getPin_price());
            paymentIn.setPin_operationType(0);
            paymentIn.setPin_paymentType(payInInterlayer.getPin_paymentType());

            Optional<Customer> optCustomer = cRepo.findById(payInInterlayer.getCu_id());
            if (optCustomer.isPresent()) {
                paymentIn.setCustomer(optCustomer.get());
            }
            model.addAttribute("paymentOut", paymentOut);
            paymentInRepository.save(paymentIn);
            return "redirect:/payment";
        }
        model.addAttribute("isError1", true);
        model.addAttribute("isError2", false);
        return "payment";
    }

    @PostMapping("/insertPayOut")
    public String insertPayOut(@Valid @ModelAttribute("paymentOut") PaymentOut paymentOut, BindingResult bindingResult, PayInInterlayer payInInterlayer, Model model) {
        if (!bindingResult.hasErrors()) {
            paymentOut.setPout_operationType(1);
            // Kasadaki Para miktarı, çekilmek istene miktardan fazlaysa devam edecek
            Optional<PayInPayOutInfo> payInPayOutInterLayer = paymentOutRepository.totalPayInOut();
            if(payInPayOutInterLayer.isPresent()){
                int totalPayIn = payInPayOutInterLayer.get().getTotalPayIn();
                int totalPayOut = payInPayOutInterLayer.get().getTotalPayOut() == null ? 0 : payInPayOutInterLayer.get().getTotalPayOut();
                if(totalPayIn>totalPayOut){
                    model.addAttribute("payInInterlayer", payInInterlayer);
                    paymentOutRepository.save(paymentOut);
                }
            }
            return "redirect:/payment";
        }
        model.addAttribute("isError1", false);
        model.addAttribute("isError2", true);
        return "payment";
    }

    @GetMapping("/debtorCustomer")
    @ResponseBody
    public List<DebtorCustomerInfo> debtorCustomer(){
        List<DebtorCustomerInfo> debtorCustomerInfo =null;
        List<DebtorCustomerInfo> ls = new ArrayList<>();
        try{
            //    Optional<DebtorCustomerInfo> debtorCustomerInfo = Optional.ofNullable(paymentInRepository.deptorCustomerInfo(customerId));
            debtorCustomerInfo  =   paymentInRepository.debtorCustomerInfo();

            System.out.println("dizi boyutu = " + debtorCustomerInfo.size());
            for(int i = 0; i<debtorCustomerInfo.size(); i++){
                int totalPrice = Integer.parseInt(String.valueOf(debtorCustomerInfo.get(i).getTotalPrice()));
                int totalPayment = Integer.parseInt(String.valueOf(debtorCustomerInfo.get(i).getTotalPayment()));
                if(totalPrice>totalPayment){
                    ls.add(debtorCustomerInfo.get(i));
                }
            }
        }catch (Exception ex){
            Util.log("debtorCustomer Error : " + ex, this.getClass());
        }
        return ls;
    }

    @GetMapping("/getPayOutInfo")
    @ResponseBody
    public PayInPayOutInfo getPayOutInfo(){
        Optional<PayInPayOutInfo> payInPayOutInterLayer = paymentOutRepository.totalPayInOut();
        if(payInPayOutInterLayer.isPresent()){
            return paymentOutRepository.totalPayInOut().get();
        }
        return null;
    }

    @GetMapping("/getPayAllInfo/{paymentProcess}")
    @ResponseBody
    public List<? extends Object> getAllPay(@PathVariable String paymentProcess){
        try{
            Integer index = Integer.parseInt(paymentProcess);
            if(index == 2){
                return paymentInRepository.getAllPayInfo();
            }else if(index == 0){
                List<AllPayInOutInfo> paymentInList = paymentInRepository.getAllPayIn(index);
                return paymentInList;
            }else {
                List<AllPayInOutInfo> paymentOutList = paymentOutRepository.getAllPayOut(index);
                return paymentOutList;
            }
        }catch (Exception ex){
            Util.log("selectedPaymentType Error : " + ex, this.getClass());
        }
        return null;
    }

    @GetMapping("/searchlist/{paymentProcess}/{strSearch}")
    @ResponseBody
    public List<AllPayInOutInfo> getPaySearchList(@PathVariable String paymentProcess, @PathVariable String strSearch){
        try{
            Integer index = Integer.parseInt(paymentProcess);
            if(index == 2){
                return paymentInRepository.getPaySearchList(strSearch.trim());
            }else if(index == 0){
                return paymentInRepository.getAllPayInSearchList(strSearch.trim());
            }else{
                return paymentOutRepository.getAllPayOutSearchList(strSearch.trim());
            }
        }catch(Exception ex){
            Util.log("paySearch Error : " + ex, this.getClass());
        }
        return null;
    }

    @GetMapping("/delete/{type}/{index}")
    @ResponseBody
    public Boolean deletePay(@PathVariable String type, @PathVariable String index){
        Boolean isValidPay = false;
        try{
            Integer payId = Integer.parseInt(index);
            if(type.equals("0")){
                // Safe In
                isValidPay = paymentInRepository.existsById(payId);
                if(isValidPay){
                    paymentInRepository.deleteById(payId);
                }
            }else {
                // Safe Out
                isValidPay = paymentOutRepository.existsById(payId);
                if(isValidPay){
                    paymentOutRepository.deleteById(payId);
                }
            }
        }catch(Exception ex){
            Util.log("deletePay Error : " + ex, this.getClass());
        }
        return isValidPay;
    }

}
