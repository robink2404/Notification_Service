package com.notifications.notification_service.service;

import java.lang.StackWalker.Option;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.catalina.User;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.notifications.notification_service.dto.NotificationDto;
import com.notifications.notification_service.dto.UserNotFoundException;
import com.notifications.notification_service.entity.Notification;
import com.notifications.notification_service.entity.UserDetail;
import com.notifications.notification_service.enums.*;
import com.notifications.notification_service.repository.NotificationRepository;
import com.notifications.notification_service.repository.UserRepository;
import com.notifications.notification_service.service.RateLimiterService;

import lombok.extern.slf4j.Slf4j;

import com.notifications.notification_service.kafka.NotificationProducer;

@Service
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationProducer notificationProducer;
    private final RedisTemplate<String,String> redisTemplate;
    private final RateLimiterService rateLimiterService;
    private final UserRepository userRepository;



    public NotificationService(NotificationRepository notificationRepository,NotificationProducer notificationProducer,RedisTemplate<String,String> redisTemplate,RateLimiterService rateLimiterService,UserRepository userRepository) {
         this.rateLimiterService=rateLimiterService;        
        this.notificationRepository = notificationRepository;
        this.notificationProducer = notificationProducer;
        this.redisTemplate = redisTemplate;
        this.userRepository = userRepository;
    }

    public String createNotification(NotificationDto notificationDto) {
       String UserId=notificationDto.getUserId();
       String userData=redisTemplate.opsForValue().get("user:"+UserId);
       if(userData==null){
        log.warn("User data not found in Redis for userId: " + UserId);
            Optional<UserDetail> userOptional=userRepository.findById(UserId);
            if(userOptional.isEmpty()){
                log.warn("User not found in database for userId: " + UserId);
                throw new UserNotFoundException("User not found in database with id: " + UserId);
            
            }
            UserDetail userDetail=userOptional.get();
            String value=userDetail.getEmailAddress()+"|"+userDetail.getPhoneNumber();
            redisTemplate.opsForValue().set(
                "user:"+UserId,
                value,
                10, TimeUnit.MINUTES   // TTL (optional but recommended)
            );
            log.info("User found in DB and cached in Redis for userId: {}", UserId);
       }else{
        log.info("User data retrieved from Redis for userId: {}", UserId);
       }
        

        if(!rateLimiterService.isAllowed(UserId)){
            log.warn("Rate limit exceeded for userId: {}", UserId);
        return "Rate limit exceeded (max 2 notifications per minute)";
        }




        Notification notification = Notification.builder()
                .userId(notificationDto.getUserId())
                .message(notificationDto.getMessage())
                .notificationType(NotificationType.valueOf(notificationDto.getType()))
                .priority(Priority.valueOf(notificationDto.getPriority()))
                .retryCount(0)
                .status(NotificationStatus.PENDING)
                .build();

       Notification savedNotification = notificationRepository.save(notification);
      
        notificationProducer.sendNotification("notification-topic",notificationDto,savedNotification.getId());
        // System.out.println("Notification created: "+savedNotification);
            log.info("Notification created: " + savedNotification);
        return "Notification created successfully!"+savedNotification.getId();

    }

    public Notification getNotification(Long notificationId) {
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + notificationId));
    }


    

}
