package com.notifications.notification_service.service;
import  org.springframework.stereotype.Service;

@Service
public class EmailSender implements NotificationTypeInterface {
     @Override
    public String getType() {
        return "EMAIL";
    }
    @Override
    public boolean send(String destination, String message) {
        // Simulate sending email
        // System.out.println("Sending Email to " + destination + ": " + message);
        return false;
    }

    
    
}
