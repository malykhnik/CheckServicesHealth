package com.praktika.checkservicehealth.controller;

import com.praktika.checkservicehealth.dto.ChangeRoleDto;
import com.praktika.checkservicehealth.entity.Role;
import com.praktika.checkservicehealth.entity.User;
import com.praktika.checkservicehealth.repository.RoleRepo;
import com.praktika.checkservicehealth.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;

    @PostMapping("/changeRole")
    public ResponseEntity<Object> changeRoleOfUser(@RequestBody ChangeRoleDto changeRoleDto) {
        Optional<User> userOptional = Optional.ofNullable(userRepo.findByUsername(changeRoleDto.username()));
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Пользователя с таким username не существует");
        }
        Optional<Role> roleOptional = Optional.ofNullable(roleRepo.findByName(changeRoleDto.role()));
        if (roleOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Такой роли не существует!");
        }
        User user = userOptional.get();

        if (user.getRole().getName().equals("admin")) {
            return ResponseEntity.badRequest().body("Вы не можете изменять роль админу!");
        }

        Role role = roleOptional.get();
        user.setRole(role);
        userRepo.save(user);
        return ResponseEntity.ok().body("Роль пользователя изменена успешно!");
    }
}

