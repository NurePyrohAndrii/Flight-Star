package com.profITsoft.emailservice.repository;

import com.profITsoft.emailservice.data.EmailData;
import com.profITsoft.emailservice.data.StatusData;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Repository for email data operations in database.
 */
public interface MailRepository extends CrudRepository<EmailData, String> {

    List<EmailData> findByStatus(StatusData status);
}
