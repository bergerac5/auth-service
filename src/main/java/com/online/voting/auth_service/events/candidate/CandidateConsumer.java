package com.online.voting.auth_service.events.candidate;

import java.util.function.Consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.online.voting.auth_service.model.RegistrationStatus;
import com.online.voting.auth_service.repository.UserRepository;
import com.online.voting.events.candidate.CandidateCreationFailedEvent;
import com.online.voting.events.candidate.CandidateCreationSucceededEvent;

@Configuration
public class CandidateConsumer {

    private final UserRepository userRepository;

    public CandidateConsumer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public Consumer<CandidateCreationFailedEvent> candidateCreationFailed() {
        return event -> {
            System.err.println("[AUTH] Candidate creation failed for userId: " + event.getUserId()
                    + " reason: " + event.getReason());

            userRepository.findById(event.getUserId()).ifPresent(user -> {
                user.setStatus(RegistrationStatus.FAILED);
                userRepository.save(user); // rollback
                System.out.println("[AUTH] Rolled back user: " + user.getId());
            });
        };
    }

    @Bean
    public Consumer<CandidateCreationSucceededEvent> candidateCreationSucceeded() {
        System.out.println("============= CandidateCreationSucceededEvent ===============");
        return event -> userRepository.findById(event.getUserId()).ifPresent(user -> {
            user.setStatus(RegistrationStatus.ACTIVE);
            userRepository.save(user);
            System.out.println("[AUTH] User activated: " + user.getId());
        });
    }

}
