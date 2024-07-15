package com.praktika.checkservicehealth.repository;

import com.praktika.checkservicehealth.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepo extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String role);
}
