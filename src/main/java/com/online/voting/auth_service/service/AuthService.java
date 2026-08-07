package com.online.voting.auth_service.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.online.voting.auth_service.dto.UserRegisterRequest;
import com.online.voting.auth_service.dto.UserUpdateRequest;
import com.online.voting.auth_service.exception.DuplicateResourceException;
import com.online.voting.auth_service.model.OutboxEvent;
import com.online.voting.auth_service.model.RegistrationStatus;
import com.online.voting.auth_service.model.User;
import com.online.voting.auth_service.repository.OutboxRepository;
import com.online.voting.auth_service.repository.UserRepository;
import com.online.voting.auth_service.security.JwtUtil;
import com.online.voting.events.UserCreatedEvent;
import com.online.voting.events.UserDeletedEvent;
import com.online.voting.events.UserUpdatedEvent;

import jakarta.transaction.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;

    // for crypting password
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtUtil jwtUtil;

    private final StreamBridge streamBridge;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OutboxRepository outboxRepository;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil, StreamBridge streamBridge,
            OutboxRepository outboxRepository) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.streamBridge = streamBridge;
        this.outboxRepository = outboxRepository;
    }

    // for registering a new user
    @Transactional
    public User register(UserRegisterRequest request) {

        // 1. Check username uniqueness (ACTIVE only)
        boolean exists = userRepository
                .existsByUsernameAndStatus(request.getUsername(), RegistrationStatus.ACTIVE);

        if (exists) {
            throw new DuplicateResourceException("Username already exists");
        }

        UUID correlationId = UUID.randomUUID();

        // 2. Create user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setStatus(RegistrationStatus.PENDING);

        User saved = userRepository.save(user);

        // 3. Create event
        UserCreatedEvent event = new UserCreatedEvent(
                UUID.randomUUID(), // eventId
                correlationId,
                saved.getId(),
                request.getNationalId(),
                saved.getUsername(),
                request.getFirstName(),
                request.getLastName(),
                saved.getRole().name());

        // 4. Outbox event (atomic with user)
        OutboxEvent outbox = new OutboxEvent();
        outbox.setAggregateType("USER");
        outbox.setAggregateId(saved.getId());
        outbox.setEventType("USER_CREATED");
        outbox.setPayload(convertToJson(event));
        outbox.setStatus("PENDING");
        outbox.setCreatedAt(Instant.now());
        outbox.setCorrelationId(correlationId); // IMPORTANT

        outboxRepository.save(outbox);

        return saved;
    }

    // update user
    @Transactional
    public Optional<User> updateUser(UUID id, UserUpdateRequest request) {
        return userRepository.findById(id).map(existingUser -> {
            // Update username if provided
            if (request.getUsername() != null && !request.getUsername().isBlank()) {
                userRepository.findByUsername(request.getUsername())
                        .filter(u -> !u.getId().equals(id))
                        .ifPresent(u -> {
                            throw new DuplicateResourceException("Username already exists");
                        });
                existingUser.setUsername(request.getUsername());
            }

            // Update role if provided
            if (request.getRole() != null) {
                existingUser.setRole(request.getRole());
            }

            existingUser.setStatus(RegistrationStatus.PENDING_UPDATE);
            User saved = userRepository.save(existingUser);

            // Build UserUpdatedEvent
            UserUpdatedEvent event = new UserUpdatedEvent(
                    saved.getId(),
                    saved.getUsername(),
                    request.getNationalId(),
                    request.getFirstName(),
                    request.getLastName(),
                    saved.getRole().name()); // enum -> string

            // Send event and check result
            boolean sent = streamBridge.send("userUpdated-out-0", event);
            if (!sent) {
                throw new IllegalStateException("Failed to publish UserUpdatedEvent");
            } else {
                System.out.println("UserUpdatedEvent published successfully:" + event);
            }

            return saved;
        });
    }

    // delete user
    public void deleteUser(UUID id) {
        boolean exists = userRepository.existsById(id);
        if (!exists) {
            throw new IllegalArgumentException("User not found");
        }
        userRepository.findById(id).ifPresent(user -> {
            userRepository.delete(user);

            // Emit UserDeleted event
            UserDeletedEvent event = new UserDeletedEvent();
            event.setUserId(user.getId());
            streamBridge.send("userDeleted-out-0", event);
        });

    }

    // login
    public String login(String username, String rawPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        // Generate JWT with role claim
        return jwtUtil.generateToken(user.getUsername(), user.getRole().name());
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> searchByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    //

    private String convertToJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
