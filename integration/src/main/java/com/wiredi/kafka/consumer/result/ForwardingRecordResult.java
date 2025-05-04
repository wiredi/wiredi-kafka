package com.wiredi.kafka.consumer.result;

import com.wiredi.kafka.consumer.container.KafkaListenerContainer;
import com.wiredi.kafka.consumer.result.forwarding.KafkaForwardSink;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.util.function.Function;

public class ForwardingRecordResult implements RecordResult {

    private static final Function<ConsumerRecord<byte[], byte[]>, ConsumerRecord<byte[], byte[]>> DEFAULT_RECORD_TRANSLATION = Function.identity();
    private final Function<ConsumerRecord<byte[], byte[]>, ConsumerRecord<byte[], byte[]>> recordTranslation;
    private final KafkaForwardSink sink;

    public ForwardingRecordResult(
            KafkaForwardSink sink,
            Function<ConsumerRecord<byte[], byte[]>,
                    ConsumerRecord<byte[], byte[]>> recordTranslation
    ) {
        this.sink = sink;
        this.recordTranslation = recordTranslation;
    }

    public ForwardingRecordResult(KafkaForwardSink sink) {
        this(sink, DEFAULT_RECORD_TRANSLATION);
    }

    @Override
    public boolean apply(
            KafkaListenerContainer container,
            Consumer<byte[], byte[]> consumer,
            ConsumerRecord<byte[], byte[]> pointer,
            ConsumerRecords<byte[], byte[]> polled
    ) {
        return sink.accept(
                recordTranslation.apply(pointer)
        );
    }
}
