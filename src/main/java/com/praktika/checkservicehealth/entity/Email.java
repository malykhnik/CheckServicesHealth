package com.praktika.checkservicehealth.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "emails")
@Data
public class Email {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String receiver;
}
