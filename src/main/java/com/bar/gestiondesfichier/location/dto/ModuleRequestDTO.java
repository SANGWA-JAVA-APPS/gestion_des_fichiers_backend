package com.bar.gestiondesfichier.location.dto;

import lombok.Data;

@Data
public class ModuleRequestDTO {
    private String name;
    private String description;

    private String moduleCode;
    private String moduleType; // string, not enum (API-safe)

    private String coordinates;
    private Double areaSize;
    private String areaUnit;

    private Long locationEntityId; // REQUIRED
}