package com.online.voting.auth_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.online.voting.auth_service.dto.LoginRequest;
import com.online.voting.auth_service.dto.LoginResponse;
import com.online.voting.auth_service.models.User;
import com.online.voting.auth_service.security.JwtUtil;
import com.online.voting.auth_service.service.AuthUserService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthUserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthUserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        User user = userService.findByUsername(request.getUsername());

        if (user == null || !user.getPassword().equals(request.getPassword())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        return ResponseEntity.ok(new LoginResponse(token, user.getRole()));
    }
}
