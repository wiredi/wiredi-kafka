package com.wiredi.kafka.consumer.exceptions;

public class KafkaAssignmentException extends RuntimeException {

    public KafkaAssignmentException() {
    }

    public KafkaAssignmentException(String message) {
        super(message);
    }

    public KafkaAssignmentException(String message, Throwable cause) {
        super(message, cause);
    }

    public KafkaAssignmentException(Throwable cause) {
        super(cause);
    }

    public KafkaAssignmentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
