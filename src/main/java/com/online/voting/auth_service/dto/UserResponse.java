package com.online.voting.auth_service.dto;

import java.util.UUID;

import com.online.voting.auth_service.model.Role;

public class UserResponse {
    private UUID id;
    private String username;
    private Role role;
    private String token; // optional, only for login responses

    public UserResponse() {
    }

    public UserResponse(UUID id, String username, Role role, String token) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.token = token;

    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Role getRole() {
        return this.role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserResponse id(UUID id) {
        setId(id);
        return this;
    }

    public UserResponse username(String username) {
        setUsername(username);
        return this;
    }

    public UserResponse role(Role role) {
        setRole(role);
        return this;
    }

    public UserResponse token(String token) {
        setToken(token);
        return this;
    }

}
