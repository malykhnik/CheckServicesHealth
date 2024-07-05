package com.praktika.checkservicehealth.dto;

import lombok.Data;

import java.util.List;

@Data
public class OutputDataDto {
    private String role;
    private String url;
    private List<ServiceDto> services;
    private String time;
}
