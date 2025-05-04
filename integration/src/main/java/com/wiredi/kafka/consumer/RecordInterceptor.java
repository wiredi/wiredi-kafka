package com.wiredi.kafka.consumer;

import com.wiredi.kafka.consumer.container.KafkaListenerContainer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A low-level record interceptor.
 * <p>
 * This interceptor is called before and after the record is processed.
 * It only cares about the raw consumer record.
 * <p>
 * Contrary to the {@link com.wiredi.runtime.messaging.RequestAware}, this is low-level and operates on the raw Kafka record.
 * If you need to handle a deserialized message, use the {@link com.wiredi.runtime.messaging.RequestAware} instead.
 *
 * @see KafkaListenerContainer#addInterceptor(RecordInterceptor)
 */
public interface RecordInterceptor {

    @Nullable
    default ConsumerRecord<byte[], byte[]> beforeHandle(@NotNull ConsumerRecord<byte[], byte[]> record) {
        return record;
    }

    default void handleSuccess(@NotNull ConsumerRecord<byte[], byte[]> record) {
    }

    default void handleFailure(@NotNull ConsumerRecord<byte[], byte[]> record, @Nullable Throwable throwable) {
    }

    default void afterHandle(@NotNull ConsumerRecord<byte[], byte[]> record) {
    }
}
