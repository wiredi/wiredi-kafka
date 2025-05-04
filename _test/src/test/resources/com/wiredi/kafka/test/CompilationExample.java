package com.wiredi.kafka.test;

import com.wiredi.annotations.Wire;
import com.wiredi.kafka.api.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;

@Wire(proxy = false)
public class CompilationExample {

    @KafkaConsumer(topics = "test")
    public void listen(ConsumerRecord<byte[], byte[]> record) {

    }

}
