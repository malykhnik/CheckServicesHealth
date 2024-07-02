package com.praktika.checkservicehealth.service.impl;

import com.praktika.checkservicehealth.controller.EndpointController;
import com.praktika.checkservicehealth.service.NotificationTg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class NotificationTgImpl implements NotificationTg {
    private final Logger LOGGER = LoggerFactory.getLogger(NotificationTgImpl.class);
    @Value("${url.tg.key}")
    String URL_TG;
    @Value("${url.tg_req.key}")
    String TG_REQUEST;
    @Override
    public void sendNotification(String message) {
        try {
            WebClient.create(URL_TG).post()
                    .uri(TG_REQUEST)
                    .header("Content-Type", "application/json")
                    .bodyValue(message)
                    .retrieve()
                    .bodyToMono(ClientResponse.class)
                    .subscribe(
                            responseBody -> LOGGER.info("Notification send"),
                            error -> LOGGER.info("Error: " + error.getMessage())
                    );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
