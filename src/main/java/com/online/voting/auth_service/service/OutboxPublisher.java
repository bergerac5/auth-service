package com.online.voting.auth_service.service;

import java.time.Instant;
import java.util.List;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.online.voting.auth_service.model.OutboxEvent;
import com.online.voting.auth_service.repository.OutboxRepository;
import com.online.voting.events.UserCreatedEvent;

@Service
public class OutboxPublisher {

    private final OutboxRepository outboxRepository;
    private final StreamBridge streamBridge;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OutboxPublisher(OutboxRepository outboxRepository,
            StreamBridge streamBridge) {
        this.outboxRepository = outboxRepository;
        this.streamBridge = streamBridge;
    }

    @Scheduled(fixedDelay = 5000)
    public void publishEvents() {

        List<OutboxEvent> events = outboxRepository.findByStatus("PENDING");

        for (OutboxEvent event : events) {

            try {
                if ("USER_CREATED".equals(event.getEventType())) {

                    UserCreatedEvent payload = objectMapper.readValue(event.getPayload(), UserCreatedEvent.class);

                    streamBridge.send("userCreated-out-0", payload);
                }

                event.setStatus("SENT");
                event.setProcessedAt(Instant.now());

            } catch (Exception e) {
                event.setStatus("FAILED");
            }

            outboxRepository.save(event);
        }
    }
}
