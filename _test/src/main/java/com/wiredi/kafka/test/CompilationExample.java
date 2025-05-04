package com.wiredi.kafka.test;

import com.wiredi.annotations.Wire;
import com.wiredi.kafka.api.KafkaConsumer;
import com.wiredi.kafka.api.messaging.KafkaConsumerMessageDetails;
import com.wiredi.runtime.messaging.MessageHeader;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Headers;

import java.time.Instant;

@Wire(proxy = false)
public class CompilationExample {

    @KafkaConsumer(topics = "test")
    public void listen(
            Headers headers,
            MessageHeader messagingHeaders,
            KafkaConsumerMessageDetails messageDetails,
            @KafkaConsumer.Offset long offset,
            @KafkaConsumer.Partition int partition,
            @KafkaConsumer.Topic String topic,
            @KafkaConsumer.Payload TestEntity payload,
            @KafkaConsumer.Key TestEntity key,
            @KafkaConsumer.Timestamp Instant timestamp,
            ConsumerRecord<byte[], byte[]> record
    ) {
    }
}
