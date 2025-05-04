package com.wiredi.kafka.consumer.exceptions;

import com.wiredi.runtime.messaging.MessagingResult;

public class UnsupportedMessagingResultException extends RuntimeException {
    public UnsupportedMessagingResultException(MessagingResult message) {
        super("Unsupported MessagingResult returned: " + message);
    }
}
