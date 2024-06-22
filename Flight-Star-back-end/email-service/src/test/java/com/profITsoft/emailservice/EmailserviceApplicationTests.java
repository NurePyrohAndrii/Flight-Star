package com.profITsoft.emailservice;

import com.profITsoft.emailservice.config.TestElasticsearchConfiguration;
import com.profITsoft.emailservice.data.EmailData;
import com.profITsoft.emailservice.data.StatusData;
import com.profITsoft.emailservice.messaging.SendMailMessage;
import com.profITsoft.emailservice.repository.MailRepository;
import com.profITsoft.emailservice.service.MailService;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ContextConfiguration(classes = {EmailserviceApplication.class, TestElasticsearchConfiguration.class})
class EmailserviceApplicationTests {

    @Autowired
    private MailService mailService;

    @Autowired
    private MailRepository mailRepository;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @MockBean
    private JavaMailSender javaMailSender;

    private String recipient;

    EmailserviceApplicationTests() {
        Dotenv dotenv = Dotenv.load();
        recipient = dotenv.get("MAIL_DEFAULT_RECIPIENT");
        if (recipient == null) {
            recipient = "test@example.com";
        }
    }

    @BeforeEach
    void setUp() {
        elasticsearchOperations.indexOps(EmailData.class).createMapping();
    }

    @AfterEach
    void tearDown() {
        elasticsearchOperations.indexOps(EmailData.class).delete();
    }

    @Test
    void testSendMailSuccess() {
        SendMailMessage message = getTestMessage();

        // Mock successful email sending
        Mockito.doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));

        mailService.sendMail(message);

        List<EmailData> emails = iterableToList(mailRepository.findAll());
        assertThat(emails).hasSize(1);
        EmailData email = emails.get(0);
        assertThat(email.getRecipient()).isEqualTo(recipient);
        assertThat(email.getStatus()).isEqualTo(StatusData.SENT);
        assertThat(email.getErrorMessage()).isNull();

        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendMailFailure() {
        SendMailMessage message = getTestMessage();

        // Mock email sending failure
        doThrow(new RuntimeException("Email sending failed")).when(javaMailSender).send(any(SimpleMailMessage.class));

        mailService.sendMail(message);

        List<EmailData> emails = iterableToList(mailRepository.findAll());
        assertThat(emails).hasSize(1);
        EmailData email = emails.get(0);
        assertThat(email.getRecipient()).isEqualTo(recipient);
        assertThat(email.getStatus()).isEqualTo(StatusData.ERROR);
        assertThat(email.getErrorMessage()).contains("Email sending failed");

        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    private SendMailMessage getTestMessage() {
        return SendMailMessage.builder()
                .recipient(recipient)
                .subject("Test Subject")
                .text("Test Content")
                .build();
    }

    private List<EmailData> iterableToList(Iterable<EmailData> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false)
                .collect(Collectors.toList());
    }
}
