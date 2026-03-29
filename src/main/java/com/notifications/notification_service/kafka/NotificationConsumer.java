package com.notifications.notification_service.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import com.notifications.notification_service.dto.NotificationDto;
import org.springframework.stereotype.Service;


@Service
public class NotificationConsumer {
    
    @KafkaListener(topics = "notification-topic", groupId = "notification-group")
    public void consume(NotificationDto notificationDto) {
        System.out.println("Consumed notification from Kafka: " + notificationDto);
        // Here you can add logic to process the notification, e.g., send an email or push notification
    }
}
