// filepath: /Users/robin/Downloads/notification-service/src/main/java/com/notifications/notification_service/kafka/NotificationProducer.java
package com.notifications.notification_service.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.notifications.notification_service.dto.NotificationDto;

@Component
public class NotificationProducer {
    private final KafkaTemplate<String, NotificationDto> kafkaTemplate;

    public NotificationProducer(KafkaTemplate<String, NotificationDto> kafkaTemplate) {
      this.kafkaTemplate = kafkaTemplate;
    }

    public void sendNotification(NotificationDto notificationDto) {
        kafkaTemplate.send("notification-topic", notificationDto.getUserId(), notificationDto);
        System.out.println("Sent notification to Kafka: " + notificationDto);
    }
}