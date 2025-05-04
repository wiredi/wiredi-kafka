package com.wiredi.kafka.consumer.ack;

import com.wiredi.logging.Logging;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

public class AfterAllCommitStrategy implements CommitStrategy {

    private final boolean async;
    private static final Logging logger = Logging.getInstance(CommitStrategy.class);

    public AfterAllCommitStrategy(boolean async) {
        this.async = async;
    }

    @Override
    public void afterAll(Consumer<?, ?> consumer, ConsumerRecords<?, ?> record) {
        logger.info(() -> "Commiting " + record.count() + " records.");
        if (async) {
            consumer.commitAsync();
        } else {
            consumer.commitSync();
        }
    }
}
