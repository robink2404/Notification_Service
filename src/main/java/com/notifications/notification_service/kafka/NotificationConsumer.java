package com.notifications.notification_service.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import com.notifications.notification_service.dto.NotificationDto;
import org.springframework.stereotype.Service;
import com.notifications.notification_service.entity.UserDetail;
import com.notifications.notification_service.repository.UserRepository;
import com.notifications.notification_service.service.NotificationTypeInterface;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;

import java.util.List;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;
import com.notifications.notification_service.dto.NotificationEvent;
import com.notifications.notification_service.enums.NotificationStatus;
import com.notifications.notification_service.repository.NotificationRepository;
import com.notifications.notification_service.entity.Notification;
import com.notifications.notification_service.kafka.NotificationProducer;
import com.notifications.notification_service.enums.*;
import com.notifications.notification_service.config.SchedulerConfig;


@Service
@Slf4j
public class NotificationConsumer {
    private final List<NotificationTypeInterface> senders;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationProducer notificationProducer;
    @Autowired
    private TaskScheduler taskScheduler;

    public NotificationConsumer(List<NotificationTypeInterface> senders, UserRepository userRepository, NotificationRepository notificationRepository, NotificationProducer notificationProducer) {
        this.senders = senders;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
        this.notificationProducer = notificationProducer;
    }
    
    @KafkaListener(topics = "notification-topic", groupId = "notification-group")
    public void consume(NotificationEvent event) {
        Long notificationId = event.getNotificationId();
        NotificationDto notificationDto = event.getNotificationDto();
        // System.out.println("Consumed notification from Kafka: " + notificationDto);
        log.info("Consumed notification from Kafka: " + notificationDto);
        
        UserDetail user = userRepository.findById(notificationDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Fetch the notification entity
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        // Default status to FAILED
        NotificationStatus status = NotificationStatus.FAILED;
        String logMessage = "Failed to send notification to " + notificationDto.getType();

        for (NotificationTypeInterface sender : senders) {
            if (sender.getType().equalsIgnoreCase(notificationDto.getType())) {
                String destination = notificationDto.getType().equalsIgnoreCase("EMAIL")
                        ? user.getEmailAddress()
                        : user.getPhoneNumber();

                boolean result = sender.send(destination, notificationDto.getMessage());
                if (result) {
                    status = NotificationStatus.SUCCESS;
                    logMessage = "Notification sent successfully to " + destination;
                    log.info(logMessage);
                    // System.out.println("Notification sent successfully to " + destination);
                } else {
                    logMessage = "Failed to send notification to " + destination;
                    log.error(logMessage);
                    handleRetry(notification, event);
                    // System.out.println("Failed to send notification to " + destination);    
                }
                break;  // Exit loop after attempting send
            }
        }

        // If no sender matched, log unsupported type
        if (status == NotificationStatus.FAILED && logMessage.equals("Failed to send notification to " + notificationDto.getType())) {
            log.info("Unsupported notification type: " + notificationDto.getType());
        } else {
            log.info(logMessage);
        }

        // Update and save the notification status
        notification.setStatus(status);
        notificationRepository.save(notification);
    }
   // ...existing code...

    private void handleRetry(Notification notification, NotificationEvent event) {
        int retryCount = notification.getRetryCount() + 1;
        notification.setRetryCount(retryCount);
        int maxRetry = notification.getPriority() == Priority.HIGH ? 5 : 3;

        if (retryCount > maxRetry) {
            notification.setStatus(NotificationStatus.FAILED);
            notificationRepository.save(notification);
            notificationProducer.sendNotification("notification-dlq-topic",event.getNotificationDto(), notification.getId());
            log.info("Max retry attempts reached for notification: " + notification.getId());
            return;
        } else {
            notification.setStatus(NotificationStatus.PENDING);
            notificationRepository.save(notification);
            long baseDelay = notification.getPriority() == Priority.HIGH ? 1000 : 5000;
            long delay = (long) (baseDelay * Math.pow(2, retryCount));
            long jitter=ThreadLocalRandom.current().nextLong(0, 500);
            delay += jitter;
            log.info("Scheduling retry " + retryCount + " for notification: " + notification.getId());
            taskScheduler.schedule(()->{
                notificationProducer.sendNotification("notification-retry-topic",event.getNotificationDto(), notification.getId());
                log.info("Retrying notification: " + retryCount + " for notification: " + notification.getId());
            },new Date(System.currentTimeMillis()+delay));
            // Move this INSIDE else block - only retry if count <= maxRetry
            notificationProducer.sendNotification("notification-retry-topic",event.getNotificationDto(), notification.getId());
            log.info("Retrying notification: " + retryCount + " for notification: " + notification.getId());
        }
    }
}