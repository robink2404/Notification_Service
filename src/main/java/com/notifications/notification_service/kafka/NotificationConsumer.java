package com.notifications.notification_service.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import com.notifications.notification_service.dto.NotificationDto;
import org.springframework.stereotype.Service;
import com.notifications.notification_service.entity.UserDetail;
import com.notifications.notification_service.repository.UserRepository;
import com.notifications.notification_service.service.NotificationTypeInterface;
import java.util.List;
import com.notifications.notification_service.dto.NotificationEvent;
import com.notifications.notification_service.enums.NotificationStatus;
import com.notifications.notification_service.repository.NotificationRepository;
import com.notifications.notification_service.entity.Notification;
import com.notifications.notification_service.kafka.NotificationProducer;
import com.notifications.notification_service.enums.*;


@Service
public class NotificationConsumer {
    private final List<NotificationTypeInterface> senders;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationProducer notificationProducer;

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
        System.out.println("Consumed notification from Kafka: " + notificationDto);
        
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
                    // System.out.println("Notification sent successfully to " + destination);
                } else {
                    logMessage = "Failed to send notification to " + destination;
                    handleRetry(notification, event);
                    // System.out.println("Failed to send notification to " + destination);    
                }
                break;  // Exit loop after attempting send
            }
        }

        // If no sender matched, log unsupported type
        if (status == NotificationStatus.FAILED && logMessage.equals("Failed to send notification to " + notificationDto.getType())) {
            System.out.println("Unsupported notification type: " + notificationDto.getType());
        } else {
            System.out.println(logMessage);
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
            System.out.println("Max retry attempts reached for notification: " + notification.getId());
        } else {
            notification.setStatus(NotificationStatus.PENDING);
            notificationRepository.save(notification);
            System.out.println("Scheduling retry " + retryCount + " for notification: " + notification.getId());
            try {
                if (notification.getPriority() == Priority.HIGH) {
                    Thread.sleep(1000);
                } else {
                    Thread.sleep(5000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Retry sleep interrupted for notification: " + notification.getId());
            }
            // Move this INSIDE else block - only retry if count <= maxRetry
            notificationProducer.sendNotification(event.getNotificationDto(), notification.getId());
            System.out.println("Retrying notification: " + retryCount + " for notification: " + notification.getId());
        }
    }
}