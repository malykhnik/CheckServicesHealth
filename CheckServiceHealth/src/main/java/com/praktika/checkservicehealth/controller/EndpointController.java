package com.praktika.checkservicehealth.controller;

import com.praktika.checkservicehealth.entity.Endpoint;
import com.praktika.checkservicehealth.service.EndpointService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/endpoints")
@RequiredArgsConstructor
public class EndpointController {

    private final EndpointService endpointService;

    @GetMapping
    public ResponseEntity<List<Endpoint>> getAllEndpoints() {
        return ResponseEntity.ok().body(endpointService.getAllEndpoints());
    }
}
