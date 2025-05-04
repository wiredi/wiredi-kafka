package com.wiredi.kafka.buffer.outbox;

import com.wiredi.kafka.buffer.relay.AbstractMessageRelayExecutor;
import com.wiredi.kafka.buffer.relay.MessageRelayExecution;
import org.apache.kafka.clients.producer.Producer;

public class OutboxMessageRelayExecutor extends AbstractMessageRelayExecutor<OutboxMessageRelayExecutorProperties> {

    private final Producer<byte[], byte[]> producer;
    private final MessageOutbox messageOutbox;
    private final int pollBatchSize;

    public OutboxMessageRelayExecutor(
            Producer<byte[], byte[]> producer,
            MessageOutbox messageOutbox,
            OutboxMessageRelayExecutorProperties properties
    ) {
        super(properties);
        this.producer = producer;
        this.messageOutbox = messageOutbox;
        this.pollBatchSize = properties.polling().pollBatchSize();
    }

    @Override
    protected MessageRelayExecution newRelayExecution() {
        return new OutboxMessageRelayExecution(messageOutbox, pollBatchSize, producer);
    }
}
