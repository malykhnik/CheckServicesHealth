package com.praktika.checkservicehealth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class SavedDataDto {
    private List<EndpointStatusDto> statusEndpoints;
    private String time;

    public SavedDataDto(Map<String, TimeDto> timeObj, String time) {
        List<EndpointStatusDto> list = new ArrayList<>();
        for (Map.Entry<String, TimeDto> entry : EndpointWithTimeDto.getInstance().getTimeObj().entrySet()) {
            list.add(entry.getValue().getEndpoint());
        }
        this.statusEndpoints = list;
        this.time = time;
    }
}
