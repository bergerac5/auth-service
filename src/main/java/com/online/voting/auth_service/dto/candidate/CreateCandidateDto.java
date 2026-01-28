package com.online.voting.auth_service.dto.candidate;

import com.online.voting.auth_service.model.Role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateCandidateDto {

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

    private String party;
    private String manifesto;

    public CreateCandidateDto() {
    }

    public CreateCandidateDto(String username, String password, Role role, String nationalId, String firstName,
            String lastName, String party, String manifesto) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.nationalId = nationalId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.party = party;
        this.manifesto = manifesto;
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

    public Role getRole() {
        return this.role;
    }

    public void setRole(Role role) {
        this.role = role;
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

    public String getParty() {
        return this.party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public String getManifesto() {
        return this.manifesto;
    }

    public void setManifesto(String manifesto) {
        this.manifesto = manifesto;
    }
}
