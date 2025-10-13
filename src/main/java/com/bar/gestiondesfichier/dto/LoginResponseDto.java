package com.bar.gestiondesfichier.dto;

public class LoginResponseDto {
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

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}