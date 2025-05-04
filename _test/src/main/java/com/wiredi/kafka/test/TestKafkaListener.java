package com.wiredi.kafka.test;

import com.wiredi.annotations.Wire;
import com.wiredi.kafka.api.KafkaListener;
import com.wiredi.kafka.api.ListenerEvent;
import com.wiredi.kafka.api.topics.Topics;
import org.jetbrains.annotations.NotNull;

@Wire
public class TestKafkaListener implements KafkaListener {

    @Override
    public void handle(@NotNull ListenerEvent ListenerEvent) {
        System.out.println("Received " + ListenerEvent.value(String.class));
    }

    @Override
    public @NotNull Topics topics() {
        return Topics.of("test");
    }
}
