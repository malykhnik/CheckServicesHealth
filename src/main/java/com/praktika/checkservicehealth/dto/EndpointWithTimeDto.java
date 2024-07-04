package com.praktika.checkservicehealth.dto;

import com.praktika.checkservicehealth.entity.Endpoint;
import com.praktika.checkservicehealth.repository.EndpointRepo;
import com.praktika.checkservicehealth.service.impl.EndpointServiceImpl;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Data
public class EndpointWithTimeDto {
    private static final Logger LOGGER = LoggerFactory.getLogger(EndpointWithTimeDto.class);
    private static EndpointWithTimeDto INSTANCE;
    private final Map<String, TimeDto> timeObj;

    private EndpointWithTimeDto() {
        timeObj = new HashMap<>();
    }

    public static EndpointWithTimeDto getInstance() {
        if (INSTANCE == null) {
            LOGGER.info("СОЗДАН НОВЫЙ INSTANCE");
            INSTANCE = new EndpointWithTimeDto();
        }
        return INSTANCE;
    }

    public void init(EndpointRepo endpointRepo) {
        List<Endpoint> endpointsList = endpointRepo.findAll();
        endpointsList.forEach(endpoint -> {
            timeObj.put(endpoint.getUsername(), new TimeDto(new EndpointStatusDto(),Instant.now(), endpoint.getPeriod()));
        });
    }

    public void updateMap(String endpoint, EndpointStatusDto endpointStatusDto) {
        this.timeObj.get(endpoint).setEndpoint(endpointStatusDto);
    }

}