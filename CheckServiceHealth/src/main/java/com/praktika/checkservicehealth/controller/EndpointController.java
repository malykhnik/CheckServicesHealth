package com.praktika.checkservicehealth.controller;

import com.praktika.checkservicehealth.service.EndpointService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/endpoints")
@RequiredArgsConstructor
public class EndpointController {

    private final EndpointService endpointService;

    @GetMapping("/check")
    public void checkAllEndpoints() {
        endpointService.checkAllEndpoints();
    }
}
