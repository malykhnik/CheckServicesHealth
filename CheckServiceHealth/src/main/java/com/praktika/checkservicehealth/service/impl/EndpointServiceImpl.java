package com.praktika.checkservicehealth.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    @Value("${url.endpoints.key}")
    String URL_ENDPOINTS;
    @Value("${url.tg.key}")
    String URL_TG;

    @Override
    @Scheduled(fixedRate = 5000) //каждые 5 сек
    public boolean checkAllEndpoints() {
        LOGGER.info("ВЫЗВАНА ФУНКЦИЯ checkAllEndpoints()");
        List<Endpoint> endpoints = endpointRepo.findAll();
        WebClient webClient = WebClient.create(URL_ENDPOINTS);
        for (Endpoint endpoint : endpoints) {
            try {
                LoginEndpointDto loginEndpointDto = new LoginEndpointDto(endpoint.getUsername(), endpoint.getPassword());
                ObjectMapper objectMapper = new ObjectMapper();
                String json = objectMapper.writeValueAsString(loginEndpointDto);

                Mono<ClientResponse> responseMono = webClient.post()
                        .uri(endpoint.getUrl())
                        .header("Content-Type", "application/json")
                        .bodyValue(json)
                        .exchange();

//                Mono<String> responseMono = webClient.post()
//                        .uri(endpoint.getUrl())
//                        .header("Content-Type", "application/json")
//                        .bodyValue(json)
//                        .retrieve()
//                        .bodyToMono(TokenDto.class)
//                        .map(TokenDto::token);

                Mono<ClientResponse> responseToken = webClient.get()
                        .uri(endpoint.getUrl())
                        .header("Content-Type", "application/json")
                        .exchange();

                responseMono.flatMap(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(String.class);
                    } else {
                        return response.createException().flatMap(Mono::error);
                    }
                }).subscribe(
                        responseBody -> LOGGER.info("Notification send"),
                        error -> {
                            sendNotification(endpoint.getUrl());
                            LOGGER.info("Error: " + error.getMessage());
                        }
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void sendNotification(String url) {
        try {
            WebClient webClient = WebClient.create(URL_TG);
            Mono<ClientResponse> responseMono = webClient.post()
                    .uri("/notify")
                    .header("Content-Type", "application/json")
                    .bodyValue("Endpoint на " + url + " недоступен!")
                    .exchange();
            responseMono.flatMap(response -> {
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
