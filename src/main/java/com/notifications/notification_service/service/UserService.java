package com.notifications.notification_service.service;

import org.apache.catalina.User;
import org.springframework.messaging.simp.user.UserDestinationResolver;
import org.springframework.stereotype.Service;

import com.notifications.notification_service.dto.UserDto;
import com.notifications.notification_service.entity.UserDetail;
import com.notifications.notification_service.repository.UserRepository;

@Service
public class UserService {

   private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String saveUser(UserDto userDto){
        UserDetail userDetail=UserDetail.builder()
        .emailAddress(userDto.getEmailAddress())
        .phoneNumber(userDto.getPhoneNumber())
        .build();

        userRepository.save(userDetail);

        System.out.println("Saved user: " + userDetail);
        return userDetail.getUserId();
    }
    

    }
    

