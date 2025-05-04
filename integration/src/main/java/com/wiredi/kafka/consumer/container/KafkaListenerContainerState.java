package com.wiredi.kafka.consumer.container;


import com.wiredi.runtime.async.barriers.MutableBarrier;
import com.wiredi.runtime.async.barriers.SemaphoreBarrier;
import com.wiredi.runtime.lang.Preconditions;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;

public class KafkaListenerContainerState {

    private final MutableBarrier startingBarrier = SemaphoreBarrier.closed();
    private final MutableBarrier shutdownBarrier = SemaphoreBarrier.closed();
    private ContainerState value = ContainerState.CLOSED;

    void set(ContainerState value) {
        value.validateStep(this.value);
        this.value = value;
        value.apply(startingBarrier, shutdownBarrier);
    }

    public void requireNotRunning() {
        Preconditions.is(isNotRunning(), () -> "The listener is already running");
    }

    public boolean isRunning() {
        return this.value == ContainerState.RUNNING;
    }

    public boolean isNotRunning() {
        return this.value != ContainerState.RUNNING;
    }

    public ContainerState value() {
        return this.value;
    }

    public void waitUntilRunning() {
        this.startingBarrier.traverse();
    }

    public void waitUntilRunning(@Nullable Duration duration) {
        if (duration == null) {
            this.startingBarrier.traverse();
        } else {
            this.startingBarrier.traverse(duration);
        }
    }

    public void waitUntilStopped() {
        this.shutdownBarrier.traverse();
    }

    public void waitUntilStopped(@Nullable Duration duration) {
        if (duration == null) {
            this.shutdownBarrier.traverse();
        } else {
            this.shutdownBarrier.traverse(duration);
        }
    }
}
