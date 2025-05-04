package com.wiredi.kafka;

import com.wiredi.kafka.consumer.container.KafkaListenerContainer;
import com.wiredi.runtime.async.barriers.MutableBarrier;
import com.wiredi.runtime.async.barriers.SemaphoreBarrier;
import com.wiredi.runtime.async.state.AbstractState;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class KafkaConsumerState extends AbstractState<List<KafkaListenerContainer>> {

    private final MutableBarrier barrier = SemaphoreBarrier.closed()
            .withTraversingFailedMessage("The kafka consumers did not start in time.");

    public KafkaConsumerState() {
        super(new ArrayList<>());
    }

    @Override
    public void awaitUntilSet() {
        barrier.traverse();
        tryThrow();
    }

    @Override
    public void awaitUntilSet(@NotNull Duration duration) {
        barrier.traverse(duration);
        tryThrow();
    }

    public void add(KafkaListenerContainer container) {
        if (this.value == null) {
            this.value = new ArrayList<>(List.of(container));
        } else {
            value.add(container);
        }
    }

    public void complete() {
        this.barrier.open();
    }

    public void markAsDirty(Throwable throwable) {
        doMarkAsDirty(throwable);
        this.barrier.open();
    }
}
