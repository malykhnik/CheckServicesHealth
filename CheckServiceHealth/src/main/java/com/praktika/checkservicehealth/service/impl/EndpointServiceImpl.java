package com.praktika.checkservicehealth.service.impl;

import com.praktika.checkservicehealth.entity.Endpoint;
import com.praktika.checkservicehealth.repository.EndpointRepo;
import com.praktika.checkservicehealth.service.EndpointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EndpointServiceImpl implements EndpointService {

    private final EndpointRepo endpointRepo;
    @Override
    public List<Endpoint> getAllEndpoints() {
        return endpointRepo.findAll();
    }
}
