package com.notifications.notification_service.service;

import org.apache.catalina.User;
import org.springframework.messaging.simp.user.UserDestinationResolver;
import org.springframework.stereotype.Service;

import com.notifications.notification_service.dto.UserDto;
import com.notifications.notification_service.entity.UserDetail;
import com.notifications.notification_service.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService {

   private final UserRepository userRepository;
   private static final Logger logger=LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String saveUser(UserDto userDto){
        UserDetail userDetail=UserDetail.builder()
        .emailAddress(userDto.getEmailAddress())
        .phoneNumber(userDto.getPhoneNumber())
        .build();

        userRepository.save(userDetail);
        logger.info("Saved user: " + userDetail);
        return userDetail.getUserId();
    }
    

    }
    

