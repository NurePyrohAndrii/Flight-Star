package com.profITsoft.gateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.profITsoft.gateway.data.UserSession;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    private final ObjectMapper objectMapper;

    @Bean
    public ReactiveRedisTemplate<String, UserSession> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
        Jackson2JsonRedisSerializer<UserSession> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, UserSession.class);

        RedisSerializationContext<String, UserSession> serializationContext = RedisSerializationContext
                .<String, UserSession>newSerializationContext(RedisSerializer.string())
                .hashKey(RedisSerializer.string())
                .hashValue(RedisSerializer.json())
                .value(serializer)
                .build();

        return new ReactiveRedisTemplate<>(factory, serializationContext);
    }
}
