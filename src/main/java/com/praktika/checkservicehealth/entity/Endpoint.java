package com.praktika.checkservicehealth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;

@Entity
@Table(name = "endpoints")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Endpoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    private String url;

    private Long period;

    public Duration getPeriod() {
        return Duration.ofSeconds(period);
    }

}
