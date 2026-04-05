// filepath: /Users/robin/Downloads/notification-service/src/main/java/com/notifications/notification_service/kafka/NotificationProducer.java
package com.notifications.notification_service.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import com.notifications.notification_service.dto.NotificationEvent;

import lombok.extern.slf4j.Slf4j;

import com.notifications.notification_service.dto.NotificationDto;

@Component
@Slf4j
public class NotificationProducer {
    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    public NotificationProducer(KafkaTemplate<String, NotificationEvent> kafkaTemplate) {
      this.kafkaTemplate = kafkaTemplate;
    }

    public void sendNotification(NotificationDto notificationDto, Long notificationId) {
        NotificationEvent event = new NotificationEvent();
        event.setNotificationId(notificationId);
        event.setNotificationDto(notificationDto);
        kafkaTemplate.send("notification-topic", notificationDto.getUserId(), event);
        // System.out.println("Sent notification to Kafka: " + event);
        log.info("Sent notification to Kafka: " + event);
    }
}