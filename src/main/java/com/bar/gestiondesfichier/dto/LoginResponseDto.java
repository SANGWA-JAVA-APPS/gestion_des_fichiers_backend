package com.bar.gestiondesfichier.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginResponseDto {

    private boolean success;
    private String message;

    private AccountDTO account;   // FULL USER PROFILE (country + entity + role)

    private String token;
    private String refreshToken;

    public LoginResponseDto() {}

    public LoginResponseDto(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public LoginResponseDto(boolean success, String message, AccountDTO account, String token, String refreshToken) {
        this.success = success;
        this.message = message;
        this.account = account;
        this.token = token;
        this.refreshToken = refreshToken;
    }
}
