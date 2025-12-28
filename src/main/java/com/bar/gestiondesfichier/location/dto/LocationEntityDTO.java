package com.bar.gestiondesfichier.location.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class LocationEntityDTO {
    private Long id;
    private String name;
    private String description;
    private String code;
    private String postalCode;
    private String entityType;
    private Long countryId;
    private String countryName;
    private String countryFlag;
}
