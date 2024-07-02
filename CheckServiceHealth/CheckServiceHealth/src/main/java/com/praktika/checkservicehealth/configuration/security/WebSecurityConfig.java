package com.praktika.checkservicehealth.configuration.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.*;
@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class WebSecurityConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        return new MyUserDetailsService();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return  http.csrf(AbstractHttpConfigurer::disable)
//                .formLogin(form -> form
//                        .loginProcessingUrl("/customLogin")
//                        .loginPage("/customLogin")
//                        .defaultSuccessUrl("/check", true)
//                        .permitAll()
//                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/registration").permitAll()
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/static/css/*.css").permitAll()
                        .requestMatchers("/static/images/*").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("admin")
                        .requestMatchers("/api/auth/*").permitAll()
                        .anyRequest().authenticated())
                .build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}