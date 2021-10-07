package com.works.controllers;

import com.works.entities.CustomerGroup;
import com.works.repositories.CustomerGroupRepository;
import com.works.utils.Util;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/customergroup")
public class CustomerGroupController {

    final CustomerGroupRepository customerGroupRepository;

    public CustomerGroupController(CustomerGroupRepository customerGroupRepository) {
        this.customerGroupRepository = customerGroupRepository;
    }

    @GetMapping("")
    public String customerGroup(Model model, CustomerGroup customerGroup) {
        model.addAttribute("customerGroup", customerGroup);
        model.addAttribute("isValidName", false);
        model.addAttribute("isError", false);
        return "customergroup";
    }

    CustomerGroup customerGroupUpdate = new CustomerGroup();
    Integer processType = null;

    @PostMapping("/insert")
    public String customerGroupInsert(@Valid @ModelAttribute("customerGroup") CustomerGroup customerGroup, BindingResult bindingResult, Model model) {
        System.out.println("*******************PROCESS-TYPE*********************" + processType);
        boolean isValidName = false;
        if (!bindingResult.hasErrors()) {
            try {
                if (processType == 1) {
                    customerGroupUpdate = new CustomerGroup();
                } else {
                    customerGroup.setCu_gr_id(customerGroupUpdate.getCu_gr_id());
                }
                customerGroupRepository.saveAndFlush(customerGroup);
                customerGroupUpdate = new CustomerGroup();
                return "redirect:/customergroup";
            } catch (DataIntegrityViolationException ex) {
                Util.log("CustomerGroupController Error : " + "Bu isimde bir grup bulunmaktadır." + ex, this.getClass());
                isValidName = true;
                model.addAttribute("validNameError", "Bu isimde bir grup bulunmaktadır.");
            } catch (Exception ex) {
                System.out.println("customerGroupInsert Error : " + ex);
                Util.log("customerGroupInsert Error : " + ex, this.getClass());

            }
        }
        model.addAttribute("isValidName", isValidName);
        model.addAttribute("isError", true);
        return "customergroup";
    }

    @GetMapping("/getAllCustomerGroup")
    @ResponseBody
    public List<CustomerGroup> getAllCustomerGroup() {
        return customerGroupRepository.findAll();
    }

    @GetMapping("/delete/{index}")
    @ResponseBody
    public Boolean deleteCustomerGroup(@PathVariable String index) {
        Boolean isValidCustomerGroup = false;
        try {
            Integer customerGroupId = Integer.parseInt(index);
            isValidCustomerGroup = customerGroupRepository.existsById(customerGroupId);
            if (isValidCustomerGroup) {
                customerGroupRepository.deleteById(customerGroupId);
                customerGroupUpdate = new CustomerGroup();
            }
        } catch (Exception ex) {
            Util.log("deleteCustomerGroup Error : " + ex, this.getClass());
        }
        return isValidCustomerGroup;
    }

    @GetMapping("/update/{index}")
    @ResponseBody
    public List<Object> updateCustomerUpdate(@PathVariable String index) {
        List<Object> info = new ArrayList<>();
        try {
            Integer customerGroupId = Integer.parseInt(index);
            Boolean isValidCustomerGroup = customerGroupRepository.existsById(customerGroupId);
            if (isValidCustomerGroup) {
                customerGroupUpdate = customerGroupRepository.findById(customerGroupId).get();
                info.add(true);
                info.add(customerGroupUpdate);
            } else {
                // aranan müşteri yok, sisteme müdahale edilmiş
                info.add(false);
            }
        } catch (Exception ex) {
            Util.log("updateCustomerGroup Error : " + ex, this.getClass());
        }

        return info;
    }

    @GetMapping("/processtype/{strIndex}")
    @ResponseBody
    public boolean processType(@PathVariable String strIndex) {
        System.out.println("strIndex = " + strIndex);
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

    @GetMapping("/search/{strSearch}")
    @ResponseBody
    public List<CustomerGroup> getCustomerGroupSearch(@PathVariable String strSearch) {
        try {
            return customerGroupRepository.findByCu_gr_nameContainsAllIgnoreCase(strSearch.trim());
        } catch (Exception ex) {
            Util.log("customerGroupSearch Error : " + ex, this.getClass());
        }
        return null;
    }
}