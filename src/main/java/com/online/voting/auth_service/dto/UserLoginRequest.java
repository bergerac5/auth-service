package com.online.voting.auth_service.dto;

import jakarta.validation.constraints.NotBlank;

public class UserLoginRequest {
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    public UserLoginRequest() {
    }

    public UserLoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserLoginRequest username(String username) {
        setUsername(username);
        return this;
    }

    public UserLoginRequest password(String password) {
        setPassword(password);
        return this;
    }

}
