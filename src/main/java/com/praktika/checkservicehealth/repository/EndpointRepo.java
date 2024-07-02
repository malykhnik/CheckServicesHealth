package com.praktika.checkservicehealth.repository;

import com.praktika.checkservicehealth.entity.Endpoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EndpointRepo extends JpaRepository<Endpoint, Long> {
}
