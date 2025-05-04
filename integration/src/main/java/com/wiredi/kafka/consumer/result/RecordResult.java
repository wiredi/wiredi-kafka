package com.wiredi.kafka.consumer.result;

import com.wiredi.kafka.consumer.container.KafkaListenerContainer;
import com.wiredi.kafka.consumer.result.forwarding.KafkaForwardSink;
import com.wiredi.kafka.consumer.result.logical.FirstSuccessRecordResult;
import com.wiredi.runtime.retry.RetryState;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

public interface RecordResult {

    RecordResult SUCCESS = new CommitRecordResult();

    RecordResult RETRY_RECORD = new RetryRecordResult();

    static RecordResult retry(RetryState retryState) {
        return new StatefullRetryRecordResult(retryState);
    }

    static RecordResult retry() {
        return RETRY_RECORD;
    }

    static RecordResult acknowledge() {
        return SUCCESS;
    }

    static RecordResult skip() {
        return SUCCESS;
    }

    static RecordResult forwardTo(KafkaForwardSink sink) {
        return new ForwardingRecordResult(sink);
    }

    /**
     * Handles the result of a single record.
     * <p>
     * This method is responsible for committing or seeking, depending on the implementation.
     * It can commit, but it can also seek records.
     * <p>
     * If the implementation wants to abort further processing of records, it should return false.
     * Otherwise, if the next records in the {@code polled} should be processed, it should return true.
     *
     * @param consumer the consumer that polled the records
     * @param pointer  the currently processed record, part of the {@code polled}
     * @param polled   all records that where polled
     * @return true, if further processing should continue, otherwise false
     */
    boolean apply(
            KafkaListenerContainer container,
            Consumer<byte[], byte[]> consumer,
            ConsumerRecord<byte[], byte[]> pointer,
            ConsumerRecords<byte[], byte[]> polled
    );

    default RecordResult or(RecordResult other) {
        FirstSuccessRecordResult composite = new FirstSuccessRecordResult();
        composite.add(this);
        composite.add(other);
        return composite;
    }
}
