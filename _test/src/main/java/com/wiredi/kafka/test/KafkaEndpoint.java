package com.wiredi.kafka.test;

import com.wiredi.annotations.Wire;
import com.wiredi.kafka.api.KafkaConsumer;

@Wire
public class KafkaEndpoint {

    @KafkaConsumer(topics = "test")
    public void listen(TestKafkaListener listener) {
        System.out.println("Received " + listener);
    }

}
