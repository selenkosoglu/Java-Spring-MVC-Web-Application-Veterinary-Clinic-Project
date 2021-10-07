package com.works.controllers;

import com.works.entities.projections.DiaryInfoToIstaticticPageCustomer;
import com.works.entities.projections.DiaryInfoToIstatisticPage;
import com.works.repositories.DiaryRepository;
import com.works.repositories.UserFollowInRepository;
import com.works.utils.Util;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@RequestMapping("/statistic")
@Controller
public class StatisticController {

    final DiaryRepository diaryRepository;
    final UserFollowInRepository userFollowInRepository;

    public StatisticController(DiaryRepository diaryRepository, UserFollowInRepository userFollowInRepository) {
        this.diaryRepository = diaryRepository;
        this.userFollowInRepository = userFollowInRepository;
    }

    @GetMapping
    public String statistic(Model model) {
        Optional<DiaryInfoToIstatisticPage> opt = diaryRepository.findBusiestDayOfWeek();
        Optional<DiaryInfoToIstaticticPageCustomer> opt2 = diaryRepository.findBusiestCustomer();
        String date = "";
        Integer randevuNumber = 0;
        if (opt.isPresent()) {
            date = castingDayName(opt.get().getDaysOfWeek());
            try {
                randevuNumber = Integer.parseInt(opt.get().getDiaryNumber());
            } catch (Exception e) {
                Util.log("StatisticController Error" + e, this.getClass());
            }
        }
        Integer randevuNumber2 = 0;
        String cuname = "";
        if (opt2.isPresent()) {
            try {
                randevuNumber2 = Integer.parseInt(opt2.get().getCount_diary());
                cuname = opt2.get().getCu_name();
            } catch (Exception e) {
                Util.log("StatisticController Error" + e, this.getClass());
            }
        }
        Optional<String> optional = userFollowInRepository.getNumber();
        Integer ziyaretsayisi = 0;
        if (optional.isPresent()) {
            try {
                ziyaretsayisi = Integer.parseInt(optional.get());
            } catch (Exception e) {
                Util.log("StatisticController Error : " + e, this.getClass());
            }
        }

        List<Object[]> customerList = userFollowInRepository.ls();//Bu Hafta Hangi Müşteri Ne Kadar Kazandırdı
        List<Object[]> saleTypeList = userFollowInRepository.ls2();//Ödeme Tipine Göre Kazanç

        saleTypeList.forEach(item -> {
            if (item[0].equals(1)) {
                item[0] = "Nakit";
            } else if (item[0].equals(2)) {
                item[0] = "Kredi";
            } else {
                item[0] = "Havale";
            }
        });

        model.addAttribute("enyogungun", "" + "Gün: " + date);
        model.addAttribute("randevusayisi", "Randevu Sayısı: " + randevuNumber);
        model.addAttribute("customername", "Müşteri Adı: " + cuname);
        model.addAttribute("count_diary", "Randevu Sayısı: " + randevuNumber2);
        model.addAttribute("ziyaretsayisi", "+" + ziyaretsayisi);
        model.addAttribute("customerList", customerList);
        model.addAttribute("saleTypeList", saleTypeList);
        return "statistic";
    }

    private String castingDayName(String i) {
        if (i.equals("1")) {
            return "Pazartesi";
        } else if (i.equals("2")) {
            return "Salı";
        } else if (i.equals("3")) {
            return "Çarşamba";
        } else if (i.equals("4")) {
            return "Perşembe";
        } else if (i.equals("5")) {
            return "Cuma";
        } else if (i.equals("6")) {
            return "Cumartesi";
        } else {
            return "Pazar";
        }
    }
}


