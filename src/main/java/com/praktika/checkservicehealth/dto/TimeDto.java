package com.praktika.checkservicehealth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Duration;
import java.time.Instant;

@Data
@EqualsAndHashCode
@AllArgsConstructor
public class TimeDto {
    private Instant lastVisit;
    private Duration timePeriod;
}
