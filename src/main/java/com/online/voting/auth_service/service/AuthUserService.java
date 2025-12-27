package com.online.voting.auth_service.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.online.voting.auth_service.models.User;

@Service
public class AuthUserService {

    private static final Map<String, User> USERS = Map.of(
        "admin", new User("admin", "admin123", "ADMIN"),
        "voter1", new User("voter1", "voter123", "VOTER")
    );

    public User findByUsername(String username) {
        return USERS.get(username);
    }

        
}
