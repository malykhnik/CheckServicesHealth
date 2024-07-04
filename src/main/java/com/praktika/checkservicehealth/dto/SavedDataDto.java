package com.praktika.checkservicehealth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class SavedDataDto {
    private List<EndpointStatusDto> statusEndpoints;
    private String time;

}
