package com.works.controllers;

import com.works.entities.Customer;
import com.works.entities.Diary;
import com.works.entities.calender.CalendarInfo;
import com.works.entities.calender.ScheduleCalendar;
import com.works.entities.projections.DiaryInfo;
import com.works.entities.security.User;
import com.works.properties.DiaryInterlayer;
import com.works.repositories.CustomerRepository;
import com.works.repositories.DiaryRepository;
import com.works.repositories.UserRepository;
import com.works.repositories.calender.CalendarInfoRepository;
import com.works.repositories.calender.ScheduleCalendarRepository;
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
@RequestMapping("/diary")
public class DiaryController {

    final DiaryRepository diaryRepository;
    final UserRepository userRepository;
    final CustomerRepository customerRepository;
    final ScheduleCalendarRepository scheduleCalendarRepository;
    final CalendarInfoRepository calendarInfoRepository;

    public DiaryController(DiaryRepository diaryRepository, UserRepository userRepository, CustomerRepository customerRepository, ScheduleCalendarRepository scheduleCalendarRepository, CalendarInfoRepository calendarInfoRepository) {
        this.diaryRepository = diaryRepository;
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.scheduleCalendarRepository = scheduleCalendarRepository;
        this.calendarInfoRepository = calendarInfoRepository;
    }

    @GetMapping()
    public String getDiary(Model model, DiaryInterlayer diaryInterlayer) {
        model.addAttribute("diaryInterlayer", diaryInterlayer);
        model.addAttribute("doctorList", userRepository.getUsersForRoleId(2));
        model.addAttribute("customerList", customerRepository.findAll());
        model.addAttribute("isError", false);
        return "diary";
    }

    @PostMapping("/insert")
    public String addInsert(@Valid @ModelAttribute("diaryInterlayer") DiaryInterlayer diaryInterlayer, BindingResult bindingResult, Model model) {
        System.out.println(diaryInterlayer.getDiary_statu());
        if (!bindingResult.hasErrors()) {
            Diary diary = new Diary();

            Integer us_id = diaryInterlayer.getUs_id();
            Optional<User> optUser = userRepository.findById(us_id);
            if (optUser.isPresent()) {
                diary.setUser(optUser.get());
            }

            Integer cu_id = diaryInterlayer.getCu_id();
            Optional<Customer> optCustomer = customerRepository.findById(cu_id);
            if (optCustomer.isPresent()) {
                diary.setCustomer(optCustomer.get());
            }

            diary.setDiary_title(diaryInterlayer.getDiary_title());
            diary.setDiary_description(diaryInterlayer.getDiary_description());
            diary.setDiary_date(diaryInterlayer.getDiary_date());
            diary.setDiary_time(diaryInterlayer.getDiary_time());

            if (diaryInterlayer.getDiary_statu() != null) {
                diary.setDiary_statu(true);
            } else {
                diary.setDiary_statu(false);
            }
            diaryRepository.save(diary);
            //DENEME
            //Hasta takviminin bilgilerini alma.
            String bg_color = "";
            String border_color = "";
            String color = "";
            String drag_bg_color = "";
            Optional<CalendarInfo> o = calendarInfoRepository.findById(1);
            if (o.isPresent()) {
                bg_color = o.get().getCbgColor();
                border_color = o.get().getCborderColor();
                color = o.get().getCcolor();
                drag_bg_color = o.get().getCdragBgColor();
            }
            Integer calendar_id = 1;
            String category = "time";
            //Format ayarlanacak
            String time = diaryInterlayer.getDiary_date() + diaryInterlayer.getDiary_time();
            String start = "2021-09-22T11:00:00.000Z";
            String end = "2021-09-22T11:00:00.000Z";
            String id = UUID.randomUUID().toString();
            Integer is_all_day = 0;
            String title = optUser.get().getUs_name();
            String location = optCustomer.get().getCu_name();
            String raw = "public";
            String state = "Free";


            //DENEME BİTİŞ
            return "redirect:/diary";
        }
        model.addAttribute("doctorList", userRepository.getUsersForRoleId(2));
        model.addAttribute("customerList", customerRepository.findAll());
        model.addAttribute("isError", true);
        return "diary";
    }

    @GetMapping("/diaryList")
    @ResponseBody
    public List<DiaryInfo> getLabList() {
        try {
            return diaryRepository.allDiaryList();
        } catch (Exception e) {
            Util.log("DiaryController getLabList Error : " + e, this.getClass());
        }
        return null;
    }

    @GetMapping("/diaryList/{search}")
    @ResponseBody
    public List<DiaryInfo> getLabListSearch(@PathVariable String search) {
        try {
            return diaryRepository.allDiaryListSearch(search);
        } catch (Exception e) {
            Util.log("DiaryController getLabListSearch Error : " + e, this.getClass());
        }
        return null;
    }

    @GetMapping("/delete/{stDiary_id}")
    @ResponseBody
    public Boolean delete(@PathVariable String stDiary_id) {
        Boolean feedBack = false;
        try {
            Integer labId = Integer.parseInt(stDiary_id);
            feedBack = diaryRepository.existsById(labId);
            if (feedBack == true) {
                diaryRepository.deleteById(labId);
            }
        } catch (Exception e) {
            Util.log("DiaryController delete Error : " + e, this.getClass());
        }
        return feedBack;
    }
}
