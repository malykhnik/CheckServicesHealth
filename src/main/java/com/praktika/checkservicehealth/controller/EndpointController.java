package com.praktika.checkservicehealth.controller;

import com.praktika.checkservicehealth.dto.EndpointStatusDto;
import com.praktika.checkservicehealth.dto.OutputDataDto;
import com.praktika.checkservicehealth.dto.SavedDataDto;
import com.praktika.checkservicehealth.entity.Endpoint;
import com.praktika.checkservicehealth.service.EndpointService;
import com.praktika.checkservicehealth.service.NotificationTg;
import com.praktika.checkservicehealth.service.impl.EndpointServiceImpl;
import com.praktika.checkservicehealth.utils.WorkWithAuth;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;


@Controller
@RequestMapping("/api/endpoints")
@RequiredArgsConstructor
public class EndpointController {
    private final Logger LOGGER = LoggerFactory.getLogger(EndpointController.class);
    private final EndpointService endpointService;

    @GetMapping("/check")
    public String checkAllEndpoints(Model model) {
        List<OutputDataDto> list = endpointService.getSavedData();

        String currentRole = WorkWithAuth.getCurrentRole();
        System.out.println(list);
        List<OutputDataDto> outputList = new ArrayList<>();
        String formattedRole = currentRole.split("_")[1];
        if (!formattedRole.equals("admin")) {
            for (OutputDataDto output : list) {
                if (output.getRole().equals(formattedRole)) {
                    outputList.add(output);
                }
            }
        } else {
            outputList = list;
        }
        System.out.println(outputList);
        model.addAttribute("list", outputList);
        return "check";
    }

    @GetMapping("/checkByUrl/{url}")
    public String checkEndpointByUrl(@PathVariable String url) {
        endpointService.checkEndpointByUrl(url);
        return "redirect:/api/endpoints/check";
    }
}
