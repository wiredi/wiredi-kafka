package com.wiredi.kafka.consumer.result;

import com.wiredi.kafka.consumer.container.KafkaListenerContainer;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

public class RetryRecordResult implements RecordResult {

    @Override
    public boolean apply(
            KafkaListenerContainer container,
            Consumer<byte[], byte[]> consumer,
            ConsumerRecord<byte[], byte[]> pointer,
            ConsumerRecords<byte[], byte[]> polled
    ) {
        container.seekTo(pointer);
        return false;
    }
}
