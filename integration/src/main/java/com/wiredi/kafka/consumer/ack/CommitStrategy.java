package com.wiredi.kafka.consumer.ack;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

public interface CommitStrategy {

    default void afterEach(
            Consumer<?, ?> consumer,
            ConsumerRecord<?, ?> record
    ) {}

    default void afterAll(
            Consumer<?, ?> consumer,
            ConsumerRecords<?, ?> records
    ) {}
}
