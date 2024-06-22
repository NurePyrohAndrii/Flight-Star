package com.profITsoft.gateway.config;

import com.profITsoft.gateway.data.UserSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public ReactiveRedisTemplate<String, UserSession> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
        RedisSerializationContext<String, UserSession> serializationContext = RedisSerializationContext
                .<String, UserSession>newSerializationContext(RedisSerializer.string())
                .hashKey(RedisSerializer.string())
                .hashValue(RedisSerializer.json())
                .build();

        return new ReactiveRedisTemplate<>(factory, serializationContext);
    }
}
