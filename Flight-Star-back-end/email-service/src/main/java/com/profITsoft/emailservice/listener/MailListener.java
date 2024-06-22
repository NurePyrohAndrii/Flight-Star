package com.profITsoft.emailservice.listener;

import com.profITsoft.emailservice.messaging.SendMailMessage;
import com.profITsoft.emailservice.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Listener for mail messages from Kafka.
 */
@Component
@RequiredArgsConstructor
public class MailListener {

    private final MailService mailService;

    @KafkaListener(topics = "${kafka.topic.mail}")
    public void listen(SendMailMessage message) {
        mailService.sendMail(message);
    }

}
