package com.notifications.notification_service.controller;

import com.notifications.notification_service.dto.*;
import com.notifications.notification_service.service.NotificationService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.notifications.notification_service.service.UserService;

import lombok.extern.slf4j.Slf4j;

import com.notifications.notification_service.entity.Notification;  

@RestController     
@RequestMapping("/api/notifications")
@Slf4j  
public class NotificationController {
    private final NotificationService notificationService;


    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<String>> sendNotification(@RequestBody NotificationDto  request) {
        // NotificationService notificationService=new NotificationService(null);
       String resuString= notificationService.createNotification(request);    
         
        log.info("Notification request processed: " + request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Notification sent successfully", resuString, null));
    }
}