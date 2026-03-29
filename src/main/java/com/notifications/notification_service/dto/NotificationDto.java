package com.notifications.notification_service.dto;

import lombok.Data;

@Data
public class NotificationDto {
    private String userId;
    private String message;
    private String type;  
    private String priority;
}
