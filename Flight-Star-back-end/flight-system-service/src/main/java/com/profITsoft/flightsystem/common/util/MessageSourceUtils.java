package com.profITsoft.flightsystem.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageSourceUtils {

    /**
     * MessageSource object for accessing the messages from the message properties files.
     */
    private final MessageSource messageSource;

    /**
     * Get the internationalized message from the given message key.
     *
     * @param message String object with the message key.
     * @return String object with the internationalized message.
     */
    public String getLocalizedMessage(String message) {
        return messageSource.getMessage(
                message, null, LocaleContextHolder.getLocale());
    }

}
