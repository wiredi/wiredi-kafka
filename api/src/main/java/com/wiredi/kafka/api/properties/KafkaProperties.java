package com.wiredi.kafka.api.properties;

import com.wiredi.annotations.Wire;
import org.apache.kafka.clients.consumer.ConsumerConfig;

@Wire
public record KafkaProperties(
        KafkaListenerProperties listenerProperties,
        ApacheKafkaConsumerProperties consumerProperties,
        KafkaProducerProperties kafkaProducerProperties
) {

    public ConsumerConfig buildConsumerConfig() {
        return consumerProperties.toConsumerConfig();
    }
}
