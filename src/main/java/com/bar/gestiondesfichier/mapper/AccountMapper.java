package com.bar.gestiondesfichier.mapper;

import com.bar.gestiondesfichier.dto.AccountDTO;
import com.bar.gestiondesfichier.entity.Account;

public class AccountMapper {

    public static AccountDTO toDTO(Account account) {

        Long categoryId = null;
        String categoryName = null;

        Long countryId = null;
        String countryName = null;

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
            }
        }

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
                locationEntityId,
                locationEntityName
        );
    }
}
