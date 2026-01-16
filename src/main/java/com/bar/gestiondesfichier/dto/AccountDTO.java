package com.bar.gestiondesfichier.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountDTO {

    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String gender;

    private Long categoryId;
    private String categoryName;

    private boolean active;

    private Long countryId;
    private String countryName;

    private Long locationEntityId;
    private String locationEntityName;

    public AccountDTO(
            Long id,
            String username,
            String email,
            String fullName,
            String phoneNumber,
            String gender,
            Long categoryId,
            String categoryName,
            boolean active,
            Long countryId,
            String countryName,
            Long locationEntityId,
            String locationEntityName
    ) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.active = active;
        this.countryId = countryId;
        this.countryName = countryName;
        this.locationEntityId = locationEntityId;
        this.locationEntityName = locationEntityName;
    }
}
