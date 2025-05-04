package com.wiredi.kafka.consumer;

import com.wiredi.kafka.KafkaListenerRegistry;
import com.wiredi.kafka.consumer.container.KafkaListenerContainer;
import com.wiredi.kafka.consumer.exceptions.KafkaAssignmentException;
import com.wiredi.kafka.consumer.exceptions.KafkaSubscriptionException;
import com.wiredi.logging.Logging;

/**
 * A controller for handling errors and state changes
 * <p>
 * Any exception raised here will be forwarded to the {@link KafkaListenerContainer}, which then delegates it back
 * to the {@link KafkaListenerRegistry}, which then marks its state as dirty.
 */
public interface KafkaConsumerContainerController {

    Logging logger = Logging.getInstance(KafkaListenerContainer.class);

    /**
     * Handle an exception that was raised during polling.
     * <p>
     * This method will be invoked if an exception is received and the listener is still running.
     * <p>
     * The return value will determine if the KafkaConsumer should continue to poll.
     * If true is returned, the KafkaConsumer will continue to poll.
     * If false is returned, the KafkaConsumer will be shut down.
     *
     * @param throwable The encountered throwable
     * @param container The container that encountered the throwable.
     * @return the new running state of the container.
     */
    default boolean handleUnexpectedShutdown(Throwable throwable, KafkaListenerContainer container) {
        logger.error(() -> "KafkaConsumerContainer was shutdown unexpectedly", throwable);
        return false;
    }

    /**
     * Handles the shutdown of the provided {@code container}.
     * <p>
     * This method can return the new state of the processor.
     * If it is true, the container is considered still running, whilst false indicates that the container is stopped.
     * <p>
     * As the container is stopped before invocation of this method, implementations should return false if they are
     * not keeping the container awake.
     * <p>
     * Please note:
     * <p>
     * When this method is returning true, the shutdown that was requested will be prevented; the underlying
     * {@link KafkaListenerContainer} will stay alive.
     * If done improperly, this could mean that the container will stay alive, whilst the underlying
     * {@link com.wiredi.kafka.api.KafkaListener} is already shutdown and not working.
     * Then, the container will try to poll from the Listener, which then results in additional errors.
     * In most, if not all cases, false should be returned.
     *
     * @param container the container that was shutdown
     */
    default boolean handleExpectedShutdown(KafkaListenerContainer container) {
        logger.info(() -> "Shut down " + container);
        return false;
    }

    /**
     * Handle errors that occurred while the provided {@code container} is subscribed to topics.
     * <p>
     * This method does not allow you to return the new running state of the container.
     * Subscription errors cannot be recovered.
     *
     * @param throwable the exception that was encountered
     * @param container the container which encountered the throwable
     */
    default void handleSubscriptionError(Throwable throwable, KafkaListenerContainer container) {
        logger.error("Error while subscribing the container " + container + ": " + throwable.getMessage());
        throw new KafkaSubscriptionException("Error while subscribing the container " + container + ": " + throwable.getMessage(), throwable);
    }

    /**
     * Handle errors that happen during the first poll of the listeners.
     * <p>
     * The return value determines if the consumer should continue to listen.
     * If true, the listener will continue.
     * On false the consumer will shut down.
     *
     * @param throwable the exception that occurred
     * @param container the container that was tried to be assigned
     * @return true, if the setup should continue, otherwise false
     */
    default boolean handleAssignmentError(Throwable throwable, KafkaListenerContainer container) {
        logger.error(() -> "Error while assigning the container " + container);
        throw new KafkaAssignmentException("Error while assigning the container " + container + ": " + throwable.getMessage(), throwable);
    }

    /**
     * Handle any exception that is thrown whilst the underlying {@link org.apache.kafka.clients.consumer.KafkaConsumer}
     * is constructed.
     *
     * @param throwable the exception that was encountered.
     * @param container the container in which the throwable was raised.
     */
    default void handleConsumerConstructionError(Throwable throwable, KafkaListenerContainer container) {
        logger.error("Error during construction of a container " + container, throwable);
    }

    /**
     * Invoked when the {@link KafkaListenerContainer} is shut down.
     *
     * @param processor the container that is being shut down
     */
    default void handleShutdown(KafkaListenerContainer processor) {
        logger.info(() -> "Processor shut down");
    }
}
