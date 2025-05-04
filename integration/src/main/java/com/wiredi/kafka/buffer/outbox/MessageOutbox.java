package com.wiredi.kafka.buffer.outbox;

import com.wiredi.kafka.buffer.BufferedMessage;
import com.wiredi.kafka.buffer.MessageBuffer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.List;

public interface MessageOutbox extends MessageBuffer {

    void accept(ProducerRecord<byte[], byte[]> record);
}
