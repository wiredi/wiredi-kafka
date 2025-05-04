package com.wiredi.kafka.buffer;

import java.util.List;

public interface MessageBuffer {

    List<BufferedMessage<?>> poll(int batchSize);

    default boolean buffersMessages() {
        return true;
    }
}
