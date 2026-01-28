package com.online.voting.auth_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic userEventsTopic() {
        return new NewTopic("user-created", 3, (short) 1); // 3 partitions, replication factor 3
    }

    @Bean
    public NewTopic userUpdatedTopic() {
        return new NewTopic("user-updated", 3, (short) 1);
    }

    @Bean
    public NewTopic userDeletedTopic() {
        return new NewTopic("user-deleted", 3, (short) 1);
    }

    // ------Create voter feedback consumer----------
    @Bean
    public NewTopic voterCreationSucceededTopic() {
        return new NewTopic("voter-creation-succeeded", 3, (short) 1);
    }

    @Bean
    public NewTopic voterCreationFailedTopic() {
        return new NewTopic("voter-creation-failed", 3, (short) 1);
    }

    @Bean
    public NewTopic voterUpdateSucceededTopic() {
        return new NewTopic("voter-update-succeeded", 3, (short) 1);
    }

    @Bean
    public NewTopic voterUpdateFailedTopic() {
        return new NewTopic("voter-update-failed", 3, (short) 1);
    }

    /*
     * ===========================================================================
     * 
     * ----------------------------- CandidateTopic --------------------------------
     * 
     * =============================================================================
     * =
     */

    @Bean
    public NewTopic candidateCreatedTopic() {
        return new NewTopic("candidate-created", 3, (short) 1);
    }

    @Bean
    public NewTopic candidateUpdatedTopic() {
        return new NewTopic("candidate-updated", 3, (short) 1);
    }

    public NewTopic candidateDeletedTopic() {
        return new NewTopic("candidate-deleted", 3, (short) 1);
    }

    // ------Create candidate feedback consumer----------
    @Bean
    public NewTopic candidateCreationSucceededTopic() {
        return new NewTopic("candidate-creation-succeeded", 3, (short) 1);
    }

    @Bean
    public NewTopic candidateCreationFailedTopic() {
        return new NewTopic("candidate-creation-failed", 3, (short) 1);
    }

}