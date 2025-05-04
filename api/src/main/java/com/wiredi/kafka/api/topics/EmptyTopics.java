package com.wiredi.kafka.api.topics;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.jetbrains.annotations.NotNull;

public class EmptyTopics implements Topics {
    @Override
    public void subscribe(@NotNull Consumer<?, ?> consumer, @NotNull ConsumerRebalanceListener rebalanceListener) {
        throw new IllegalStateException("No topics have been configured");
    }

    @Override
    public boolean matches(@NotNull String topic) {
        return false;
    }

    @Override
    public String toString() {
        return "NoTopics()";
    }
}
