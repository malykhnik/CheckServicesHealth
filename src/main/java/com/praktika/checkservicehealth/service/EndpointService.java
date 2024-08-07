package com.praktika.checkservicehealth.service;

import com.praktika.checkservicehealth.dto.EndpointStatusDto;
import com.praktika.checkservicehealth.dto.OutputDataDto;
import com.praktika.checkservicehealth.dto.SavedDataDto;

import java.util.List;

public interface EndpointService {
    void checkAllEndpoints();
    void checkEndpointByUrl(String url);
    List<OutputDataDto> getSavedData();
}
