package com.online.voting.auth_service.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.online.voting.auth_service.dto.UserRegisterRequest;
import com.online.voting.auth_service.dto.UserUpdateRequest;
import com.online.voting.auth_service.model.RegistrationStatus;
import com.online.voting.auth_service.model.User;
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

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil, StreamBridge streamBridge) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.streamBridge = streamBridge;
    }

    // for registering a new user
    @Transactional
    public User register(UserRegisterRequest request) {

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setStatus(RegistrationStatus.PENDING);

        User saved = userRepository.save(user);

        UserCreatedEvent event = new UserCreatedEvent(
                saved.getId(),
                request.getNationalId(),
                saved.getUsername(),
                request.getFirstName(),
                request.getLastName(),
                saved.getRole().name());

        boolean sent = streamBridge.send("userCreated-out-0", event);
        if (!sent) {
            throw new IllegalStateException("Failed to publish UserCreatedEvent");
        } else {
            System.out.println("UserCreatedEvent published successfully:" + event);
        }

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
                            throw new IllegalArgumentException("Username already exists");
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

}
