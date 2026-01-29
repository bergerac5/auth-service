package com.online.voting.auth_service.dto.candidate;

import java.util.UUID;

import com.online.voting.auth_service.model.Role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateCandidateDto {
    private UUID userId;

    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters")
    private String username;

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

    public UpdateCandidateDto() {
    }

    public UpdateCandidateDto(UUID userId, String username, Role role, String nationalId, String firstName,
            String lastName, String party, String manifesto) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.nationalId = nationalId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.party = party;
        this.manifesto = manifesto;
    }

    public UUID getUserId() {
        return this.userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
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
