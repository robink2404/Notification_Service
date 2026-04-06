package com.notifications.notification_service.response;

public class RateLimitExceedException extends RuntimeException {
    
    public RateLimitExceedException(String message) {
        super(message);
    }
    
}
