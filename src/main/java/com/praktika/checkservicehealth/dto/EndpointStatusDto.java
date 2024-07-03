package com.praktika.checkservicehealth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EndpointStatusDto {
    private String role;
    private String url;
    private List<ServiceDto> services;

}
