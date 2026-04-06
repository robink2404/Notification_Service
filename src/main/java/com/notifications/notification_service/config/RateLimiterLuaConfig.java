package com.notifications.notification_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import kotlin.io.encoding.Base64.Default;

@Configuration
public class RateLimiterLuaConfig {

    @Bean
    public DefaultRedisScript<Long> rateLimiterScript(){
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(
            "local key = KEYS[1]\n" +
                "local capacity = tonumber(ARGV[1])\n" +
                "local refill_rate = tonumber(ARGV[2])\n" + // tokens per second
                "local now = tonumber(ARGV[3])\n" +
                "\n" +
                "local data = redis.call('HMGET', key, 'tokens', 'last_refill_time')\n" +
                "local tokens = tonumber(data[1])\n" +
                "local last_refill = tonumber(data[2])\n" +
                "\n" +
                "if tokens == nil then\n" +
                "  tokens = capacity\n" +
                "  last_refill = now\n" +
                "end\n" +
                "\n" +
                "local elapsed = (now - last_refill) / 1000\n" +
                "local refill = elapsed * refill_rate\n" +
                "tokens = math.min(capacity, tokens + refill)\n" +
                "\n" +
                "if tokens < 1 then\n" +
                "  return 0\n" +
                "end\n" +
                "\n" +
                "tokens = tokens - 1\n" +
                "\n" +
                "redis.call('HMSET', key, 'tokens', tokens, 'last_refill_time', now)\n" +
                "redis.call('EXPIRE', key, 120)\n" + // cleanup
                "\n" +
                "return 1\n"

        );
        redisScript.setResultType(Long.class);
        return redisScript;
    }
    
}
