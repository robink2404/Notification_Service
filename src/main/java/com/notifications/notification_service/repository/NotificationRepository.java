package com.notifications.notification_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.notifications.notification_service.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification,Long>{

    
} 
