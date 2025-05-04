package com.wiredi.kafka.api.topics;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public interface Topics {

    void subscribe(
            @NotNull Consumer<?, ?> consumer,
            @NotNull ConsumerRebalanceListener rebalanceListener
    );

    boolean matches(@NotNull String topic);

    static @NotNull Topics empty() {
        return new EmptyTopics();
    }

    static @NotNull Topics of(@NotNull Pattern pattern) {
        return new PatternTopics(pattern);
    }

    static @NotNull Topics of(@NotNull List<@NotNull String> topics) {
        return new TopicsList(topics);
    }

    static @NotNull Topics of(@NotNull String topic, @NotNull String... additionalTopics) {
        List<@NotNull String> topicList = new ArrayList<>();
        topicList.add(topic);
        topicList.addAll(Arrays.asList(additionalTopics));
        return new TopicsList(topicList);
    }
}
