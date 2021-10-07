package com.works.controllers;

import com.works.entities.security.Role;
import com.works.entities.security.User;
import com.works.properties.RegisterChangeInterlayer;
import com.works.properties.RegisterInterlayer;
import com.works.repositories.RoleRepository;
import com.works.repositories.UserRepository;
import com.works.services.UserService;
import com.works.utils.Util;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.AuthenticationException;
import javax.validation.Valid;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/register")
public class RegisterController {

    private final String UPLOAD_DIR = "src/main/resources/static/uploads/userphotolist/";

    final UserService uService;
    final RoleRepository rRepo;
    final UserRepository userRepository;

    public RegisterController(UserService uService, RoleRepository rRepo, UserRepository userRepository) {
        this.uService = uService;
        this.rRepo = rRepo;
        this.userRepository = userRepository;
    }

    @GetMapping("")
    public String register(Model model, RegisterInterlayer registerInterlayer, RegisterChangeInterlayer registerChangeInterlayer) {
        model.addAttribute("registerInterlayer", registerInterlayer);
        model.addAttribute("registerChangeInterlayer", registerChangeInterlayer);
        model.addAttribute("userList", userRepository.findAll());
        return "register";
    }

    @PostMapping("/add")
    public String register(@Valid @ModelAttribute("registerInterlayer") RegisterInterlayer registerInterlayer, BindingResult bindingResult, Model model, @RequestParam("user_file") MultipartFile file) {
        if (!bindingResult.hasErrors()) {
            try {
                User user = new User();
                user.setUs_name(registerInterlayer.getUser_name());
                user.setUs_surname(registerInterlayer.getUser_surname());
                user.setEmail(registerInterlayer.getUser_email());
                user.setPassword(registerInterlayer.getUser_password());
                user.setUs_tel(registerInterlayer.getUser_tel());
                user.setEnabled(true);
                user.setTokenExpired(true);


                String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                String ext = "";
                try {//File kısmı validation'da kontrol edilmediği için resim yüklenmemesi durumu kontrolü
                    int length = fileName.lastIndexOf(".");
                    ext = fileName.substring(length, fileName.length());
                    String uui = UUID.randomUUID().toString();
                    fileName = uui + ext;

                    Path path = Paths.get(UPLOAD_DIR + fileName);
                    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception e) {
                    fileName = "anonim.jpg";
                    Util.log("RegisterController Error : " + e, this.getClass());
                    //File seçilmediyse de devam edilsin.
                    /*model.addAttribute("registerInterlayer", registerInterlayer);
                    model.addAttribute("registerChangeInterlayer", new RegisterChangeInterlayer());
                    model.addAttribute("userList", userRepository.findAll());
                    return "register";//File hatası*/
                }

                user.setUser_file(fileName);

                List<Role> roleList = new ArrayList<>();
                for (int i = 0; i < registerInterlayer.getUser_roles().length; i++) {
                    try {
                        Integer roleId = Integer.parseInt(registerInterlayer.getUser_roles()[i]);
                        Optional<Role> optRole = rRepo.findById(roleId);
                        if (optRole.isPresent()) {
                            roleList.add(optRole.get());
                        }
                    } catch (Exception e) {
                        Util.log("RegisterController Error : " + e, this.getClass());
                        model.addAttribute("registerInterlayer", registerInterlayer);
                        model.addAttribute("registerChangeInterlayer", new RegisterChangeInterlayer());
                        model.addAttribute("userList", userRepository.findAll());
                        return "register";//Dizi içinde String ifade olursa casting hatası
                    }
                }
                user.setRoles(roleList);
                uService.register(user);
            } catch (Exception e) {
                Util.log("RegisterController Error : " + e, this.getClass());
            }
            return "redirect:/register";
        }
        model.addAttribute("registerInterlayer", registerInterlayer);
        model.addAttribute("registerChangeInterlayer", new RegisterChangeInterlayer());
        model.addAttribute("userList", userRepository.findAll());
        return "register";//Validation hatası
    }

    @PostMapping("/change")
    public String registerChange(@Valid @ModelAttribute("registerChangeInterlayer") RegisterChangeInterlayer registerChangeInterlayer, BindingResult bindingResult, Model model) {
        if (!bindingResult.hasErrors()) {
            try {
                Integer us_id = Integer.parseInt(registerChangeInterlayer.getChange_user_name());
                Optional<User> optUser = userRepository.findById(us_id);
                List<Role> roleList = new ArrayList<>();
                if (optUser.isPresent()) {
                    for (int i = 0; i < registerChangeInterlayer.getChange_user_roles().length; i++) {
                        try {
                            Integer roleId = Integer.parseInt(registerChangeInterlayer.getChange_user_roles()[i]);
                            Optional<Role> optRole = rRepo.findById(roleId);
                            if (optRole.isPresent()) {
                                roleList.add(optRole.get());
                            }
                        } catch (Exception e) {
                            Util.log("RegisterController Error : " + e, this.getClass());
                            model.addAttribute("registerInterlayer", new RegisterInterlayer());
                            model.addAttribute("registerChangeInterlayer", registerChangeInterlayer);
                            model.addAttribute("userList", userRepository.findAll());
                            return "register";//Dizi içinde String ifade olursa casting hatası
                        }
                    }
                    optUser.get().setRoles(roleList);
                    userRepository.saveAndFlush(optUser.get());
                }
                return "redirect:/register";
            } catch (Exception e) {
                Util.log("RegisterController Error : " + e, this.getClass());
                model.addAttribute("registerInterlayer", new RegisterInterlayer());
                model.addAttribute("registerChangeInterlayer", registerChangeInterlayer);
                model.addAttribute("userList", userRepository.findAll());
                return "register";//Us_id değeri String ifade olursa casting hatası
            }
        }
        model.addAttribute("registerInterlayer", new RegisterInterlayer());
        model.addAttribute("registerChangeInterlayer", registerChangeInterlayer);
        model.addAttribute("userList", userRepository.findAll());
        return "register";//Validation hatası
    }
}
