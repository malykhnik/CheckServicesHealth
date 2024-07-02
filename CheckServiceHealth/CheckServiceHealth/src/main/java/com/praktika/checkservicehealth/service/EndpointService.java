package com.praktika.checkservicehealth.service;

import com.praktika.checkservicehealth.dto.EndpointStatusDto;

import java.util.List;

public interface EndpointService {
    List<EndpointStatusDto> checkAllEndpoints();
}
