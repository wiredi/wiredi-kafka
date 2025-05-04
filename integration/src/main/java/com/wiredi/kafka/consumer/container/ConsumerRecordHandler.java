package com.wiredi.kafka.consumer.container;

import com.wiredi.kafka.consumer.result.RecordResult;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.jetbrains.annotations.NotNull;

public interface ConsumerRecordHandler {

    @NotNull RecordResult handle(@NotNull ConsumerRecord<byte[], byte[]> records, @NotNull KafkaListenerContainer kafkaListenerContainer);

}
