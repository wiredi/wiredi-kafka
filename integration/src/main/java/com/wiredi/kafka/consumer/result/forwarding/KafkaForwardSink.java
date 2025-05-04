package com.wiredi.kafka.consumer.result.forwarding;

import com.wiredi.kafka.publisher.KafkaPublisher;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class KafkaForwardSink {

    private final KafkaPublisher publisher;
    private final String targetTopic;

    public KafkaForwardSink(KafkaPublisher publisher, String targetTopic) {
        this.publisher = publisher;
        this.targetTopic = targetTopic;
    }

    public boolean accept(ConsumerRecord<byte[], byte[]> record) {
        publisher.publish(record.value())
                .withKey(record.key())
                .withHeadersOf(record)
                .toTopic(targetTopic);

        return true;
    }
}
