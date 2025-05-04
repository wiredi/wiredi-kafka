package com.wiredi.kafka.test;

import com.wiredi.kafka.publisher.KafkaPublisher;
import com.wiredi.runtime.WiredApplication;
import com.wiredi.runtime.WiredApplicationInstance;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        WiredApplicationInstance application = WiredApplication.start();

        KafkaPublisher kafkaPublisher = application.wireRepository().get(KafkaPublisher.class);

        while(true) {
            System.out.println("Publishing...");
            kafkaPublisher.publish("test", "test", new TestEntity("test"));
            Thread.sleep(TimeUnit.SECONDS.toMillis(ThreadLocalRandom.current().nextInt(5, 20)));
        }
    }
}
