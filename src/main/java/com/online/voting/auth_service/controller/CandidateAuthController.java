package com.online.voting.auth_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.online.voting.auth_service.dto.candidate.CandidateResponse;
import com.online.voting.auth_service.dto.candidate.CreateCandidateDto;
import com.online.voting.auth_service.service.candidateAuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth/candidates")
public class CandidateAuthController {

    private final candidateAuthService candidateAuthService;

    public CandidateAuthController(candidateAuthService candidateAuthService) {
        this.candidateAuthService = candidateAuthService;
    }

    /**
     * Register a new candidate (status = PENDING)
     */
    @PostMapping
    public ResponseEntity<?> registerCandidate(
            @Valid @RequestBody CreateCandidateDto request) {

        try {
            CandidateResponse response = candidateAuthService.registerCandidate(request);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);
        } catch (RuntimeException ex) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ex.getMessage());
        }
    }
}
