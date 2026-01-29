package com.online.voting.auth_service.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.online.voting.auth_service.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    // Add custom query methods here if needed
    Optional<User> findByUsername(String username);

}
