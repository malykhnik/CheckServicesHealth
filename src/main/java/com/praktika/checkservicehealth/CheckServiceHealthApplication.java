package com.praktika.checkservicehealth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
public class CheckServiceHealthApplication {

    public static void main(String[] args) {
        SpringApplication.run(CheckServiceHealthApplication.class, args);
    }

}
