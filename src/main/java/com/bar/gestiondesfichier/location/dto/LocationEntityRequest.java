package com.bar.gestiondesfichier.location.dto;

import lombok.Data;

@Data
public class LocationEntityRequest {
    private String name;
    private String description;
    private String code;
    private String postalCode;
    private String entityType;
    private Long countryId;
}