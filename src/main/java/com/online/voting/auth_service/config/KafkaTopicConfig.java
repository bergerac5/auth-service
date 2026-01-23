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

}
