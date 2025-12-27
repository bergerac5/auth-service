package com.online.voting.auth_service.dto;

public class LoginResponse {
    public String token;
    public String role;

    public LoginResponse(String token, String role) {
        this.token = token;
        this.role = role;
    }
}
