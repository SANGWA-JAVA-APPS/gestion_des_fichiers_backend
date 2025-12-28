package com.bar.gestiondesfichier.location.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ModuleResponseDTO {
    private Long id;
    private String name;
    private String description;

    private String moduleCode;
    private String moduleType;

    private String coordinates;
    private Double areaSize;
    private String areaUnit;

    private Long locationEntityId;
    private String locationEntityName;




}