package com.online.voting.auth_service.dto;

import com.online.voting.auth_service.model.Role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserRegisterRequest {
    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    private Role role; // must be ADMIN, VOTER, or CANDIDATE

    @NotBlank(message = "National ID is required")
    @Size(min = 16, max = 16)
    private String nationalId;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    public UserRegisterRequest() {
    }

    public UserRegisterRequest(String username, String password, Role role, String nationalId, String firstName,
            String lastName) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.nationalId = nationalId;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public String getNationalId() {
        return nationalId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}