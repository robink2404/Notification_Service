package com.notifications.notification_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic notificationTopic() {
        return new NewTopic("notification-topic", 3, (short) 1);
    }
    
}
