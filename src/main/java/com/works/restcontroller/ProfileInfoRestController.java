package com.works.restcontroller;

import com.works.entities.security.User;
import com.works.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/profile")
public class ProfileInfoRestController {

    final UserRepository userRepository;

    public ProfileInfoRestController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/getPhoto")
    public Map<String, Object> getPhoto() {
        Authentication aut = SecurityContextHolder.getContext().getAuthentication();
        String email = aut.getName(); // username

        Optional<User> optUser = userRepository.findByEmailEqualsAllIgnoreCase(email);
        Map<String, Object> hm = new LinkedHashMap<>();
        if (optUser.isPresent()) {
            hm.put("profilePhoto", optUser.get().getUser_file());
        }
        return hm;
    }
}
