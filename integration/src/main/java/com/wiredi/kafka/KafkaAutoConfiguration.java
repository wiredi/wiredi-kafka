package com.wiredi.kafka;

import com.wiredi.annotations.Provider;
import com.wiredi.annotations.stereotypes.DefaultConfiguration;
import com.wiredi.kafka.api.properties.KafkaListenerProperties;
import com.wiredi.kafka.api.properties.KafkaProducerProperties;
import com.wiredi.kafka.consumer.DefaultKafkaConsumerContainerShutdownHandler;
import com.wiredi.kafka.consumer.KafkaConsumerContainerController;
import com.wiredi.kafka.consumer.NoOpConsumerRebalanceListener;
import com.wiredi.kafka.consumer.ack.AfterAllCommitStrategy;
import com.wiredi.kafka.consumer.ack.AfterEachCommitStrategy;
import com.wiredi.kafka.consumer.ack.CommitStrategy;
import com.wiredi.kafka.consumer.container.ConsumerRecordHandler;
import com.wiredi.kafka.consumer.container.MessagingKafkaListenerContainerHandler;
import com.wiredi.kafka.error.DefaultErrorHandler;
import com.wiredi.kafka.error.KafkaErrorHandler;
import com.wiredi.kafka.executor.KafkaListenerExecutor;
import com.wiredi.kafka.publisher.KafkaPublisher;
import com.wiredi.runtime.domain.conditional.builtin.ConditionalOnMissingBean;
import com.wiredi.runtime.messaging.MessagingEngine;
import com.wiredi.runtime.types.TypeMapper;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;

@DefaultConfiguration
public class KafkaAutoConfiguration {

    @Provider
    @ConditionalOnMissingBean(type = KafkaListenerExecutor.class)
    public KafkaListenerExecutor executorService() {
        return KafkaListenerExecutor.blocking();
    }

    @Provider
    @ConditionalOnMissingBean(type = KafkaPublisher.class)
    public KafkaPublisher publisher(
            KafkaProducerProperties kafkaProperties,
            MessagingEngine messagingEngine,
            TypeMapper typeMapper
    ) {
        return new KafkaPublisher(messagingEngine, typeMapper, kafkaProperties);
    }

    @Provider
    @ConditionalOnMissingBean(type = KafkaErrorHandler.class)
    public KafkaErrorHandler defaultErrorHandler() {
        return new DefaultErrorHandler();
    }

    @Provider
    @ConditionalOnMissingBean(type = ConsumerRecordHandler.class)
    public ConsumerRecordHandler defaultConsumerRecordHandler(TypeMapper typeMapper, MessagingEngine messagingEngine) {
        return new MessagingKafkaListenerContainerHandler(typeMapper, messagingEngine);
    }

    @Provider
    @ConditionalOnMissingBean(type = ConsumerRebalanceListener.class)
    public ConsumerRebalanceListener defaultConsumerRebalanceListener() {
        return new NoOpConsumerRebalanceListener();
    }

    @Provider
    @ConditionalOnMissingBean(type = KafkaConsumerContainerController.class)
    public KafkaConsumerContainerController defaultKafkaConsumerContainerController() {
        return DefaultKafkaConsumerContainerShutdownHandler.INSTANCE;
    }

    @Provider
    @ConditionalOnMissingBean(type = CommitStrategy.class)
    public CommitStrategy defaultCommitStrategy(KafkaListenerProperties properties) {
        return switch (properties.commitStrategy()) {
            case AFTER_ALL_SYNC -> new AfterAllCommitStrategy(false);
            case AFTER_ALL_ASYNC -> new AfterAllCommitStrategy(true);
            case AFTER_EACH_SYNC -> new AfterEachCommitStrategy(false);
            case AFTER_EACH_ASYNC -> new AfterEachCommitStrategy(true);
        };
    }
}
