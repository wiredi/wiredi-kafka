package com.wiredi.kafka.consumer.result.logical;

import com.wiredi.kafka.consumer.container.KafkaListenerContainer;
import com.wiredi.kafka.consumer.result.RecordResult;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.util.ArrayList;
import java.util.List;

public class FirstSuccessRecordResult implements RecordResult {

    private final List<RecordResult> recordResult = new ArrayList<>();

    public void add(RecordResult recordResult) {
        this.recordResult.add(recordResult);
    }

    @Override
    public boolean apply(
            KafkaListenerContainer container,
            Consumer<byte[], byte[]> consumer,
            ConsumerRecord<byte[], byte[]> pointer,
            ConsumerRecords<byte[], byte[]> polled
    ) {
        for (RecordResult result : recordResult) {
            if (result.apply(container, consumer, pointer, polled)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public RecordResult or(RecordResult other) {
        recordResult.add(other);
        return this;
    }
}
