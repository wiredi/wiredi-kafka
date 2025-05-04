package com.wiredi.kafka.api;

import com.wiredi.kafka.api.messaging.KafkaConsumerMessageDetails;
import com.wiredi.runtime.messaging.Message;
import org.apache.kafka.common.record.TimestampType;

public interface ListenerEvent {

    KafkaConsumerMessageDetails details();

    Message<KafkaConsumerMessageDetails> message();

    byte[] value();

    <T> T value(Class<T> valueType);

    <T> T key(Class<T> keyType);

    byte[] key();

    <T> T topic(Class<T> type);

    String topic();

    <T> T partition(Class<T> type);

    int partition();

    <T> T offset(Class<T> type);

    long offset();

    <T> T timestamp(Class<T> type);

    long timestamp();

    <T> T timestampType(Class<T> type);

    TimestampType timestampType();

    <T> T serializedKeySize(Class<T> type);

    int serializedKeySize();

    <T> T serializedValueSize(Class<T> type);

    int serializedValueSize();
}
