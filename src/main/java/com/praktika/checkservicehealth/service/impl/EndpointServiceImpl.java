package com.praktika.checkservicehealth.service.impl;

import com.praktika.checkservicehealth.dto.*;
import com.praktika.checkservicehealth.entity.Endpoint;
import com.praktika.checkservicehealth.repository.EndpointRepo;
import com.praktika.checkservicehealth.service.EndpointService;
import com.praktika.checkservicehealth.service.NotificationTg;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EndpointServiceImpl implements EndpointService {
    private final Logger LOGGER = LoggerFactory.getLogger(EndpointServiceImpl.class);
    private final EndpointRepo endpointRepo;
    private final NotificationTg notificationTg;
    private SavedDataDto savedDataDto;
    private List<EndpointStatusDto> endpointStatusDtos;


    @Value("${url.get_token.key}")
    String GET_TOKEN;
    @Value("${url.check_status.key}")
    String CHECK_STATUS;

    private final RestClient restClient = RestClient.create();

    @Override
    @Scheduled(fixedRate = 5000)
    public void checkAllEndpoints() {
        endpointStatusDtos = new ArrayList<>();
        LOGGER.info("ВЫЗВАНА ФУНКЦИЯ checkAllEndpoints()");
        List<Endpoint> endpoints = endpointRepo.findAll();

        endpoints.forEach(endpoint -> {
            LoginEndpointDto loginEndpointDto = new LoginEndpointDto(endpoint.getUsername(), endpoint.getPassword());

            LOGGER.info(endpoint.getUrl());

            try {
                TokenDto tokenDto = restClient.post()
                        .uri(endpoint.getUrl() + GET_TOKEN)
                        .header("Content-Type", "application/json")
                        .body(loginEndpointDto)
                        .retrieve()
                        .body(TokenDto.class);

                String token = tokenDto.getToken();
                AuthResponse authResponse = checkServiceAvailability(endpoint.getUrl(), token);

                LOGGER.info("authResponse: {}", authResponse);
                EndpointStatusDto endpointStatusDto = new EndpointStatusDto();
                endpointStatusDto.setRole(endpoint.getRole().getName());
                endpointStatusDto.setUrl(endpoint.getUrl());
                endpointStatusDto.setServices(new ArrayList<>());

                for (var s : authResponse.getServices()) {
                    LOGGER.info(s.toString());
                    if ("inactive".equals(s.getStatus())) {
                        String message = String.format("Сервис %s не работает на эндпоинте %s", s.getName(), endpoint.getUrl());
                        notificationTg.sendNotification(message);
                    }
                    endpointStatusDto.getServices().add(s);
                }
                endpointStatusDtos.add(endpointStatusDto);

            } catch (RestClientException e) {
                notificationTg.sendNotification(String.format("Сервис на эндпоинте %s не отвечает", endpoint.getUrl()));
                List<ServiceDto> list = new ArrayList<>();
                list.add(new ServiceDto("endpoint", "no connection"));
                endpointStatusDtos.add(new EndpointStatusDto(endpoint.getRole().getName(), endpoint.getUrl(), list));
                LOGGER.info("Error: " + e.getMessage());
            }
        });

        savedDataDto = new SavedDataDto(endpointStatusDtos, Instant.now());
    }

    private AuthResponse checkServiceAvailability(String url, String token) {
        return restClient.get()
                .uri(url + CHECK_STATUS)
                .header("token", token)
                .retrieve()
                .body(AuthResponse.class);
    }

    @Override
    public SavedDataDto getSavedData() {
        if (savedDataDto == null) {
           checkAllEndpoints();
        }
        return savedDataDto;
    }
}