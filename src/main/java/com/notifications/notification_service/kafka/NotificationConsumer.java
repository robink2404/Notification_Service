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

@Service
public class NotificationConsumer {
    private final List<NotificationTypeInterface> senders;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public NotificationConsumer(List<NotificationTypeInterface> senders, UserRepository userRepository, NotificationRepository notificationRepository) {
        this.senders = senders;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
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
                    System.out.println("Notification sent successfully to " + destination);
                } else {
                    logMessage = "Failed to send notification to " + destination;
                    System.out.println("Failed to send notification to " + destination);    
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
}