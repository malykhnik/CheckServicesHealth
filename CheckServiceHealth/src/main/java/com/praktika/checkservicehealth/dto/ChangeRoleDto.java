package com.praktika.checkservicehealth.dto;

import lombok.*;

@Builder
public record ChangeRoleDto(String username, String role) { }
