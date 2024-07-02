package com.praktika.checkservicehealth.controller;

import com.praktika.checkservicehealth.dto.UserDto;
import com.praktika.checkservicehealth.entity.User;
import com.praktika.checkservicehealth.service.UserService;
import com.praktika.checkservicehealth.service.impl.NotificationTgImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("userForm", new UserDto());
        return "registration";
    }

    @PostMapping("/signUp")
    private String signUp(@ModelAttribute("userForm") UserDto userForm,
                          BindingResult bindingResult,
                          Model model) {
        if (bindingResult.hasErrors()) {
            return "registration";
        }
        if (userService.findByUsername(userForm.getUsername()) != null){
            model.addAttribute("usernameError", "Пользователь с таким именем уже существует");
            LOGGER.info("Пользователь с таким именем уже существует");
            return "registration";
        } else {
            userService.saveUser(userForm);
            LOGGER.info("Пользователь сохранен");
        }

        return "redirect:/api/auth/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/signIn")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(username, password);
        try {
            Authentication auth = authenticationManager.authenticate(authReq);
            SecurityContext sc = SecurityContextHolder.getContext();
            sc.setAuthentication(auth);
            HttpSession session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, sc);

            LOGGER.info("Успешно залогинился");
            return "redirect:/api/endpoints/check";
        } catch (AuthenticationException e) {
            LOGGER.info("Ошибка логина");
            return "login";
        }
    }

//
//    @PostMapping("/signUp")
//    private ResponseEntity<Object> signUp(@RequestBody UserDto userDto) {
//        if (userService.findByUsername(userDto.getUsername()) != null){
//            return ResponseEntity.ok("Пользователь с таким username уже существует!");
//        } else {
//            userService.saveUser(userDto);
//            return ResponseEntity.ok().body("Пользователь успешно зарегистрирован");
//        }
//    }
//
//    @PostMapping("/signIn")
//    private ResponseEntity<String> signIn(@RequestBody UserDto userDto, HttpServletRequest request) {
//        UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(userDto.getUsername(),
//                userDto.getPassword());
//        try {
//            Authentication auth = authenticationManager.authenticate(authReq);
//            SecurityContext sc = SecurityContextHolder.getContext();
//            sc.setAuthentication(auth);
//            HttpSession session = request.getSession(true);
//            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, sc);
//
//            return ResponseEntity.ok().body("Аутентификация прошла успешно");
//        } catch (AuthenticationException e) {
//            return ResponseEntity.ok().body("Аутентификация провалена");
//        }
//    }

}
