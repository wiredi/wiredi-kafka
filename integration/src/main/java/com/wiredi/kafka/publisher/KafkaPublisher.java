package com.wiredi.kafka.publisher;

import com.wiredi.kafka.MessagingKafkaHeader;
import com.wiredi.kafka.api.messaging.KafkaProducerRecordDetails;
import com.wiredi.kafka.api.properties.KafkaProducerProperties;
import com.wiredi.runtime.messaging.MessageHeader;
import com.wiredi.runtime.messaging.MessageHeaders;
import com.wiredi.runtime.messaging.MessagingEngine;
import com.wiredi.runtime.messaging.Message;
import com.wiredi.runtime.types.TypeMapper;
import com.wiredi.runtime.values.Value;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Header;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.Future;

public class KafkaPublisher {

    private final MessagingEngine messageEngine;
    private final TypeMapper typeMapper;
    private final KafkaProducerProperties properties;
    private final Value<KafkaProducer<byte[], byte[]>> producer;

    public KafkaPublisher(
            MessagingEngine messageEngine,
            TypeMapper typeMapper,
            KafkaProducerProperties properties
    ) {
        this.messageEngine = messageEngine;
        this.typeMapper = typeMapper;
        this.properties = properties;
        if (properties.isLazy()) {
            this.producer = Value.lazy(() -> new KafkaProducer<>(this.properties.apacheConfig().originals()));
        } else {
            this.producer = Value.just(new KafkaProducer<>(this.properties.apacheConfig().originals()));
        }
    }

    @NotNull
    public PublisherBuilder publish(@NotNull Object value) {
        return new PublisherBuilder(this, messageEngine.serialize(value));
    }

    @NotNull
    public Future<RecordMetadata> publish(
            @NotNull String topic,
            @NotNull Object value
    ) {
        return publish(
                topic,
                null,
                null,
                null,
                value,
                Collections.emptyList()
        );
    }

    @NotNull
    public Future<RecordMetadata> publish(
            @NotNull String topic,
            @Nullable Object key,
            @NotNull Object value
    ) {
        return publish(
                topic,
                null,
                null,
                key,
                value,
                Collections.emptyList()
        );
    }

    @NotNull
    public Future<RecordMetadata> publish(
            @NotNull String topic,
            @Nullable Object key,
            @NotNull Object value,
            @Nullable Iterable<MessageHeader> headers
    ) {
        return publish(
                topic,
                null,
                null,
                key,
                value,
                headers
        );
    }

    @NotNull
    public Future<RecordMetadata> publish(
            @NotNull String topic,
            @Nullable Integer partition,
            @Nullable Object key,
            @NotNull Object value,
            @Nullable Iterable<MessageHeader> headers
    ) {
        return publish(
                topic,
                partition,
                null,
                key,
                value,
                headers
        );
    }

    @NotNull
    public Future<RecordMetadata> publish(
            @NotNull String topic,
            @Nullable Integer partition,
            @Nullable Long timestamp,
            @Nullable Object key,
            @NotNull Object value,
            @Nullable Iterable<MessageHeader> headers
    ) {
        final byte[] serializedKey = Optional.ofNullable(key).map(it -> typeMapper.convert(it, byte[].class)).orElse(null);
        final Message<KafkaProducerRecordDetails> message;
        final KafkaProducerRecordDetails details = new KafkaProducerRecordDetails(topic, key, partition, timestamp);
        try {
            message = messageEngine.serialize(value, MessageHeaders.of(headers), details);
        } catch (Throwable throwable) {
            throw new IllegalStateException("Serialization failed " + throwable.getMessage(), throwable);
        }

        return publish(new ProducerRecord<>(
                topic,
                partition,
                timestamp,
                serializedKey,
                message.body(),
                map(message.headers())
        ));
    }

    @NotNull
    public Future<RecordMetadata> publish(
            Message<KafkaProducerRecordDetails> message
    ) {
        byte[] serializedKey = message.details().key().map(it -> typeMapper.convert(it, byte[].class)).orElse(null);
        return publish(new ProducerRecord<>(
                message.details().topic(),
                message.details().partition(),
                message.details().timestamp(),
                serializedKey,
                message.body(),
                map(message.headers())
        ));
    }

    public Future<RecordMetadata> publish(ProducerRecord<byte[], byte[]> producerRecord) {
        return this.producer.get().send(producerRecord);
    }

    private Iterable<Header> map(MessageHeaders headers) {
        Map<String, List<MessageHeader>> messagingHeaders = headers.map();
        return messagingHeaders
                .keySet()
                .stream()
                .flatMap(headerName -> messagingHeaders.get(headerName)
                        .stream()
                        .map(header -> (Header) new MessagingKafkaHeader(header))
                )
                .toList();
    }
}
