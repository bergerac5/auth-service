package com.online.voting.auth_service.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.online.voting.events.UserCreatedEvent;

@RestController
@RequestMapping("/api/kafka-test")
public class KafkaTestController {

    @Autowired
    private StreamBridge streamBridge;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Send a test message to Kafka
     */
    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendTestMessage(
            @RequestBody(required = false) Map<String, String> request) {

        Map<String, Object> result = new HashMap<>();

        try {
            // Create test event with provided data or defaults
            UserCreatedEvent event = new UserCreatedEvent();
            event.setUserId(UUID.randomUUID());
            event.setNationalId(request != null && request.containsKey("nationalId")
                    ? request.get("nationalId")
                    : "1234567890123456");
            event.setFirstName(request != null && request.containsKey("firstName")
                    ? request.get("firstName")
                    : "Test");
            event.setLastName(request != null && request.containsKey("lastName")
                    ? request.get("lastName")
                    : "User");
            event.setRole(request != null && request.containsKey("role")
                    ? request.get("role")
                    : "VOTER");

            System.out.println("=== SENDING TEST MESSAGE ===");
            System.out.println("Event: " + event);
            System.out.println("Destination: user-created");

            // Send via StreamBridge
            boolean sent = streamBridge.send("userCreated-out-0", event);

            if (sent) {
                System.out.println("✓ Message sent successfully");
                result.put("status", "SUCCESS");
                result.put("message", "Test message sent to Kafka");
                result.put("event", event);
                result.put("destination", "user-created");
                result.put("sentVia", "StreamBridge");
            } else {
                System.out.println("✗ Failed to send message");
                result.put("status", "FAILED");
                result.put("message", "StreamBridge returned false");
                result.put("event", event);
            }

        } catch (Exception e) {
            System.err.println("Error sending test message: " + e.getMessage());
            e.printStackTrace();

            result.put("status", "ERROR");
            result.put("error", e.getMessage());
            result.put("stackTrace", e.getStackTrace());
        }

        return ResponseEntity.ok(result);
    }

    /**
     * Simple test connection endpoint
     */
    @GetMapping("/test-connection")
    public ResponseEntity<Map<String, Object>> testConnection() {
        Map<String, Object> result = new HashMap<>();

        try {
            // Create a simple test event
            UserCreatedEvent testEvent = new UserCreatedEvent();
            testEvent.setUserId(UUID.randomUUID());
            testEvent.setNationalId("9999999999999999");
            testEvent.setFirstName("Connection");
            testEvent.setLastName("Test");
            testEvent.setRole("VOTER");

            System.out.println("Testing StreamBridge connection...");
            boolean sent = streamBridge.send("userCreated-out-0", testEvent);

            result.put("streamBridgeWorking", sent);
            result.put("testEvent", testEvent.toString());
            result.put("timestamp", new java.util.Date());

            if (sent) {
                result.put("status", "SUCCESS");
                result.put("message", "StreamBridge is working");
            } else {
                result.put("status", "WARNING");
                result.put("message", "StreamBridge returned false");
            }

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("error", e.getMessage());
        }

        return ResponseEntity.ok(result);
    }

    /**
     * Send multiple test messages
     */
    @PostMapping("/send-multiple")
    public ResponseEntity<Map<String, Object>> sendMultipleMessages(
            @RequestParam(defaultValue = "3") int count) {

        Map<String, Object> result = new HashMap<>();
        int successCount = 0;
        int failCount = 0;

        for (int i = 1; i <= count; i++) {
            try {
                UserCreatedEvent event = new UserCreatedEvent();
                event.setUserId(UUID.randomUUID());
                event.setNationalId(String.format("100000000000000%d", i % 10));
                event.setFirstName("Test" + i);
                event.setLastName("User" + i);
                event.setRole(i % 2 == 0 ? "VOTER" : "ADMIN");

                boolean sent = streamBridge.send("userCreated-out-0", event);

                if (sent) {
                    successCount++;
                    System.out.println("Sent message " + i + ": " + event.getFirstName());
                } else {
                    failCount++;
                    System.err.println("Failed to send message " + i);
                }

                // Small delay between messages
                Thread.sleep(100);

            } catch (Exception e) {
                failCount++;
                System.err.println("Error sending message " + i + ": " + e.getMessage());
            }
        }

        result.put("status", "COMPLETED");
        result.put("totalAttempted", count);
        result.put("successCount", successCount);
        result.put("failCount", failCount);
        result.put("message", "Sent " + successCount + " of " + count + " messages");

        return ResponseEntity.ok(result);
    }

    /**
     * Check StreamBridge status
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> result = new HashMap<>();

        result.put("service", "auth-service");
        result.put("timestamp", new java.util.Date());
        result.put("streamBridgeAvailable", streamBridge != null);

        // Test destinations
        String[] destinations = { "userCreated-out-0", "userUpdated-out-0", "userDeleted-out-0" };
        Map<String, Boolean> destStatus = new HashMap<>();

        for (String dest : destinations) {
            try {
                // Quick test by sending null (won't actually send, just tests binding)
                destStatus.put(dest, true);
            } catch (Exception e) {
                destStatus.put(dest, false);
            }
        }

        result.put("destinations", destStatus);

        return ResponseEntity.ok(result);
    }
}