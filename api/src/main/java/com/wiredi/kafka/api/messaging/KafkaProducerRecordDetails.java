package com.wiredi.kafka.api.messaging;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class KafkaProducerRecordDetails extends KafkaMessageDetails{

    private final @NotNull String topic;
    private final @Nullable Integer partition;
    private final @Nullable Long timestamp;
    private final @Nullable Object key;

    public KafkaProducerRecordDetails(@NotNull String topic) {
        this(topic, null, null, null);
    }

    public KafkaProducerRecordDetails(@NotNull String topic, @Nullable Object key) {
        this(topic, key, null, null);
    }

    public KafkaProducerRecordDetails(@NotNull String topic, @Nullable Object key, @Nullable Integer partition) {
        this(topic, key, partition, null);
    }

    public KafkaProducerRecordDetails(@NotNull String topic, @Nullable Object key, @Nullable Integer partition, @Nullable Long timestamp) {
        this.topic = topic;
        this.partition = partition;
        this.timestamp = timestamp;
        this.key = key;    }

    @NotNull
    public String topic() {
        return topic;
    }

    @Nullable
    public Integer partition() {
        return partition;
    }

    @Nullable
    public Long timestamp() {
        return timestamp;
    }

    @NotNull
    public Optional<Object> key() {
        return Optional.ofNullable(key);
    }
}
