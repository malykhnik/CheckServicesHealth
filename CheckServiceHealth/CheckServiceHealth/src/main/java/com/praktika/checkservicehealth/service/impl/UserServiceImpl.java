package com.praktika.checkservicehealth.service.impl;

import com.praktika.checkservicehealth.dto.UserDto;
import com.praktika.checkservicehealth.entity.Role;
import com.praktika.checkservicehealth.entity.User;
import com.praktika.checkservicehealth.repository.RoleRepo;
import com.praktika.checkservicehealth.repository.UserRepo;
import com.praktika.checkservicehealth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final BCryptPasswordEncoder encoder;

    @Override
    public User findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    @Override
    public void saveUser(UserDto userDto) {
        Role role = roleRepo.findByName("user");
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(encoder.encode(userDto.getPassword()));
        user.setRole(role);

        userRepo.save(user);
    }
}
