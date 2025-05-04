package com.wiredi.kafka.api.messaging;

import com.wiredi.kafka.api.KafkaListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.jetbrains.annotations.NotNull;

public class KafkaConsumerMessageDetails extends KafkaMessageDetails {

    @NotNull
    private final ConsumerRecord<byte[], byte[]> record;
    @NotNull
    private final KafkaConsumer<byte[], byte[]> kafkaConsumer;
    @NotNull
    private final KafkaListener kafkaListener;

    public KafkaConsumerMessageDetails(
            @NotNull ConsumerRecord<byte[], byte[]> record,
            @NotNull KafkaConsumer<byte[], byte[]> consumer,
            @NotNull KafkaListener kafkaListener
    ) {
        this.record = record;
        this.kafkaConsumer = consumer;
        this.kafkaListener = kafkaListener;
    }

    @NotNull
    public ConsumerRecord<byte[], byte[]> record() {
        return record;
    }

    public KafkaConsumer<byte[], byte[]> kafkaConsumer() {
        return kafkaConsumer;
    }

    public KafkaListener kafkaListener() {
        return kafkaListener;
    }
}
