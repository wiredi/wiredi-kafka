package com.wiredi.kafka.buffer.relay;

import java.util.concurrent.atomic.AtomicBoolean;

public class MessageRelayExecutionRunnable implements Runnable {

    private final AtomicBoolean running = new AtomicBoolean(true);
    private final MessageRelayExecution execution;
    private final SleepState sleepState;

    public MessageRelayExecutionRunnable(MessageRelayExecution execution, MessageRelayExecutorProperties.Polling pollingProperties) {
        this.execution = execution;
        this.sleepState = new SleepState(pollingProperties);
    }

    @Override
    public void run() {
        while (running.get()) {
            MessageRelayExecution.Result result = execute();
            try {
                sleepState.sleep(result);
            } catch (InterruptedException e) {
                e.printStackTrace(); // Use slf4j for this
            }
        }
    }

    private MessageRelayExecution.Result execute() {
        try {
            return execution.execute();
        } catch (Exception e) {
            e.printStackTrace(); // Use slf4j for this
            return MessageRelayExecution.Result.FAILURE;
        }
    }
}
