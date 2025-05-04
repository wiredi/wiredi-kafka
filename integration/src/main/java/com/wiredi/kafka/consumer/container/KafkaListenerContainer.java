package com.wiredi.kafka.consumer.container;

import com.wiredi.kafka.api.KafkaListener;
import com.wiredi.kafka.api.properties.ApacheKafkaConsumerProperties;
import com.wiredi.kafka.api.properties.KafkaListenerProperties;
import com.wiredi.kafka.api.topics.Topics;
import com.wiredi.kafka.consumer.KafkaConsumerContainerController;
import com.wiredi.kafka.consumer.KafkaListenerContext;
import com.wiredi.kafka.consumer.RecordInterceptor;
import com.wiredi.kafka.consumer.result.RecordResult;
import com.wiredi.kafka.error.KafkaErrorHandler;
import com.wiredi.logging.Logging;
import com.wiredi.runtime.lang.Preconditions;
import com.wiredi.runtime.time.Timed;
import com.wiredi.runtime.values.Value;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KafkaListenerContainer implements Runnable {

    private static final Logging logger = Logging.getInstance(KafkaListenerContainer.class);
    private final KafkaListenerContext context;
    private final Value<KafkaConsumer<byte[], byte[]>> consumerState = Value.empty();
    private final Value<Topics> topics = Value.neverNull(Topics.empty());
    private final KafkaListener kafkaListener;
    private final ApacheKafkaConsumerProperties consumerProperties;
    private final KafkaListenerProperties listenerProperties;
    private final KafkaListenerContainerState state = new KafkaListenerContainerState();
    private final List<RecordInterceptor> interceptors = new ArrayList<>();
    private final ConsumerRecordHandler consumerRecordHandler;

    public KafkaListenerContainer(
            @NotNull KafkaListener kafkaListener,
            @NotNull KafkaListenerContext context,
            @NotNull KafkaListenerProperties listenerProperties,
            @NotNull ApacheKafkaConsumerProperties consumerProperties,
            @NotNull ConsumerRecordHandler consumerRecordHandler
    ) {
        this.kafkaListener = kafkaListener;
        this.context = context;
        this.listenerProperties = listenerProperties;
        this.consumerProperties = consumerProperties;
        this.consumerRecordHandler = consumerRecordHandler;
    }

    public void addInterceptor(RecordInterceptor interceptor) {
        interceptors.add(interceptor);
    }

    public void seekTo(ConsumerRecord<?, ?> record) {
        getKafkaConsumer().seek(new TopicPartition(record.topic(), record.partition()), record.offset());
    }

    public @NotNull KafkaErrorHandler errorHandler() {
        return context.errorHandler();
    }

    private @NotNull KafkaConsumerContainerController shutdownHandler() {
        return context.shutdownHandler();
    }

    public KafkaListenerContext context() {
        return context;
    }

    public KafkaListener getKafkaListener() {
        return kafkaListener;
    }

    public KafkaConsumer<byte[], byte[]> getKafkaConsumer() {
        return consumerState.get();
    }

    public KafkaListenerContainer forTopics(Topics topics) {
        state.requireNotRunning();
        this.topics.set(topics);
        return this;
    }

    public Topics getTopics() {
        return topics.get();
    }

    public KafkaListenerContainerState state() {
        return this.state;
    }

    public void prepareStart() {
        state.set(ContainerState.STARTING);
    }

    @Override
    public void run() {
        state.requireNotRunning();
        state.set(ContainerState.STARTING);
        Preconditions.is(topics.isSet(), () -> "The container was not configured correctly. Set the topics before running the container.");
        Topics topics = this.topics.get();
        logger.info("Starting up a new KafkaConsumerContainer for " + topics);
        KafkaConsumer<byte[], byte[]> consumer;
        try {
            consumer = new KafkaConsumer<>(consumerProperties.toConsumerConfig().originals(), new ByteArrayDeserializer(), new ByteArrayDeserializer());
            consumerState.set(consumer);
        } catch (Throwable t) {
            shutdownHandler().handleConsumerConstructionError(t, this);
            throw t;
        }

        try (consumer) {
            state.set(startupContainer(topics, consumer));
            logger.info("KafkaConsumerContainer for topics " + topics + " successfully started");
            while (state.isRunning()) {
                try {
                    ConsumerRecords<byte[], byte[]> nextRecords = pollNext(consumer);
                    handleRemainingRecords(consumer, nextRecords);
                } catch (Throwable throwable) {
                    state.set(handleProcessingException(throwable));
                }
            }
        } finally {
            state.set(ContainerState.SHUTTING_DOWN);
            shutdownHandler().handleShutdown(this);
            consumerState.set(null);
            state.set(ContainerState.CLOSED);
        }
    }

    private ConsumerRecords<byte[], byte[]> pollNext(@NotNull Consumer<byte[], byte[]> consumer) {
        return Timed.of(() -> consumer.poll(listenerProperties.maxPollDuration()))
                .then(timedValue -> logger.debug(() -> "Polled " + timedValue.value().count() + " records in " + timedValue.time()))
                .value();
    }

    private ContainerState startupContainer(
            @NotNull Topics topics,
            @NotNull KafkaConsumer<byte[], byte[]> consumer
    ) {
        logger.debug(() -> "Starting up KafkaConsumerContainer for " + topics);
        ContainerState returnValue = initialSubscription(topics, consumer);
        if (listenerProperties.awaitAssignments()) {
            returnValue = waitUntilAssigned(consumer);
        }
        return returnValue;
    }

    private ContainerState initialSubscription(
            @NotNull Topics topics,
            @NotNull KafkaConsumer<byte[], byte[]> consumer
    ) {
        try {
            topics.subscribe(consumer, context.consumerRebalanceListener());
            return ContainerState.RUNNING;
        } catch (Throwable throwable) {
            shutdownHandler().handleSubscriptionError(throwable, this);
            return ContainerState.SHUTTING_DOWN;
        }
    }

    @SuppressWarnings("BusyWait") // This is on purpose.
    private ContainerState waitUntilAssigned(
            @NotNull KafkaConsumer<byte[], byte[]> consumer
    ) {
        logger.info("Waiting for the assignments of listener " + this);
        Set<TopicPartition> assignedPartitions = new HashSet<>();
        long delayMillis = listenerProperties.awaitAssignmentsPollDelay().toMillis();
        while (assignedPartitions.isEmpty()) {
            try {
                ConsumerRecords<byte[], byte[]> nextRecords = consumer.poll(Duration.ofMillis(100));
                assignedPartitions.addAll(consumer.assignment());
                logger.debug(() -> "Received assignments: " + consumer.assignment());
                handleRemainingRecords(consumer, nextRecords);
                if (assignedPartitions.isEmpty()) {
                    logger.debug(() -> "No assigned partitions. Waiting for " + delayMillis + " ms.");
                    Thread.sleep(delayMillis);
                }
            } catch (Throwable throwable) {
                shutdownHandler().handleAssignmentError(throwable, this);
            }
        }

        logger.info("Listener " + this + " is assigned to its topic partitions");
        return ContainerState.RUNNING;
    }

    private ContainerState handleProcessingException(
            @NotNull Throwable throwable
    ) {
        KafkaConsumerContainerController shutdownListener = shutdownHandler();

        boolean keepAlive;
        if (throwable instanceof WakeupException && state.isNotRunning()) {
            keepAlive = shutdownListener.handleExpectedShutdown(this);
        } else {
            keepAlive = shutdownListener.handleUnexpectedShutdown(throwable, this);
        }

        if (keepAlive) {
            return ContainerState.STARTING;
        } else {
            return ContainerState.SHUTTING_DOWN;
        }
    }

    public void stop() {
        consumerState.ifPresent(consumer -> {
            this.state.set(ContainerState.SHUTTING_DOWN);
            consumer.wakeup();
            if (listenerProperties.awaitShutdown()) {
                state.waitUntilStopped(listenerProperties.awaitShutdownDuration());
            }
        }).orElse(() -> {
            throw new IllegalStateException("The consumer is not started");
        });
    }

    private void handleRemainingRecords(
            @NotNull KafkaConsumer<byte[], byte[]> consumer,
            @NotNull ConsumerRecords<byte[], byte[]> records
    ) {
        if (records.isEmpty()) {
            return;
        }

        try {
            for (ConsumerRecord<byte[], byte[]> record : records) {
                ConsumerRecord<byte[], byte[]> adjustedRecord = beforeHandle(record);
                if (adjustedRecord != null) {
                    try {
                        RecordResult result = consumerRecordHandler.handle(adjustedRecord, this);
                        if (!result.apply(this, consumer, adjustedRecord, records)) {
                            return;
                        }
                        this.context.commitStrategy().afterEach(consumer, adjustedRecord);
                    } finally {
                        afterHandle(adjustedRecord);
                    }
                }
            }
        } finally {
            this.context.commitStrategy().afterAll(consumer, records);
        }
    }

    private ConsumerRecord<byte[], byte[]> beforeHandle(
            @NotNull ConsumerRecord<byte[], byte[]> record
    ) {
        ConsumerRecord<byte[], byte[]> adjustedRecord = record;
        for (RecordInterceptor interceptor : interceptors) {
            if (adjustedRecord != null) {
                adjustedRecord = interceptor.beforeHandle(adjustedRecord);
            }
        }

        return adjustedRecord;
    }

    public void handleFailure(@NotNull ConsumerRecord<byte[], byte[]> record, @Nullable Throwable throwable) {
        for (RecordInterceptor interceptor : interceptors) {
            interceptor.handleFailure(record, throwable);
        }
    }

    public void handleSuccess(@NotNull ConsumerRecord<byte[], byte[]> record) {
        for (RecordInterceptor interceptor : interceptors) {
            interceptor.handleSuccess(record);
        }
    }

    public void afterHandle(@NotNull ConsumerRecord<byte[], byte[]> record) {
        for (RecordInterceptor interceptor : interceptors) {
            interceptor.afterHandle(record);
        }
    }

    @Override
    public String toString() {
        List<String> entries = new ArrayList<>();
        topics.ifPresent(it -> entries.add(it.toString()));
        entries.add("State(" + state.value() + ")");

        return "KafkaListenerContainer(" + String.join(", ", entries) + ")";
    }
}
