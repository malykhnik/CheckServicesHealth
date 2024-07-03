package com.praktika.checkservicehealth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CrudStatusDto {
    private boolean create;
    private boolean read;
    private boolean update;
    private boolean delete;
}
