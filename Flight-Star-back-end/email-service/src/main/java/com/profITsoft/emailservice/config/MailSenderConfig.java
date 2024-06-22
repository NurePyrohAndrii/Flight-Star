package com.profITsoft.emailservice.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Objects;
import java.util.Properties;

/**
 * Configuration class for JavaMailSender bean with properties from .env file.
 */
@Configuration
public class MailSenderConfig {

    @Bean
    public JavaMailSender javaMailSender() {
        Dotenv dotenv = Dotenv.load();

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(dotenv.get("MAIL_HOST"));
        mailSender.setPort(Integer.parseInt(Objects.requireNonNull(dotenv.get("MAIL_PORT"))));
        mailSender.setUsername(dotenv.get("MAIL_USERNAME"));
        mailSender.setPassword(dotenv.get("MAIL_PASSWORD"));

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", dotenv.get("MAIL_SMTP_AUTH"));
        props.put("mail.smtp.starttls.enable", dotenv.get("MAIL_SMTP_STARTTLS_ENABLE"));

        return mailSender;
    }
}
