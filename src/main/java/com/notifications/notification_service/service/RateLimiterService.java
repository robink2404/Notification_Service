package com.notifications.notification_service.service;

import java.util.Collections;

import org.apache.kafka.common.metrics.stats.Rate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import com.notifications.notification_service.config.RateLimiterLuaConfig;

import kotlin.io.encoding.Base64.Default;


@Service
public class RateLimiterService {


    private final RedisTemplate<String,String> redisTemplate;
    private final DefaultRedisScript<Long> tokenBucketScript;

    public RateLimiterService(RedisTemplate<String, String> redisTemplate, DefaultRedisScript<Long> tokenBucketScript) {
        this.redisTemplate = redisTemplate;
        this.tokenBucketScript = tokenBucketScript;
    }

    public boolean isAllowed(String userId){
        String key ="notification:rate:user:"+userId;
        int capacity=2;
        double refillRate=2.0/60.0; // 2 tokens per minute
        long now=System.currentTimeMillis();
        Long result=redisTemplate.execute(
            tokenBucketScript,
            Collections.singletonList(key),
            String.valueOf(capacity),
            String.valueOf(refillRate),
            String.valueOf(now)
        );

        return result!=null && result==1;
    } 
    
}
