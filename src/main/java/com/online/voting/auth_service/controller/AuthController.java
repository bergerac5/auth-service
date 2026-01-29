package com.online.voting.auth_service.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.online.voting.auth_service.dto.ApiResponse;
import com.online.voting.auth_service.dto.UserLoginRequest;
import com.online.voting.auth_service.dto.UserRegisterRequest;
import com.online.voting.auth_service.dto.UserResponse;
import com.online.voting.auth_service.dto.UserUpdateRequest;
import com.online.voting.auth_service.model.User;
import com.online.voting.auth_service.repository.UserRepository;
import com.online.voting.auth_service.service.AuthService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/auth")
public class AuthController {

        private final AuthService authService;
        private final UserRepository userRepository;

        public AuthController(AuthService authService, UserRepository userRepository) {
                this.authService = authService;
                this.userRepository = userRepository;
        }

        @PostMapping("/register")
        public ResponseEntity<ApiResponse<UserResponse>> register(
                        @Valid @RequestBody UserRegisterRequest request) {

                User savedUser = authService.register(request);

                UserResponse response = new UserResponse(
                                savedUser.getId(),
                                savedUser.getUsername(),
                                savedUser.getRole(),
                                null);

                return ResponseEntity.ok(
                                ApiResponse.success("User registered successfully", response));
        }

        @PostMapping("/login")
        public ResponseEntity<ApiResponse<UserResponse>> login(
                        @Valid @RequestBody UserLoginRequest request) {

                String token = authService.login(request.getUsername(), request.getPassword());
                User user = authService.findByUsername(request.getUsername());

                UserResponse response = new UserResponse(
                                user.getId(),
                                user.getUsername(),
                                user.getRole(),
                                token);

                return ResponseEntity.ok(
                                ApiResponse.success("Login successful", response));
        }

        @PatchMapping("/update/{id}")
        public ResponseEntity<ApiResponse<UserResponse>> updateUser(
                        @PathVariable UUID id,
                        @Valid @RequestBody UserUpdateRequest request) {

                User user = authService.updateUser(id, request)
                                .orElseThrow(() -> new IllegalArgumentException("User not found"));

                UserResponse response = new UserResponse(
                                user.getId(),
                                user.getUsername(),
                                user.getRole(),
                                null);

                return ResponseEntity.ok(
                                ApiResponse.success("User updated successfully", response));
        }

        @DeleteMapping("/delete/{id}")
        public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable UUID id) {

                boolean existsById = userRepository.existsById(id);
                if (existsById) {
                        authService.deleteUser(id);
                        return ResponseEntity.ok(
                                        ApiResponse.success("User deleted successfully", null));
                } else {
                        return ResponseEntity
                                        .status(HttpStatus.NOT_FOUND)
                                        .body(ApiResponse.error("User not found"));
                }

        }

        @GetMapping("/users")
        public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {

                List<UserResponse> users = authService.getAllUsers().stream()
                                .map(u -> new UserResponse(u.getId(), u.getUsername(), u.getRole(), null))
                                .collect(Collectors.toList());

                return ResponseEntity.ok(
                                ApiResponse.success("Users retrieved successfully", users));
        }

        @GetMapping("/users/{username}")
        public ResponseEntity<UserResponse> searchUser(@PathVariable String username) {
                Optional<User> userOpt = authService.searchByUsername(username);
                if (userOpt.isPresent()) {
                        User user = userOpt.get();
                        UserResponse response = new UserResponse(user.getId(), user.getUsername(), user.getRole(),
                                        null);
                        return ResponseEntity.ok(response);
                } else {
                        return ResponseEntity.notFound().build();
                }
        }

}
