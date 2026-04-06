package com.notifications.notification_service.entity;

import java.time.LocalDateTime;
import com.notifications.notification_service.enums.*;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "notifications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
     

    @Column(name="user_id",nullable = false)
    private String userId;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType notificationType;

    @Column(name="message",nullable=false)
    private String message;

    @Column(name="retryCount",nullable = false)
    private Integer retryCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    
  @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

@PreUpdate
protected void onUpdate() {
    updatedAt = LocalDateTime.now();
}



}
