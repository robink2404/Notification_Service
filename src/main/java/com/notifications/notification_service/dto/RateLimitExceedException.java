package com.notifications.notification_service.dto;

public class RateLimitExceedException extends RuntimeException {
    
    public RateLimitExceedException(String message) {
        super(message);
    }
    
}
