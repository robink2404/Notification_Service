package com.notifications.notification_service.controller;
import org.apache.catalina.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.notifications.notification_service.service.UserService;
import com.notifications.notification_service.dto.UserDto;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
   private final UserService userService;
   private static final Logger logger=LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/save")
    public String saveUser(@RequestBody UserDto userDto) {
       String userId = userService.saveUser(userDto);
        logger.info("User saved: " + userDto);
        return "User saved successfully!"+userId;
    }
    @GetMapping("/check/{userId}")
    public String checkUserInRedis(@PathVariable String userId){
        String value=userService.checkUserinRedis(userId);
        if(value!=null){
            return "User found in Redis cache: " + userId+" with value: "+value;
        }else{
            return "User not found in Redis cache: " + userId;
        }
    }

}
