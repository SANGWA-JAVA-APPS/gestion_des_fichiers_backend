package com.bar.gestiondesfichier.mapper;

import com.bar.gestiondesfichier.document.dto.SectionCategoryDTO;
import com.bar.gestiondesfichier.document.dto.PermissionDTO;
import com.bar.gestiondesfichier.dto.AccountDTO;
import com.bar.gestiondesfichier.entity.Account;
import com.bar.gestiondesfichier.document.model.SectionCategory;
import com.bar.gestiondesfichier.location.model.Permission;


import java.util.Set;
import java.util.stream.Collectors;

public class AccountMapper {

    public static AccountDTO toDTO(Account account) {

        Long categoryId = null;
        String categoryName = null;

        Long countryId = null;
        String countryName = null;
        String countryIsoCode = null;
        String countryFlagUrl = null;

        Long locationEntityId = null;
        String locationEntityName = null;

        if (account.getAccountCategory() != null) {
            categoryId = account.getAccountCategory().getId();
            categoryName = account.getAccountCategory().getName();
        }

        if (account.getLocationEntity() != null) {
            locationEntityId = account.getLocationEntity().getId();
            locationEntityName = account.getLocationEntity().getName();

            if (account.getLocationEntity().getCountry() != null) {
                countryId = account.getLocationEntity().getCountry().getId();
                countryName = account.getLocationEntity().getCountry().getName();
                countryIsoCode = account.getLocationEntity().getCountry().getIsoCode();
                countryFlagUrl = account.getLocationEntity().getCountry().getFlagUrl();
            }
        }

        // Map section categories
        Set<SectionCategoryDTO> sectionCategories =
                account.getSectionCategories() == null
                        ? Set.of()
                        : account.getSectionCategories()
                        .stream()
                        .map(AccountMapper::toSectionCategoryDTO)
                        .collect(Collectors.toSet());

        // Map permissions
        Set<PermissionDTO> permissions =
                account.getPermissions() == null
                        ? Set.of()
                        : account.getPermissions()
                        .stream()
                        .map(AccountMapper::toPermissionDTO)
                        .collect(Collectors.toSet());

        return new AccountDTO(
                account.getId(),
                account.getUsername(),
                account.getEmail(),
                account.getFullName(),
                account.getPhoneNumber(),
                account.getGender(),
                categoryId,
                categoryName,
                account.isActive(),
                countryId,
                countryName,
                countryIsoCode,
                countryFlagUrl,
                locationEntityId,
                locationEntityName,
                sectionCategories,
                permissions
        );
    }

    private static SectionCategoryDTO toSectionCategoryDTO(SectionCategory sectionCategory) {
        return new SectionCategoryDTO(
                sectionCategory.getId(),
                sectionCategory.getName(),
                sectionCategory.getCode()
        );
    }

    private static PermissionDTO toPermissionDTO(Permission permission) {
        return new PermissionDTO(
                permission.getId(),
                permission.getName(),
                permission.getCode(),
                permission.getBlock() != null ? permission.getBlock().getName() : null
        );
    }
}
