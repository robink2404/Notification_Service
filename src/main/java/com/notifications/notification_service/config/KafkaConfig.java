package com.notifications.notification_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic notificationTopic() {
        return new NewTopic("notification-topic", 3, (short) 3);
    }
    public NewTopic retryTopic() {
        return new NewTopic("notification-retry-topic", 3, (short) 1);
    }
    public NewTopic dlqTopic() {
        return new NewTopic("notification-dlq-topic", 3, (short) 1);
    }
    
}
