package com.praktika.checkservicehealth.controller;

import com.praktika.checkservicehealth.dto.EndpointStatusDto;
import com.praktika.checkservicehealth.service.EndpointService;
import com.praktika.checkservicehealth.service.NotificationTg;
import com.praktika.checkservicehealth.service.impl.EndpointServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Controller
@RequestMapping("/api/endpoints")
@RequiredArgsConstructor
public class EndpointController {
    private final Logger LOGGER = LoggerFactory.getLogger(EndpointController.class);
    private final EndpointService endpointService;
    private final NotificationTg notificationTg;

    @GetMapping("/check")
    public String checkAllEndpoints(Model model) {
        List<EndpointStatusDto> statusEndpoints = endpointService.checkAllEndpoints();

        List<String> roles = new ArrayList<>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            for (GrantedAuthority authority : auth.getAuthorities()) {
                roles.add(authority.getAuthority());
            }
        }
        String currentRole = roles.get(0);
        for (var se : statusEndpoints) {
            if (currentRole.contains(se.getRole())) {
                notificationTg.sendNotification(se.toString());
                LOGGER.info(se.toString());
            }
        }
        String formattedRole = currentRole.split("_")[1];
        if(!formattedRole.equals("admin")) {
            for (var endpoint : statusEndpoints) {
                if (endpoint.getRole().equals(formattedRole)) {
                    statusEndpoints.remove(endpoint);
                }
            }
        }

        model.addAttribute("list", statusEndpoints);

        return "check";
    }


}
