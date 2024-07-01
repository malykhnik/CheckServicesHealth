package com.praktika.checkservicehealth.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.praktika.checkservicehealth.dto.AuthResponse;
import com.praktika.checkservicehealth.dto.LoginEndpointDto;
import com.praktika.checkservicehealth.dto.TokenDto;
import com.praktika.checkservicehealth.entity.Endpoint;
import com.praktika.checkservicehealth.repository.EndpointRepo;
import com.praktika.checkservicehealth.service.EndpointService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EndpointServiceImpl implements EndpointService {
    private final Logger LOGGER = LoggerFactory.getLogger(EndpointServiceImpl.class);
    private final EndpointRepo endpointRepo;

    @Value("${url.tg.key}")
    String URL_TG;
    @Value("${url.tg_req.key}")
    String TG_REQUEST;
    @Value("${url.get_token.key}")
    String GET_TOKEN;
    @Value("${url.check_status.key}")
    String CHECK_STATUS;

    @Override
    @Scheduled(fixedRate = 30000)
    public void checkAllEndpoints() {
        LOGGER.info("ВЫЗВАНА ФУНКЦИЯ checkAllEndpoints()");
        List<Endpoint> endpoints = endpointRepo.findAll();
        for (Endpoint endpoint : endpoints) {
            try {
                LoginEndpointDto loginEndpointDto = new LoginEndpointDto(endpoint.getUsername(), endpoint.getPassword());
                ObjectMapper objectMapper = new ObjectMapper();

                LOGGER.info(endpoint.getUrl());

                WebClient.create(endpoint.getUrl()).post()
                        .uri(GET_TOKEN)
                        .header("Content-Type", "application/json")
                        .bodyValue(loginEndpointDto)
                        .retrieve()
                        .bodyToMono(TokenDto.class)
                        .subscribe(
                                response -> {
                                    try {







                                        String token = response.getToken();
                                        Mono<AuthResponse> authResponse = checkServiceAvailability(endpoint.getUrl(), token);
                                        LOGGER.info("authResponse: {}", authResponse);
                                        LOGGER.info("Response: {}", objectMapper.writeValueAsString(response.getToken()));
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                },
                                error -> {
                                    sendNotification(endpoint.getUrl());
                                    LOGGER.info("........../get_token");
                                    LOGGER.info("Error: " + error.getMessage());
                                }
                        );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Mono<AuthResponse> checkServiceAvailability(String url, String token) {
        return WebClient.create(url).get()
                .uri(CHECK_STATUS)
                .header("token", token)
                .retrieve()
                .bodyToMono(ClientResponse.class)
                .flatMap(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return Mono.just(new AuthResponse(token, url, "active"));
                    } else {
                        return Mono.just(new AuthResponse(token, url, "inactive"));
                    }
                });
    }

    private void sendNotification(String url) {
        try {
            WebClient.create(URL_TG).post()
                    .uri(TG_REQUEST)
                    .header("Content-Type", "application/json")
                    .bodyValue("Endpoint на " + url + " недоступен!")
                    .retrieve()
                    .bodyToMono(ClientResponse.class)
                    .flatMap(response -> {
                        if (response.statusCode().is2xxSuccessful()) {
                            return response.bodyToMono(String.class);
                        } else {
                            return response.createException().flatMap(Mono::error);
                        }
                    }).subscribe(
                            responseBody -> LOGGER.info("Notification send"),
                            error -> LOGGER.info("Error: " + error.getMessage())
                    );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
