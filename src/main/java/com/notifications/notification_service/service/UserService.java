package com.notifications.notification_service.service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.catalina.User;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.user.UserDestinationResolver;
import org.springframework.stereotype.Service;

import com.notifications.notification_service.dto.UserDto;
import com.notifications.notification_service.response.UserNotFoundException;
import com.notifications.notification_service.entity.UserDetail;
import com.notifications.notification_service.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService {

   private final UserRepository userRepository;
   private static final Logger logger=LoggerFactory.getLogger(UserService.class);
   private final RedisTemplate<String,String> redisTemplate;

    public UserService(UserRepository userRepository, RedisTemplate<String,String> redisTemplate) {
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;

    }

    public String saveUser(UserDto userDto){
        UserDetail userDetail=UserDetail.builder()
        .emailAddress(userDto.getEmailAddress())
        .phoneNumber(userDto.getPhoneNumber())
        .build();

        userRepository.save(userDetail);
       
        String userId=userDetail.getUserId();
          String key="user:"+userId;
        String value=userDetail.getEmailAddress()+"|"+userDetail.getPhoneNumber();
         redisTemplate.opsForValue().set(
                key,
                value,
                10, TimeUnit.MINUTES   // TTL (optional but recommended)
        );
        logger.info("Saved user: " + userDetail);
        return userDetail.getUserId();
    }

    public String checkUserinRedis(String userId){
       

        String value=redisTemplate.opsForValue().get("user:" + userId);
        if(value!=null){
            logger.info("User found in Redis cache: " + userId);
            return value;
        }else{
            logger.info("User not found in Redis cache: " + userId);
            Optional<UserDetail> userOptional=userRepository.findById(userId);
            if(userOptional.isEmpty()){
                logger.warn("User not found in database for userId: " + userId);
                throw new UserNotFoundException("User not found in database with id: " + userId);
            }else{
                UserDetail useDetail=userOptional.get();
                String userValue=useDetail.getEmailAddress()+"|"+useDetail.getPhoneNumber();
                redisTemplate.opsForValue().set(
                    "user:"+userId,
                    userValue,
                    10, TimeUnit.MINUTES   // TTL (optional but recommended)
                );
                logger.info("User found in database and cached in Redis for userId: {}", userId);
                return userValue+" (fetched from DB and cached in Redis)";
            }
            
        }

    }
    

    }
    

