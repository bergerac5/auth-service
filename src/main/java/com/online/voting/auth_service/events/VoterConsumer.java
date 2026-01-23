package com.online.voting.auth_service.events;

import java.util.function.Consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.online.voting.auth_service.model.RegistrationStatus;
import com.online.voting.auth_service.repository.UserRepository;
import com.online.voting.events.VoterCreationFailedEvent;
import com.online.voting.events.VoterCreationSucceededEvent;

@Configuration
public class VoterConsumer {

    private final UserRepository userRepository;

    public VoterConsumer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public Consumer<VoterCreationFailedEvent> voterCreationFailed() {
        return event -> {
            System.err.println("[AUTH] Voter creation failed for userId: " + event.getUserId()
                    + " reason: " + event.getReason());

            userRepository.findById(event.getUserId()).ifPresent(user -> {
                userRepository.delete(user); // rollback
                System.out.println("[AUTH] Rolled back user: " + user.getId());
            });
        };
    }

    @Bean
    public Consumer<VoterCreationSucceededEvent> voterCreationSucceeded() {
        System.out.println("============= VoterCreationSucceededEvent ===============");
        return event -> userRepository.findById(event.getUserId()).ifPresent(user -> {
            user.setStatus(RegistrationStatus.ACTIVE);
            userRepository.save(user);
            System.out.println("[AUTH] User activated: " + user.getId());
        });
    }

}
