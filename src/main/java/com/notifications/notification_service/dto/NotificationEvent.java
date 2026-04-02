package com.notifications.notification_service.dto;
import com.notifications.notification_service.*;

import lombok.Data;


@Data
public class NotificationEvent {
     private Long notificationId;
    private NotificationDto notificationDto;
}
