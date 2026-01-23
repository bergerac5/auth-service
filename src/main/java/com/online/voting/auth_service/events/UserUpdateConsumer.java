package com.online.voting.auth_service.events;

import java.util.function.Consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.online.voting.auth_service.model.RegistrationStatus;
import com.online.voting.auth_service.repository.UserRepository;
import com.online.voting.events.VoterUpdateFailedEvent;
import com.online.voting.events.VoterUpdateSucceededEvent;

@Configuration
public class UserUpdateConsumer {

    private final UserRepository userRepository;

    public UserUpdateConsumer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public Consumer<VoterUpdateFailedEvent> voterUpdateFailed() {
        return event -> {
            System.err.println("[AUTH] Update failed: " + event.getReason());

            userRepository.findById(event.getUserId()).ifPresent(user -> {
                // ✅ Roll back to last stable state
                user.setStatus(RegistrationStatus.FAILED_UPDATE);
                userRepository.save(user);

                System.out.println("[AUTH] Update rolled back for user " + user.getId());
                System.out.println("===================================================");
                System.err.println("[AUTH] Reason: " + event.getReason());
                System.out.println("===================================================");
            });
        };
    }

    @Bean
    public Consumer<VoterUpdateSucceededEvent> voterUpdateSucceeded() {
        return event -> {
            userRepository.findById(event.getUserId()).ifPresent(user -> {
                user.setStatus(RegistrationStatus.ACTIVE);
                userRepository.save(user);
                System.out.println("[AUTH] Update confirmed for user " + user.getId());
                System.out.println("============= Uuuuuuuuuuuuuuuuu ===============");
            });
        };
    }
}
