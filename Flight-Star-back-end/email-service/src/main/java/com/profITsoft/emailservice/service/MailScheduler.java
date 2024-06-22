package com.profITsoft.emailservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler for retrying failed emails.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MailScheduler {

    private final MailService mailService;

    /**
     * Retries sending failed emails every 5 minutes by default.
     */
    @Scheduled(cron = "${mail.retrying.cron:0 0/5 * * * ?}")
    @SchedulerLock(name = "RetryFailedEmails", lockAtLeastFor = "PT1M", lockAtMostFor = "PT10M")
    public void retryFailedEmails() {
        try {
            int retriedCount = mailService.retryFailedEmails();
            log.info("Retried {} failed emails", retriedCount);
        } catch (Throwable e) {
            log.error("Error occurred during retrying failed emails", e);
        }
    }
}