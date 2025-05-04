package com.wiredi.kafka.buffer.relay;

import java.time.Duration;

public class SleepState {

    private final MessageRelayExecutorProperties.Polling pollingProperties;
    private Duration timeout;

    public SleepState(MessageRelayExecutorProperties.Polling pollingProperties) {
        this.pollingProperties = pollingProperties;
        timeout = pollingProperties.idleTimeout();
    }

    public void sleep(MessageRelayExecution.Result currentResult) throws InterruptedException {
        switch (currentResult) {
            case CONTINUE -> this.timeout = Duration.ZERO;
            case IDLE -> this.timeout = pollingProperties.idleTimeout();
            case FAILURE -> {
                this.timeout = timeout.plus(pollingProperties.failureTimeoutIncrease());
                if (this.timeout.compareTo(pollingProperties.maxFailureTimeout()) > 0) {
                    this.timeout = pollingProperties.maxFailureTimeout();
                }
            }
        }

        Thread.sleep(timeout);
    }
}
