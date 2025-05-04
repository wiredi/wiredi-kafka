package com.wiredi.kafka;

import com.wiredi.annotations.Wire;
import com.wiredi.kafka.publisher.KafkaPublisher;
import com.wiredi.runtime.time.Timed;

import java.util.concurrent.ExecutionException;

@Wire
public class TestService {

    private final ExampleListener exampleListener;
    private final KafkaPublisher publisher;

    public TestService(ExampleListener exampleListener, KafkaPublisher publisher) {
        this.exampleListener = exampleListener;
        this.publisher = publisher;
    }

    public Timed runTestScenario() {
        exampleListener.prime(100);
        return Timed.of(() -> {
            try {
                publisher.publish("test", "key", "value").get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            exampleListener.await();
        });
    }
}
