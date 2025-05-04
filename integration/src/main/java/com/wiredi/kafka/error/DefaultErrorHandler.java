package com.wiredi.kafka.error;

import com.wiredi.kafka.api.messaging.KafkaConsumerMessageDetails;
import com.wiredi.kafka.consumer.result.RecordResult;
import com.wiredi.logging.Logging;
import com.wiredi.runtime.messaging.MessagingResult;
import com.wiredi.runtime.messaging.Message;

public class DefaultErrorHandler implements KafkaErrorHandler {

    private static final Logging logger = Logging.getInstance(DefaultErrorHandler.class);

    @Override
    public RecordResult handle(Message<KafkaConsumerMessageDetails> message, MessagingResult result) {
        logger.error(() -> "Error while processing record on topic " + message.details().record().topic() + " with offset " + message.details().record().offset(), result.error());
        return RecordResult.retry();
    }
}
