package com.praktika.checkservicehealth.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.praktika.checkservicehealth.dto.*;
import com.praktika.checkservicehealth.entity.Endpoint;
import com.praktika.checkservicehealth.repository.EndpointRepo;
import com.praktika.checkservicehealth.service.EndpointService;
import com.praktika.checkservicehealth.service.NotificationTg;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.ContentHandler;
import java.util.ArrayList;
import java.util.List;

@Service
@Data
@RequiredArgsConstructor
public class EndpointServiceImpl implements EndpointService {
    private final Logger LOGGER = LoggerFactory.getLogger(EndpointServiceImpl.class);
    private final EndpointRepo endpointRepo;
    private final NotificationTg notificationTg;
    private List<EndpointStatusDto> endpointStatusDtos;

    @Value("${url.get_token.key}")
    String GET_TOKEN;
    @Value("${url.check_status.key}")
    String CHECK_STATUS;

    @Override
    public List<EndpointStatusDto> checkAllEndpoints() {
        endpointStatusDtos = new ArrayList<>();
        LOGGER.info("ВЫЗВАНА ФУНКЦИЯ checkAllEndpoints()");
        List<Endpoint> endpoints = endpointRepo.findAll();
        endpoints.forEach(endpoint -> {
            LoginEndpointDto loginEndpointDto = new LoginEndpointDto(endpoint.getUsername(), endpoint.getPassword());

            LOGGER.info(endpoint.getUrl());

            WebClient.create(endpoint.getUrl()).post()
                    .uri(GET_TOKEN)
                    .header("Content-Type", "application/json")
                    .bodyValue(loginEndpointDto)
                    .retrieve()
                    .bodyToMono(TokenDto.class)
                    .flatMap(response -> {
                        String token = response.getToken();
                        return checkServiceAvailability(endpoint.getUrl(), token);
                    })
                    .subscribe(
                            authResponse -> {
                                LOGGER.info("authResponse: {}", authResponse);
                                EndpointStatusDto endpointStatusDto = new EndpointStatusDto();
                                endpointStatusDto.setRole(endpoint.getRole().getName());
                                endpointStatusDto.setUrl(endpoint.getUrl());
                                for (var s : authResponse.getServices()) {
                                    LOGGER.info(s.toString());
                                    if ("inactive".equals(s.getStatus())) {
                                        String message = String.format("Сервис %s не работает на эндпоинте %s", s.getName(), endpoint.getUrl());
                                        notificationTg.sendNotification(message);
                                    }
                                    endpointStatusDto.getServices().add(s);

                                }
                                endpointStatusDtos.add(endpointStatusDto);
                                LOGGER.info(endpointStatusDtos.toString()+ "sdjasdfasd");
                            },
                            error -> {
                                notificationTg.sendNotification(String.format("Сервис на эндпоинте %s не отвечает", endpoint.getUrl()));
                                List<ServiceDto> list = new ArrayList<>();
                                list.add(new ServiceDto("endpoint", "no connection"));
                                endpointStatusDtos.add(new EndpointStatusDto(endpoint.getRole().getName(),endpoint.getUrl(), list));
                                LOGGER.info("Error: " + error.getMessage());
                            }

                    );
        });
        return endpointStatusDtos;
    }

    private Mono<AuthResponse> checkServiceAvailability(String url, String token) {
        return WebClient.create(url).get()
                .uri(CHECK_STATUS)
                .header("token", token)
                .retrieve()
                .bodyToMono(AuthResponse.class);
    }

}
