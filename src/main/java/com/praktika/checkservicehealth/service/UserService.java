package com.praktika.checkservicehealth.service;

import com.praktika.checkservicehealth.dto.UserDto;
import com.praktika.checkservicehealth.entity.User;

public interface UserService {
    User findByUsername(String username);

    void saveUser(UserDto userDto);
}
