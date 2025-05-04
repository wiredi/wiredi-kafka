package com.wiredi.kafka.publisher;

import com.wiredi.runtime.messaging.Message;
import com.wiredi.runtime.messaging.MessageHeader;
import com.wiredi.runtime.messaging.MessageHeaders;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Future;

public class PublisherBuilder {

    @NotNull
    private final Object value;
    @NotNull
    private final MessageHeaders.Builder headers = MessageHeaders.builder();
    @NotNull
    private final KafkaPublisher publisher;
    @Nullable
    private Object key;
    @Nullable
    private Integer partition;
    @Nullable
    private Long timestamp;

    protected PublisherBuilder(@NotNull KafkaPublisher publisher, @NotNull Object body) {
        this.publisher = publisher;
        this.value = body;
    }

    public PublisherBuilder withKey(@Nullable Object key) {
        this.key = key;
        return this;
    }

    public PublisherBuilder withHeader(String key, String value) {
        this.headers.add(key, value);
        return this;
    }

    public PublisherBuilder withHeader(String key, byte[] value) {
        this.headers.add(key, value);
        return this;
    }

    public PublisherBuilder withHeader(MessageHeader entry) {
        this.headers.add(entry);
        return this;
    }

    public PublisherBuilder withHeaders(Iterable<MessageHeader> headers) {
        this.headers.addAll(headers);
        return this;
    }

    public PublisherBuilder withHeadersOf(ConsumerRecord<?, ?> record) {
        record.headers().forEach(it -> withHeader(it.key(), it.value()));
        return this;
    }

    public PublisherBuilder withHeadersOf(Message<?> message) {
        this.headers.addAll(message.headers());
        return this;
    }

    public PublisherBuilder withTimestamp(@Nullable Long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public PublisherBuilder toPartition(@Nullable Integer partition) {
        this.partition = partition;
        return this;
    }

    public Future<RecordMetadata> toTopic(String topic) {
        return publisher.publish(
                topic,
                partition,
                timestamp,
                key,
                value,
                headers.build()
        );
    }
}
