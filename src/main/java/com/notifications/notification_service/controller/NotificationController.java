package com.notifications.notification_service.controller;

import com.notifications.notification_service.dto.*;
import com.notifications.notification_service.service.NotificationService;
import org.springframework.web.bind.annotation.*;
import com.notifications.notification_service.service.UserService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/send")
    public String sendNotification(@RequestBody NotificationDto  request) {
        // NotificationService notificationService=new NotificationService(null);
        notificationService.createNotification(request);
        System.out.println("Notification request processed: " + request);
        return "Notification received successfully!";
    }
}