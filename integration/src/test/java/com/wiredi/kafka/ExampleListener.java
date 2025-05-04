package com.wiredi.kafka;

import com.wiredi.annotations.Wire;
import com.wiredi.kafka.api.KafkaListener;
import com.wiredi.kafka.api.ListenerEvent;
import com.wiredi.kafka.api.properties.KafkaProperties;
import com.wiredi.kafka.api.topics.Topics;
import com.wiredi.runtime.types.Bytes;
import com.wiredi.tests.async.AsyncResultList;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.List;

@Wire(proxy = false)
public class ExampleListener implements KafkaListener {

    public static final IllegalStateException ERROR = new IllegalStateException("Test");
    private final AsyncResultList<Handled> result = new AsyncResultList<>();
    private int counter = 0;
    private int successAfter = 1;

    public void prime(int successAtInvocation) {
        this.successAfter = successAtInvocation;
        this.counter = 0;
        this.result.prime(successAtInvocation);
    }

    public List<Handled> await() {
        return this.result.get(Duration.ofSeconds(30));
    }

    @Override
    public void handle(@NotNull ListenerEvent event) {
        counter++;
        String body = event.value(String.class);
        if (counter < successAfter) {
            result.add(new Handled(
                    body,
                    ERROR
            ));
            throw ERROR;
        } else {
            ConsumerRecord<byte[], byte[]> originalRecord = event.message().details().record();
            System.out.println("Received " + Bytes.toString(originalRecord.key()) + " => " + body + " on " + originalRecord.topic() + "." + originalRecord.partition() + " - " + originalRecord.offset());
            result.add(new Handled(
                    body,
                    null
            ));
        }
    }

    @Override
    public @NotNull Topics topics() {
        return Topics.of("test");
    }

    public record Handled(
            @NotNull String content,
            @Nullable Throwable error
    ) {
    }
}
