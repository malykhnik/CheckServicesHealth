package com.praktika.checkservicehealth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
public class SavedDataDto {
    private List<EndpointStatusDto> statusEndpoints;
    private Instant time;
}
