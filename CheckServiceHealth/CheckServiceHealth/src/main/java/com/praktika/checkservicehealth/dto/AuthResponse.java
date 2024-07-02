package com.praktika.checkservicehealth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {
    private String token;
    private List<ServiceDto> services;

    public AuthResponse(String token, String endpoint, String status) {
        this.token = token;
        this.services = Collections.singletonList(new ServiceDto(endpoint, status));
    }
}
