package com.profITsoft.emailservice.service;

import com.profITsoft.emailservice.data.EmailData;
import com.profITsoft.emailservice.data.StatusData;
import com.profITsoft.emailservice.messaging.SendMailMessage;
import com.profITsoft.emailservice.repository.MailRepository;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for sending emails.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;
    private final MailRepository mailRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    /**
     * Sends an email with the given message.
     *
     * @param message the message to send
     */
    public void sendMail(SendMailMessage message) {
        Dotenv dotenv = Dotenv.load();
        String recipient = dotenv.get("MAIL_DEFAULT_RECIPIENT");
        if (recipient == null) {
            recipient = message.getRecipient();
        }

        EmailData emailData = new EmailData();
        emailData.setRecipient(recipient);
        emailData.setSubject(message.getSubject());
        emailData.setText(message.getText());
        emailData.setStatus(StatusData.ERROR);

        sendAndSaveEmail(emailData);
    }

    /**
     * Retries sending failed emails and returns the count of retried emails.
     */
    public int retryFailedEmails() {
        List<EmailData> failedEmails = mailRepository.findByStatus(StatusData.ERROR);
        List<EmailData> updatedEmails = failedEmails.stream()
                .peek(this::sendEmail)
                .collect(Collectors.toList());

        if (!updatedEmails.isEmpty()) {
            elasticsearchOperations.save(updatedEmails, IndexCoordinates.of("emails"));
        }

        return updatedEmails.size();
    }

    /**
     * Sends and saves the email.
     *
     * @param emailData the email to send
     */
    private void sendAndSaveEmail(EmailData emailData) {
        sendEmail(emailData);
        mailRepository.save(emailData);
    }

    /**
     * Sends an email.
     *
     * @param emailData the email to send
     */
    private void sendEmail(EmailData emailData) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(emailData.getRecipient());
            mailMessage.setSubject(emailData.getSubject());
            mailMessage.setText(emailData.getText());
            javaMailSender.send(mailMessage);

            emailData.setStatus(StatusData.SENT);
            emailData.setErrorMessage(null);

            log.info("Email sent to {} with subject '{}' successfully", emailData.getRecipient(), emailData.getSubject());
        } catch (Exception e) {
            emailData.setStatus(StatusData.ERROR);
            emailData.setErrorMessage(e.getClass().getName() + ": " + e.getMessage());
            log.error("Error occurred while sending email to {} with subject '{}'", emailData.getRecipient(), emailData.getSubject(), e);
        }

        emailData.setAttemptCount(emailData.getAttemptCount() + 1);
        emailData.setLastAttemptTime(Instant.now());
    }
}
