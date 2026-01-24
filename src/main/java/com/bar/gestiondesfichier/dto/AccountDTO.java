package com.bar.gestiondesfichier.dto;

import com.bar.gestiondesfichier.document.dto.PermissionDTO;
import com.bar.gestiondesfichier.document.dto.SectionCategoryDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
    private String countryIsoCode;
    private String countryFlagUrl;

    private Long locationEntityId;
    private String locationEntityName;

    private Set<SectionCategoryDTO> sectionCategories;
    private Set<PermissionDTO> permissions;
}
