package com.profITsoft.emailservice.messaging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

/**
 * Message object, consuming by the mail listener.
 */
@Getter
@Builder
@Jacksonized
@AllArgsConstructor
public class SendMailMessage {

    private String subject;

    private String text;

    private String recipient;

}
