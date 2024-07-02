package com.praktika.checkservicehealth.service;

import com.praktika.checkservicehealth.dto.EndpointStatusDto;
import com.praktika.checkservicehealth.dto.SavedDataDto;

import java.util.List;

public interface EndpointService {
    void checkAllEndpoints();
    SavedDataDto getSavedData();
}
