package com.wiredi.kafka.consumer.result;

import com.wiredi.kafka.consumer.container.KafkaListenerContainer;
import com.wiredi.runtime.retry.RetryState;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.jetbrains.annotations.NotNull;

public class StatefullRetryRecordResult implements RecordResult {

    @NotNull
    private final RetryState retryState;

    public StatefullRetryRecordResult(@NotNull RetryState retryState) {
        this.retryState = retryState;
    }

    @Override
    public boolean apply(
            KafkaListenerContainer container,
            Consumer<byte[], byte[]> consumer,
            ConsumerRecord<byte[], byte[]> pointer,
            ConsumerRecords<byte[], byte[]> polled
    ) {
        if (retryState.sleepAndAdvance()) {
            container.seekTo(pointer);
            return false;
        } else {
            return true;
        }
    }
}
