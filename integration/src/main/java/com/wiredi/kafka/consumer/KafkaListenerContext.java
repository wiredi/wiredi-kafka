package com.wiredi.kafka.consumer;

import com.wiredi.annotations.Wire;
import com.wiredi.annotations.scopes.Prototype;
import com.wiredi.kafka.consumer.ack.CommitStrategy;
import com.wiredi.kafka.error.KafkaErrorHandler;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.jetbrains.annotations.NotNull;

@Wire(
        proxy = false
)
@Prototype
public class KafkaListenerContext {

    @NotNull
    private KafkaErrorHandler errorHandler;
    @NotNull
    private ConsumerRebalanceListener consumerRebalanceListener;
    @NotNull
    private KafkaConsumerContainerController shutdownHandler;
    @NotNull
    private CommitStrategy commitStrategy;

    public KafkaListenerContext(
            @NotNull KafkaErrorHandler errorHandler,
            @NotNull ConsumerRebalanceListener consumerRebalanceListener,
            @NotNull KafkaConsumerContainerController shutdownHandler,
            @NotNull CommitStrategy commitStrategy
    ) {
        this.errorHandler = errorHandler;
        this.consumerRebalanceListener = consumerRebalanceListener;
        this.shutdownHandler = shutdownHandler;
        this.commitStrategy = commitStrategy;
    }

    public @NotNull KafkaErrorHandler errorHandler() {
        return errorHandler;
    }

    public KafkaListenerContext setErrorHandler(@NotNull KafkaErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
        return this;
    }

    public @NotNull ConsumerRebalanceListener consumerRebalanceListener() {
        return consumerRebalanceListener;
    }

    public KafkaListenerContext setConsumerRebalanceListener(@NotNull ConsumerRebalanceListener consumerRebalanceListener) {
        this.consumerRebalanceListener = consumerRebalanceListener;
        return this;
    }

    public @NotNull KafkaConsumerContainerController shutdownHandler() {
        return shutdownHandler;
    }

    public KafkaListenerContext setShutdownHandler(@NotNull KafkaConsumerContainerController shutdownHandler) {
        this.shutdownHandler = shutdownHandler;
        return this;
    }

    public @NotNull CommitStrategy commitStrategy() {
        return commitStrategy;
    }

    public KafkaListenerContext setCommitStrategy(@NotNull CommitStrategy commitStrategy) {
        this.commitStrategy = commitStrategy;
        return this;
    }
}
