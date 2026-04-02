package com.notifications.notification_service.controller;
import org.apache.catalina.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.notifications.notification_service.service.UserService;
import com.notifications.notification_service.dto.UserDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
   private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/save")
    public String saveUser(@RequestBody UserDto userDto) {
       String userId = userService.saveUser(userDto);
        System.out.println("User saved: " + userDto);
        return "User saved successfully!"+userId;
    }

}
