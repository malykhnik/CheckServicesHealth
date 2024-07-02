package com.praktika.checkservicehealth.repository;

import com.praktika.checkservicehealth.entity.Email;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailRepo extends JpaRepository<Email, Long> {
}
