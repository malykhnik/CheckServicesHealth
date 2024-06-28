package com.praktika.checkservicehealth.dto;

import java.util.Collections;
import java.util.List;

public class AuthResponse {
    private String token;
    private List<ServiceDto> services;

    public AuthResponse(String token, String endpoint, String status) {
        this.token = token;
        this.services = Collections.singletonList(new ServiceDto(endpoint, status));
    }
}
