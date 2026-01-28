package com.online.voting.auth_service.dto.candidate;

import java.util.UUID;

import com.online.voting.auth_service.model.RegistrationStatus;
import com.online.voting.auth_service.model.Role;

public record CandidateResponse(
        UUID userId,
        String username,
        RegistrationStatus status,
        Role role) {
}
