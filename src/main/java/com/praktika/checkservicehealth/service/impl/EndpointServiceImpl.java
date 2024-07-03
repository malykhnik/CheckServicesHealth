package com.praktika.checkservicehealth.service.impl;

import com.praktika.checkservicehealth.dto.*;
import com.praktika.checkservicehealth.entity.Endpoint;
import com.praktika.checkservicehealth.repository.EndpointRepo;
import com.praktika.checkservicehealth.service.EndpointService;
import com.praktika.checkservicehealth.service.NotificationTg;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EndpointServiceImpl implements EndpointService {
    private final Logger LOGGER = LoggerFactory.getLogger(EndpointServiceImpl.class);
    private final EndpointRepo endpointRepo;
    private final NotificationTg notificationTg;
    private SavedDataDto savedDataDto;
    private List<EndpointStatusDto> endpointStatusDtos;
    private final MailServiceImpl mailService;
    private final EndpointWithTimeDto endpointWithTimeDto = EndpointWithTimeDto.getInstance();
    private final RestClient restClient = RestClient.create();


    @Value("${url.get_token.key}")
    String GET_TOKEN;
    @Value("${url.check_status.key}")
    String CHECK_STATUS;

    @PostConstruct
    public void postConstruct() {
        endpointWithTimeDto.init(endpointRepo);
    }

    @Override
    @Scheduled(fixedRate = 60000)
    public void checkAllEndpoints() {
        endpointStatusDtos = new ArrayList<>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formattedTime = dtf.format(LocalDateTime.now());

        LOGGER.info("ВЫЗВАНА ФУНКЦИЯ checkAllEndpoints()");
        List<Endpoint> endpoints = endpointRepo.findAll();
        LOGGER.info(endpoints.toString());
        endpoints.forEach(endpoint -> {
            LOGGER.info("зашел в цикл");
            if (checkEndpointTimer(endpoint.getUsername())) {
                LOGGER.info("зашел в if");
                LoginEndpointDto loginEndpointDto = new LoginEndpointDto(endpoint.getUsername(), endpoint.getPassword());

                LOGGER.info(endpoint.getUrl());

                try {
                    TokenDto tokenDto = restClient.post()
                            .uri(endpoint.getUrl() + GET_TOKEN)
                            .header("Content-Type", "application/json")
                            .body(loginEndpointDto)
                            .retrieve()
                            .body(TokenDto.class);

                    assert tokenDto != null;
                    String token = tokenDto.getToken();
                    AuthResponse authResponse = checkServiceAvailability(endpoint.getUrl(), token);

                    LOGGER.info("authResponse: {}", authResponse);
                    EndpointStatusDto endpointStatusDto = new EndpointStatusDto();
                    endpointStatusDto.setRole(endpoint.getRole().getName());
                    endpointStatusDto.setUrl(endpoint.getUrl());
                    endpointStatusDto.setServices(new ArrayList<>());

                    for (ServiceDto service : authResponse.getServices()) {
                        LOGGER.info(service.toString());
                        if ("inactive".equals(service.getStatus())) {
                            String message = String.format("Сервис %s не работает на эндпоинте %s. %s", service.getName(), endpoint.getUrl(), formattedTime);
                            notificationTg.sendNotification(message);
                            mailService.sendMail(message);
                        }
                        endpointStatusDto.getServices().add(service);
                    }
                    endpointStatusDtos.add(endpointStatusDto);

                } catch (RestClientException e) {
                    String message = String.format("Сервис на эндпоинте %s не отвечает. %s", endpoint.getUrl(), formattedTime);
                    notificationTg.sendNotification(message);
                    mailService.sendMail(message);
                    List<ServiceDto> list = new ArrayList<>();
                    list.add(new ServiceDto("endpoint", "no connection"));
                    endpointStatusDtos.add(new EndpointStatusDto(endpoint.getRole().getName(), endpoint.getUrl(), list));
                    LOGGER.info("Error: " + e.getMessage());
                }
            } else {
                LOGGER.info("НЕ ЗАШЕЛ В if");
            }
        });

        savedDataDto = new SavedDataDto(endpointStatusDtos, formattedTime);
    }

    private AuthResponse checkServiceAvailability(String url, String token) {
        return restClient.get()
                .uri(url + CHECK_STATUS)
                .header("token", token)
                .retrieve()
                .body(AuthResponse.class);
    }

    private boolean checkEndpointTimer(String endpoint) {
        Map<String, TimeDto> map = EndpointWithTimeDto.getInstance().getTimeObj();
        if (map.get(endpoint).getLastVisit().plus(map.get(endpoint).getTimePeriod()).isBefore(Instant.now())) {
            map.get(endpoint).setLastVisit(Instant.now());
            return true;
        }
        return false;
    }

    @Override
    public SavedDataDto getSavedData() {
        if (savedDataDto == null) {
            checkAllEndpoints();
        }
        return savedDataDto;
    }
}