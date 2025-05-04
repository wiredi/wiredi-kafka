package com.wiredi.kafka.api.topics;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TopicsList implements Topics {

    private final List<String> topics;

    public TopicsList(List<String> topics) {
        this.topics = topics;
    }

    @Override
    public void subscribe(@NotNull Consumer<?, ?> consumer, @NotNull ConsumerRebalanceListener rebalanceListener) {
        consumer.subscribe(topics, rebalanceListener);
    }

    @Override
    public boolean matches(@NotNull String topic) {
        return topics.contains(topic);
    }

    @Override
    public String toString() {
        return "Topics(" + topics + ')';
    }
}
