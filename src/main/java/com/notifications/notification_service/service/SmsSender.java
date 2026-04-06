// filepath: /Users/robin/Downloads/notification-service/src/main/java/com/notifications/notification_service/service/SmsSender.java
package com.notifications.notification_service.service;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SmsSender implements NotificationTypeInterface {
    @Override
    public String getType() {
        return "SMS";
    }

    @Override
    public boolean send(String destination, String message) {
        // Implement SMS sending logic here (e.g., using Twilio or a mock)
        // Return true if successful, false otherwise
        try {
            // Example: Simulate success/failure
            // System.out.println("Sending SMS to " + destination + ": " + message);
            log.info("Sending SMS to " + destination + ": " + message);
            // Replace with actual SMS API call
            return true;  // Or false based on API response
        } catch (Exception e) {
            log.error("SMS send failed: " + e.getMessage());
            return false;
        }
    }
}

