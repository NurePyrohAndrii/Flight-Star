package com.profITsoft.emailservice.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.Map;

/**
 * Kafka topic configuration class that creates a KafkaAdmin bean and a NewTopic bean for the mail topic.
 */
@Configuration
public class KafkaTopicConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value("${kafka.topic.mail}")
    private String mailTopic;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = Map.of(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic paymentReceivedTopic() {
        return new NewTopic(mailTopic, 2, (short) 1);
    }
}
