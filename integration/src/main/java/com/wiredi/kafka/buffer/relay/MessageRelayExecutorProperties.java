package com.wiredi.kafka.buffer.relay;

import java.time.Duration;

public interface MessageRelayExecutorProperties {

    ThreadPool threadPool();
    Polling polling();

    record ThreadPool(
            int pollingThread,
            boolean virtual,
            int threadPriority,
            boolean deamon
    ) {
        public void validate() {
            if (pollingThread <= 0) {
                throw new IllegalArgumentException("Invalid thread pool configuration. ");
            }
        }
    }

    record Polling(
            int pollBatchSize,
            Duration idleTimeout,
            Duration failureTimeoutIncrease,
            Duration maxFailureTimeout
    ) {
    }
}
