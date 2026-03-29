package com.notifications.notification_service.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.notifications.notification_service.entity.UserDetail;

public interface UserRepository extends JpaRepository<UserDetail, String> {

    
} 
