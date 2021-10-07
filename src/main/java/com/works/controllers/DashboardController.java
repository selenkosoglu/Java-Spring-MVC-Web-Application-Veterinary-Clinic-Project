package com.works.controllers;

import com.works.entities.UserFollowIn;
import com.works.entities.projections.DashboardInfo;
import com.works.entities.projections.DiaryToDashboard;
import com.works.repositories.DashboardRepository;
import com.works.repositories.UserFollowInRepository;
import com.works.repositories.UserRepository;
import com.works.utils.Util;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigInteger;
import java.util.*;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    final UserFollowInRepository userFollowInRepository;
    final UserRepository userRepository;
    final DashboardRepository dashboardRepository;

    public DashboardController(UserFollowInRepository userFollowInRepository, UserRepository userRepository, DashboardRepository dashboardRepository) {
        this.userFollowInRepository = userFollowInRepository;
        this.userRepository = userRepository;
        this.dashboardRepository = dashboardRepository;
    }

    @GetMapping("")
    public String dashboard(Model model) {
        controlUserFollowIn();
        Optional<DashboardInfo> optionalDashboardInfo = dashboardRepository.getViewDashboard();
        if (optionalDashboardInfo.isPresent()) {
            Float rateDiary = 0.0f;
            Float rateCustomer = 0.0f;
            Float rateCiro = 0.0f;
            Integer diaryToday = 0;
            Integer diaryYesterday = 0;
            Integer registercustomerToday = 0;
            Integer registercustomerYesterday = 0;
            Integer todayCiro = 0;
            Integer yesterdayCiro = 0;
            Integer stockValue = 0;
            try {
                diaryToday = Integer.parseInt(optionalDashboardInfo.get().getDiaryToday());
                diaryYesterday = Integer.parseInt(optionalDashboardInfo.get().getDiaryYesterday());
                registercustomerToday = Integer.parseInt(optionalDashboardInfo.get().getRegisterCustomerToday());
                registercustomerYesterday = Integer.parseInt(optionalDashboardInfo.get().getRegisterCustomerYesterday());
                todayCiro = Integer.parseInt(optionalDashboardInfo.get().getTodayCiro());
                yesterdayCiro = Integer.parseInt(optionalDashboardInfo.get().getYesterdayCiro());
                stockValue = Integer.parseInt(optionalDashboardInfo.get().getStockValue());
            } catch (Exception e) {
                Util.log("DashboardController Error : " + e, this.getClass());
            }
            if (diaryYesterday != 0) {
                rateDiary = ((diaryToday - diaryYesterday) / (float) diaryYesterday);
                rateDiary = rateDiary * 100;
            }
            if (registercustomerYesterday != 0) {
                rateCustomer = ((registercustomerToday - registercustomerYesterday) / (float) registercustomerYesterday);
                rateCustomer = rateCustomer * 100;
            }
            if (yesterdayCiro != 0) {
                rateCiro = ((todayCiro - yesterdayCiro) / (float) yesterdayCiro);
                rateCiro = rateCiro * 100;
            }

            String key = " % Bir önceki güne oranla";

            model.addAttribute("rateDiary", "" + rateDiary + key);
            model.addAttribute("rateCustomer", "" + rateCustomer + key);
            model.addAttribute("rateCiro", "" + rateCiro + key);
            model.addAttribute("diaryToday", diaryToday);
            model.addAttribute("customerToday", registercustomerToday);
            model.addAttribute("todayCiro", todayCiro);
            model.addAttribute("stockValue", stockValue);

            // Bugunku tarihi gecen randevular
            List<DiaryToDashboard> dashExpiredDiaryList = dashboardRepository.getExpiredDiary();
            model.addAttribute("dashExpiredDiaryList",dashExpiredDiaryList);
            // Bugunku yaklasan randevular
            List<DiaryToDashboard> dashUpcomingDiaryList = dashboardRepository.getUpcomingDiary();
            model.addAttribute("dashUpcomingDiaryList",dashUpcomingDiaryList);

        }
        return "dashboard";
    }

    private void controlUserFollowIn() {
        Authentication aut = SecurityContextHolder.getContext().getAuthentication();
        String email = aut.getName(); // username
        if (email != null) {
            System.out.println(email);
            //Son yarım saatte bu kullanıcı giriş yapmış mı?
            Optional<UserFollowIn> optUserFollowIn = userFollowInRepository.findLastLoginProcesses(email);
            if (!optUserFollowIn.isPresent()) {
                //Son yarım saatte bu kullanıcı giriş yapmadıysa yazıldı.
                UserFollowIn userFollowIn = new UserFollowIn();
                userFollowInRepository.save(userFollowIn);
            }
        }

    }

    @GetMapping("/dayOfWeeks")
    @ResponseBody
    public List<String> getCustomerRegisterDayOfWeeks(){
        List<String> ls = new ArrayList<>();
        Object[] registerCustomerDaysOfWeek = dashboardRepository.registerCustomerDaysOfWeek();
        //System.out.println(((Object[]) registerCustomerDaysOfWeek[0])[0]);
        for (int i = 0; i<7; i++){
            ls.add(String.valueOf(((BigInteger)(((Object[]) registerCustomerDaysOfWeek[0])[i])).intValue()));
        }
        return ls;
    }

    @GetMapping("/petTypeCount")
    @ResponseBody
    public Map<String,Double> getPetTypeCount(){
        Map<String,Double> hm = new LinkedHashMap<>();
        try{
            List<Object[]> petTypeNumber = dashboardRepository.petTypeCount();
            double sum = 0;
            for(int i = 0; i<petTypeNumber.size(); i++){
                sum += ((BigInteger)(((Object[]) petTypeNumber.get(i))[1])).doubleValue();
            }
            for(int i = 0; i<petTypeNumber.size(); i++){
                Double temp = ((BigInteger)(((Object[]) petTypeNumber.get(i))[1])).doubleValue();
                Double temp2 = (temp*100)/sum;
                Formatter formatter = new Formatter();
                formatter.format("%.2f", temp2);
                String temp3 = formatter.toString().replace(',','.');
                Double newValue = Double.parseDouble(temp3);

                hm.put(String.valueOf(((((Object[]) petTypeNumber.get(i))[0]))) ,newValue);
                //hm.put(String.valueOf(((((Object[]) petTypeNumber.get(i))[0]))), ((BigInteger)(((Object[]) petTypeNumber.get(i))[1])).doubleValue());
            }
        }catch (Exception ex){
            System.err.println("getPetTypeCount Error = " + ex);
        }
        return hm;
    }

}
