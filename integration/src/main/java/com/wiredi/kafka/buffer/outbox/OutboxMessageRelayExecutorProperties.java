package com.wiredi.kafka.buffer.outbox;

import com.wiredi.kafka.buffer.relay.MessageRelayExecutorProperties;

public record OutboxMessageRelayExecutorProperties(
        ThreadPool threadPool,
        Polling polling
) implements MessageRelayExecutorProperties {
}
