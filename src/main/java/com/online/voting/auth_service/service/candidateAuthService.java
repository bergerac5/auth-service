package com.online.voting.auth_service.service;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.online.voting.auth_service.dto.candidate.CandidateResponse;
import com.online.voting.auth_service.dto.candidate.CreateCandidateDto;
import com.online.voting.auth_service.model.RegistrationStatus;
import com.online.voting.auth_service.model.User;
import com.online.voting.auth_service.repository.UserRepository;
import com.online.voting.auth_service.security.JwtUtil;
import com.online.voting.events.candidate.CandidateCreationEvent;

@Service
public class candidateAuthService {

    private final UserRepository userRepository;

    // for crypting password
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtUtil jwtUtil;

    private final StreamBridge streamBridge;

    public candidateAuthService(UserRepository userRepository, JwtUtil jwtUtil, StreamBridge streamBridge) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.streamBridge = streamBridge;
    }

    // ---- register candidate ----
    public CandidateResponse registerCandidate(CreateCandidateDto request) {

        try {
            if (userRepository.findByUsername(request.getUsername()).isPresent()) {
                throw new RuntimeException("User already exists");
            }

            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setStatus(RegistrationStatus.PENDING);
            user.setRole(request.getRole());

            User saved = userRepository.save(user);

            CandidateCreationEvent event = new CandidateCreationEvent(
                    saved.getId(),
                    request.getNationalId(),
                    saved.getUsername(),
                    request.getFirstName(),
                    request.getLastName(),
                    request.getParty(),
                    request.getManifesto(),
                    saved.getRole().name());

            boolean sent = streamBridge.send("candidateCreated-out-0", event);
            if (!sent) {
                throw new RuntimeException("Failed to publish CandidateCreationEvent");
            } else {
                // Event published successfully
                System.out.println("CandidateCreationEvent published successfully:" + event);
            }

            return new CandidateResponse(
                    saved.getId(),
                    saved.getUsername(),
                    saved.getStatus(),
                    saved.getRole());

        } catch (Exception e) {
            throw new RuntimeException("Failed to register candidate: " + e.getMessage());
        }
    }

}
