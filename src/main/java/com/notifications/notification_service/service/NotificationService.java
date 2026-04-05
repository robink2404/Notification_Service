package com.notifications.notification_service.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.notifications.notification_service.dto.NotificationDto;
import com.notifications.notification_service.entity.Notification;
import com.notifications.notification_service.enums.*;
import com.notifications.notification_service.repository.NotificationRepository;

import lombok.extern.slf4j.Slf4j;

import com.notifications.notification_service.kafka.NotificationProducer;

@Service
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationProducer notificationProducer;



    public NotificationService(NotificationRepository notificationRepository,NotificationProducer notificationProducer) {
        this.notificationRepository = notificationRepository;
        this.notificationProducer = notificationProducer;
    }

    public String createNotification(NotificationDto notificationDto) {
        // notificationRepository.save(notification);
        // System.out.println("Received notification request: " + notificationDto);
        // try{
        // Thread.sleep(10000);
        // }catch(InterruptedException e){
        //     Thread.currentThread().interrupt();
        // } // Simulate processing time 

        Notification notification = Notification.builder()
                .userId(notificationDto.getUserId())
                .message(notificationDto.getMessage())
                .notificationType(NotificationType.valueOf(notificationDto.getType()))
                .priority(Priority.valueOf(notificationDto.getPriority()))
                .retryCount(0)
                .status(NotificationStatus.PENDING)
                .build();

       Notification savedNotification = notificationRepository.save(notification);
      
        notificationProducer.sendNotification("notification-topic",notificationDto,savedNotification.getId());
        // System.out.println("Notification created: "+savedNotification);
            log.info("Notification created: " + savedNotification);
        return "Notification created successfully!"+savedNotification.getId();

    }

    public Notification getNotification(Long notificationId) {
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + notificationId));
    }


    

}
