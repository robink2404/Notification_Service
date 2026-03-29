package com.notifications.notification_service.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.notifications.notification_service.dto.NotificationDto;
import com.notifications.notification_service.entity.Notification;
import com.notifications.notification_service.enums.*;
import com.notifications.notification_service.repository.NotificationRepository;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;



    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Async
    public void createNotification(NotificationDto notificationDto) {
        // notificationRepository.save(notification);
        System.out.println("Received notification request: " + notificationDto);
        try{
        Thread.sleep(10000);
        }catch(InterruptedException e){
            Thread.currentThread().interrupt();
        } // Simulate processing time 

        Notification notification = Notification.builder()
                .userId(notificationDto.getUserId())
                .message(notificationDto.getMessage())
                .notificationType(NotificationType.valueOf(notificationDto.getType()))
                .priority(Priority.valueOf(notificationDto.getPriority()))
                .retryCount(0)
                .build();

        notificationRepository.save(notification);
        System.out.println("Notification created: "+notification);

    }


    

}
