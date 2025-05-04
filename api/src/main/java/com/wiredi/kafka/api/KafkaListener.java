package com.wiredi.kafka.api;

import com.wiredi.kafka.api.properties.ApacheKafkaConsumerProperties;
import com.wiredi.kafka.api.properties.KafkaListenerProperties;
import com.wiredi.kafka.api.topics.Topics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface KafkaListener {

    void handle(@NotNull ListenerEvent ListenerEvent);

    @NotNull
    Topics topics();

    /**
     * Allows you to override the consumer properties for this kafka listener
     *
     * @return the custom consumer properties for this listener
     */
    @Nullable
    default ApacheKafkaConsumerProperties consumerProperties() {
        return null;
    }

    /**
     * Allows you to override the listener properties for this kafka listener
     *
     * @return the custom listener properties for this listener
     */
    @Nullable
    default KafkaListenerProperties listenerProperties() {
        return null;
    }
}
