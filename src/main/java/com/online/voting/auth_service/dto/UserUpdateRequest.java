package com.online.voting.auth_service.dto;

import com.online.voting.auth_service.model.Role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserUpdateRequest {
    @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters")
    private String username;

    private Role role;

    @NotBlank(message = "National ID is required")
    @Size(min = 16, max = 16)
    private String nationalId;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    public UserUpdateRequest() {
    }

    public UserUpdateRequest(String username, Role role, String nationalId, String firstName,
            String lastName) {
        this.username = username;
        this.role = role;
        this.nationalId = nationalId;
        this.firstName = firstName;
        this.lastName = lastName;
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

    public UserUpdateRequest username(String username) {
        setUsername(username);
        return this;
    }

    public UserUpdateRequest role(Role role) {
        setRole(role);
        return this;
    }

    public String getNationalId() {
        return this.nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
