package com.praktika.checkservicehealth.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EndpointServiceImpl implements EndpointService {
    private final Logger LOGGER = LoggerFactory.getLogger(EndpointServiceImpl.class);
    private final EndpointRepo endpointRepo;
    @Value("${url.endpoints.key}")
    String URL_ENDPOINTS;
    @Value("${url.tg.key}")
    String URL_TG;

    @Override
    @Scheduled(fixedRate = 5000) //каждые 5 сек
    public void checkAllEndpoints() {
        LOGGER.info("ВЫЗВАНА ФУНКЦИЯ checkAllEndpoints()");
        List<Endpoint> endpoints = endpointRepo.findAll();
        for (Endpoint endpoint : endpoints) {
            try {
                LoginEndpointDto loginEndpointDto = new LoginEndpointDto(endpoint.getUsername(), endpoint.getPassword());
                ObjectMapper objectMapper = new ObjectMapper();
                String json = objectMapper.writeValueAsString(loginEndpointDto);

                WebClient.create(URL_ENDPOINTS).post()
                        .uri(endpoint.getUrl())
                        .header("Content-Type", "application/json")
                        .bodyValue(json)
                        .retrieve()
                        .bodyToMono(JsonNode.class)
                        .flatMap(response -> {
                            if (response.get("token") != null) {
                                String token = response.get("token").asText();
                                return checkServiceAvailability(endpoint.getUrl(), token)
                                        .map(status -> new AuthResponse(token, endpoint.getUrl(), status));
                            } else {
                                return Mono.error(new RuntimeException("Токен не найден"));
                            }
                        })
                        .subscribe(
                                endpointStatus -> {
                                    try {
                                        LOGGER.info("Response: {}", objectMapper.writeValueAsString(endpointStatus));
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                },
                                error -> {
                                    sendNotification(endpoint.getUrl());
                                    LOGGER.info("Error: " + error.getMessage());
                                }
                        );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Mono<String> checkServiceAvailability(String url, String token) {
        return WebClient.create(URL_ENDPOINTS).get()
                .uri(url)
                .header("token", token)
                .retrieve()
                .bodyToMono(ClientResponse.class)
                .flatMap(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return Mono.just("active");
                    } else {
                        return Mono.just("inactive");
                    }
                });
    }

    private void sendNotification(String url) {
        try {
            WebClient.create(URL_TG).post()
                    .uri("/notify")
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
