package com.wiredi.kafka.error;

import com.wiredi.kafka.api.messaging.KafkaConsumerMessageDetails;
import com.wiredi.kafka.consumer.result.RecordResult;
import com.wiredi.runtime.messaging.MessagingResult;
import com.wiredi.runtime.messaging.Message;

public interface KafkaErrorHandler {

    default RecordResult handle(Message<KafkaConsumerMessageDetails> message, MessagingResult result) {
        return RecordResult.retry();
    }
}
