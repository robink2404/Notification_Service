package com.notifications.notification_service.service;
public interface NotificationTypeInterface {
 boolean send(String destination, String message);

    String getType(); 
    
}
