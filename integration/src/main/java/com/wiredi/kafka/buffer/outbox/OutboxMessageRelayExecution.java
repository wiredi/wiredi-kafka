package com.wiredi.kafka.buffer.outbox;

import com.wiredi.kafka.buffer.BufferedMessage;
import com.wiredi.kafka.buffer.relay.MessageRelayExecution;
import com.wiredi.runtime.messaging.MessageHeader;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;

import java.util.List;

public class OutboxMessageRelayExecution implements MessageRelayExecution {

    private final MessageOutbox messageOutbox;
    private final int pollBatchSize;
    private final Producer<byte[], byte[]> producer;

    public OutboxMessageRelayExecution(
            MessageOutbox messageOutbox,
            int pollBatchSize,
            Producer<byte[], byte[]> producer
    ) {
        this.messageOutbox = messageOutbox;
        this.pollBatchSize = pollBatchSize;
        this.producer = producer;
    }

    @Override
    public Result execute() {
        List<BufferedMessage<?>> next = messageOutbox.poll(pollBatchSize);
        if (next == null || next.isEmpty()) {
            return Result.IDLE;
        }

        for (BufferedMessage<?> bufferedMessage : next) {
            try {
                producer.send(new ProducerRecord<>(
                        bufferedMessage.topic(),
                        bufferedMessage.partition(),
                        bufferedMessage.timestamp(),
                        bufferedMessage.key(),
                        bufferedMessage.payload(),
                        map(bufferedMessage.headers())
                ));
                bufferedMessage.ack().run();
            } catch (Throwable t) {
                bufferedMessage.nack().accept(t);
                return Result.FAILURE;
            }
        }

        return Result.CONTINUE;
    }


    private Iterable<Header> map(List<MessageHeader> headers) {
        return headers
                .stream()
                .map(header -> (Header) new RecordHeader(header.name(), header.content()))
                .toList();
    }
}
