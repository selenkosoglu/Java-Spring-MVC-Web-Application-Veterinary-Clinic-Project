package com.works.controllers;

import com.works.entities.projections.AllCustomerPetInfo;
import com.works.repositories.PetRepository;
import com.works.restcontroller.ProfileInfoRestController;
import com.works.utils.Util;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CustomerInfoController {

    final PetRepository petRepository;
    final ProfileInfoRestController profileInfoRestController;

    public CustomerInfoController(PetRepository petRepository, ProfileInfoRestController profileInfoRestController) {
        this.petRepository = petRepository;
        this.profileInfoRestController = profileInfoRestController;
    }

    @GetMapping("/customerinfo/{stCid}")
    public String customerinfo(@PathVariable String stCid, Model model) {
        Integer cid = 0;

        try {
            cid = Integer.parseInt(stCid);
            System.out.println(cid);
        } catch (Exception e) {
            Util.log("CustomerInfoController Error " + e, this.getClass());
            return "redirect:/customerlist";
        }

        String value = "/images/profiles/anonim.jpg";
        model.addAttribute("profilePhoto", value);

        List<AllCustomerPetInfo> allCustomerPetInfoList = petRepository.getCustomerPets(cid);
        allCustomerPetInfoList.forEach(item -> {
            System.out.println(item);
        });
        //Müşteri ekranı açılabilmesi için, en az 1 pete sahip olmalı.
        //Bu müşterinin petleri ırka, tipe, renge vs sahip olmalı.
        if (allCustomerPetInfoList == null || allCustomerPetInfoList.size() == 0) {
            return "redirect:/customerlist";
        }
        model.addAttribute("all", allCustomerPetInfoList);
        return "customerinfo";
    }
}
