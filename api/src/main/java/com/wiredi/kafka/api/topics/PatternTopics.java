package com.wiredi.kafka.api.topics;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class PatternTopics implements Topics {

    @NotNull
    private final Pattern pattern;

    public PatternTopics(@NotNull Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public void subscribe(
            @NotNull Consumer<?, ?> consumer,
            @NotNull ConsumerRebalanceListener rebalanceListener
    ) {
        consumer.subscribe(pattern, rebalanceListener);
    }

    @Override
    public boolean matches(@NotNull String topic) {
        return pattern.matcher(topic).matches();
    }

    @Override
    public String toString() {
        return "TopicPattern(" + pattern + ')';
    }
}
