package com.praktika.checkservicehealth.controller;

import com.praktika.checkservicehealth.dto.UserDto;
import com.praktika.checkservicehealth.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/signUp")
    private ResponseEntity<Object> signUp(@RequestBody UserDto userDto) {
        if (userService.findByUsername(userDto.username()) != null){
            return ResponseEntity.ok("Пользователь с таким username уже существует!");
        } else {
            userService.saveUser(userDto);
            return ResponseEntity.ok().body("Пользователь успешно зарегистрирован");
        }
    }

    @PostMapping("/signIn")
    private ResponseEntity<Object> signIn(@RequestBody UserDto userDto, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(userDto.username(),
                userDto.password());
        try {
            Authentication auth = authenticationManager.authenticate(authReq);
            SecurityContext sc = SecurityContextHolder.getContext();
            sc.setAuthentication(auth);
            HttpSession session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, sc);

            return ResponseEntity.ok().body("Аутентификация прошла успешно");
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
