package com.wiredi.kafka.publisher;

import com.wiredi.kafka.api.ListenerEvent;
import com.wiredi.kafka.api.messaging.KafkaConsumerMessageDetails;
import com.wiredi.runtime.messaging.MessagingEngine;
import com.wiredi.runtime.messaging.Message;
import com.wiredi.runtime.types.TypeMapper;
import org.apache.kafka.common.record.TimestampType;

import java.util.HashMap;
import java.util.Map;

public class CachingListenerEvent implements ListenerEvent {

    private final TypeMapper typeMapper;
    private final MessagingEngine messagingEngine;
    private final Message<KafkaConsumerMessageDetails> message;
    private final Map<Class<?>, Object> valueCache = new HashMap<>();

    public CachingListenerEvent(
            Message<KafkaConsumerMessageDetails> message,
            TypeMapper typeMapper,
            MessagingEngine messagingEngine
    ) {
        this.typeMapper = typeMapper;
        this.messagingEngine = messagingEngine;
        this.message = message;
    }

    @Override
    public KafkaConsumerMessageDetails details() {
        return message.details();
    }

    @Override
    public Message<KafkaConsumerMessageDetails> message() {
        return message;
    }

    @Override
    public byte[] value() {
        return details().record().value();
    }

    @Override
    public <T> T value(Class<T> valueType) {
        return (T) valueCache.computeIfAbsent(valueType, t -> messagingEngine.deserialize(message, valueType));
    }

    @Override
    public <T> T key(Class<T> keyType) {
        return typeMapper.convert(details().record().key(), keyType);
    }

    @Override
    public byte[] key() {
        return details().record().key();
    }

    @Override
    public <T> T topic(Class<T> type) {
        return typeMapper.convert(details().record().topic(), type);
    }

    @Override
    public String topic() {
        return details().record().topic();
    }

    @Override
    public <T> T partition(Class<T> type) {
        return typeMapper.convert(details().record().partition(), type);
    }

    @Override
    public int partition() {
        return details().record().partition();
    }

    @Override
    public <T> T offset(Class<T> type) {
        return typeMapper.convert(details().record().offset(), type);
    }

    @Override
    public long offset() {
        return details().record().offset();
    }

    @Override
    public <T> T timestamp(Class<T> type) {
        return typeMapper.convert(details().record().timestamp(), type);
    }

    @Override
    public long timestamp() {
        return details().record().timestamp();
    }

    @Override
    public <T> T timestampType(Class<T> type) {
        return typeMapper.convert(details().record().timestampType(), type);
    }

    @Override
    public TimestampType timestampType() {
        return details().record().timestampType();
    }

    @Override
    public <T> T serializedKeySize(Class<T> type) {
        return typeMapper.convert(details().record().serializedKeySize(), type);
    }

    @Override
    public int serializedKeySize() {
        return details().record().serializedKeySize();
    }

    @Override
    public <T> T serializedValueSize(Class<T> type) {
        return typeMapper.convert(details().record().serializedValueSize(), type);
    }

    @Override
    public int serializedValueSize() {
        return details().record().serializedValueSize();
    }

}
