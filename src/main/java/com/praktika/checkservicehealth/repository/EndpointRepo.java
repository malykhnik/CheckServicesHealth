package com.praktika.checkservicehealth.repository;

import com.praktika.checkservicehealth.entity.Endpoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EndpointRepo extends JpaRepository<Endpoint, Long> {
    Optional<Endpoint> findEndpointByUrl(String url);
    Optional<Endpoint> findEndpointByUsername(String username);
}
