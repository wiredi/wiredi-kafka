package com.wiredi.kafka.buffer.relay;

import com.wiredi.annotations.Provider;
import com.wiredi.annotations.stereotypes.DefaultConfiguration;
import com.wiredi.kafka.api.properties.KafkaProperties;
import com.wiredi.kafka.buffer.outbox.MessageOutbox;
import com.wiredi.kafka.buffer.outbox.OutboxMessageRelayExecutor;
import com.wiredi.kafka.buffer.outbox.OutboxMessageRelayExecutorProperties;
import jakarta.inject.Named;
import org.apache.kafka.clients.producer.KafkaProducer;

@DefaultConfiguration
public class MessageRelayConfiguration {

    @Provider
    @Named("outbox")
    public MessageRelayExecutor outboxMessageRelayExecutor(
            MessageOutbox messageOutbox,
            OutboxMessageRelayExecutorProperties outboxProperties,
            KafkaProperties kafkaProperties
    ) {
        if (messageOutbox.buffersMessages()) {
            return new OutboxMessageRelayExecutor(new KafkaProducer<>(kafkaProperties.kafkaProducerProperties().apacheConfig().originals()), messageOutbox, outboxProperties);
        } else {
            return new NoOpMessageRelayExecutor();
        }
    }
}
