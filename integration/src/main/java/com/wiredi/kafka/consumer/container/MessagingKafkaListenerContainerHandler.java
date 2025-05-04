package com.wiredi.kafka.consumer.container;

import com.wiredi.kafka.api.messaging.KafkaConsumerMessageDetails;
import com.wiredi.kafka.consumer.exceptions.UnsupportedMessagingResultException;
import com.wiredi.kafka.consumer.result.RecordResult;
import com.wiredi.kafka.publisher.CachingListenerEvent;
import com.wiredi.runtime.messaging.Message;
import com.wiredi.runtime.messaging.MessageHeaders;
import com.wiredi.runtime.messaging.MessagingEngine;
import com.wiredi.runtime.messaging.MessagingResult;
import com.wiredi.runtime.types.TypeMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.jetbrains.annotations.NotNull;

public class MessagingKafkaListenerContainerHandler implements ConsumerRecordHandler {

    private final TypeMapper typeMapper;
    private final MessagingEngine messagingEngine;

    public MessagingKafkaListenerContainerHandler(
            TypeMapper typeMapper,
            MessagingEngine messagingEngine
    ) {
        this.typeMapper = typeMapper;
        this.messagingEngine = messagingEngine;
    }

    @Override
    public @NotNull RecordResult handle(@NotNull ConsumerRecord<byte[], byte[]> record, @NotNull KafkaListenerContainer kafkaListenerContainer) {

        Message<KafkaConsumerMessageDetails> message = Message.builder(record.value())
                .addHeaders(mapHeaders(record))
                .withDetails(new KafkaConsumerMessageDetails(
                        record,
                        kafkaListenerContainer.getKafkaConsumer(),
                        kafkaListenerContainer.getKafkaListener()
                ))
                .build();

        MessagingResult result =messagingEngine.handleMessage(message, adjustedMessage -> {
            kafkaListenerContainer.getKafkaListener().handle(new CachingListenerEvent(adjustedMessage, typeMapper, messagingEngine));
        });
        if (result instanceof MessagingResult.Faulty) {
            kafkaListenerContainer.handleFailure(record, result.error());
            return kafkaListenerContainer.errorHandler().handle(message, result);
        } else if (result.hasFailed()) {
            kafkaListenerContainer.handleFailure(record, result.error());
            Throwable error = result.error();
            if (error != null) {
                return kafkaListenerContainer.errorHandler().handle(message, result);
            }
        }

        if (result.wasSuccessful() || result.wasSkipped()) {
            kafkaListenerContainer.handleSuccess(record);
            return RecordResult.SUCCESS;
        }

        throw new UnsupportedMessagingResultException(result);

    }

    private MessageHeaders mapHeaders(ConsumerRecord<byte[], byte[]> record) {
        MessageHeaders.Builder builder = MessageHeaders.builder();
        record.headers().forEach(header -> builder.add(header.key(), header.value()));
        return builder.build();
    }
}
