package com.bar.gestiondesfichier.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginResponseDto {
    // Getters and Setters
    private boolean success;
    private String message;
    private Long userId;
    private String username;
    private String fullName;
    private String email;
    private String role;
    private String token;
    private String refreshToken;
    
    // Default constructor
    public LoginResponseDto() {}
    
    public LoginResponseDto(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public LoginResponseDto(boolean success, String message, Long userId, String username, 
                           String fullName, String email, String role, String token, String refreshToken) {
        this.success = success;
        this.message = message;
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.token = token;
        this.refreshToken = refreshToken;
    }

}