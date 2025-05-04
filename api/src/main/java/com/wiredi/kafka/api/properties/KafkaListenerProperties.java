package com.wiredi.kafka.api.properties;

import com.wiredi.annotations.properties.Property;
import com.wiredi.annotations.properties.PropertyBinding;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

@PropertyBinding(prefix = "wiredi.kafka.consumer")
public record KafkaListenerProperties(
        @Property(defaultValue = "true") boolean awaitAssignments,
        @Property(defaultValue = "PT0.5S") Duration awaitAssignmentsPollDelay,

        @Property(defaultValue = "true") boolean awaitShutdown,
        @Property(defaultValue = "AFTER_EACH_SYNC") CommitStrategy commitStrategy,
        @Property(defaultValue = "false") boolean eagerDeserialization,
        @Property(defaultValue = "PT1S") @NotNull Duration maxPollDuration,
        @Property(defaultValue = "PT10S") @NotNull Duration awaitShutdownDuration
) {
    public enum CommitStrategy {
        AFTER_EACH_ASYNC,
        AFTER_EACH_SYNC,
        AFTER_ALL_ASYNC,
        AFTER_ALL_SYNC;
    }
}
