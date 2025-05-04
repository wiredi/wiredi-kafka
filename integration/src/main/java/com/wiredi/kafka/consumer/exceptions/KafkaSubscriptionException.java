package com.wiredi.kafka.consumer.exceptions;

public class KafkaSubscriptionException extends RuntimeException {

    public KafkaSubscriptionException() {
    }

    public KafkaSubscriptionException(String message) {
        super(message);
    }

    public KafkaSubscriptionException(String message, Throwable cause) {
        super(message, cause);
    }

    public KafkaSubscriptionException(Throwable cause) {
        super(cause);
    }

    public KafkaSubscriptionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
