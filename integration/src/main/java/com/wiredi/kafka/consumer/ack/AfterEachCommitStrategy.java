package com.wiredi.kafka.consumer.ack;

import com.wiredi.logging.Logging;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

public class AfterEachCommitStrategy implements CommitStrategy {

    private final boolean async;
    private static final Logging logger = Logging.getInstance(CommitStrategy.class);

    public AfterEachCommitStrategy(boolean async) {
        this.async = async;
    }

    @Override
    public void afterEach(Consumer<?, ?> consumer, ConsumerRecord<?, ?> record) {
        logger.info(() -> "Commiting " + record.topic() + "." + record.partition() + "." + record.offset());
        if (async) {
            consumer.commitAsync();
        } else {
            consumer.commitSync();
        }
    }
}
