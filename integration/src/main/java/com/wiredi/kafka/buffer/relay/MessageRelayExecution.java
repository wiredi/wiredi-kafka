package com.wiredi.kafka.buffer.relay;

public interface MessageRelayExecution {

    Result execute();

    enum Result {
        CONTINUE,
        IDLE,
        FAILURE;
    }
}
