package com.works.services;

import com.works.entities.security.Role;
import com.works.entities.security.User;
import com.works.repositories.UserRepository;
import com.works.utils.Util;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService extends SimpleUrlLogoutSuccessHandler implements UserDetailsService, LogoutSuccessHandler {
    final UserRepository uRepo;

    public UserService(UserRepository uRepo) {
        this.uRepo = uRepo;
    }

    // security login
    @Override
    public UserDetails loadUserByUsername(String email) {
        UserDetails userDetails = null;
        Optional<User> oUser = uRepo.findByEmailEqualsAllIgnoreCase(email);
        if (oUser.isPresent()) {
            User us = oUser.get();
            userDetails = new org.springframework.security.core.userdetails.User(
                    us.getEmail(),
                    us.getPassword(),
                    us.isEnabled(),
                    us.isTokenExpired(),
                    true,
                    true,
                    getAuthorities(us.getRoles()));
        } else {
            Util.log("Kullanıcı adı yada şifre hatalı", this.getClass());
            throw new UsernameNotFoundException("Kullanıcı adı yada şifre hatalı");
        }
        return userDetails;
    }

    private List<GrantedAuthority> getAuthorities(List<Role> roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getRo_name()));
        }
        return authorities;
    }

    public User register(User us) throws AuthenticationException {

        if (!Util.isEmail(us.getEmail())) {
            Util.log("Bu mail formatı hatalı!", this.getClass());
            throw new AuthenticationException("Bu mail formatı hatalı!");
        }

        Optional<User> uOpt = uRepo.findByEmailEqualsAllIgnoreCase(us.getEmail());
        if (uOpt.isPresent()) {
            Util.log("Bu kullanıcı daha önce kayıtlı!", this.getClass());
            throw new AuthenticationException("Bu kullanıcı daha önce kayıtlı!");
        }
        us.setPassword(encoder().encode(us.getPassword()));
        return uRepo.save(us);
    }

    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        System.out.println("onLogoutSuccess Call ");
        httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/login");
    }


}
