package com.wiredi.kafka;

import com.wiredi.annotations.Wire;
import com.wiredi.kafka.api.KafkaListener;
import com.wiredi.kafka.api.properties.KafkaProperties;
import com.wiredi.kafka.consumer.KafkaConsumerContainerController;
import com.wiredi.kafka.consumer.KafkaListenerContext;
import com.wiredi.kafka.consumer.container.ConsumerRecordHandler;
import com.wiredi.kafka.consumer.container.KafkaListenerContainer;
import com.wiredi.kafka.executor.KafkaListenerExecutor;
import com.wiredi.logging.Logging;
import com.wiredi.runtime.WireContainer;
import com.wiredi.runtime.async.AsyncLoader;
import com.wiredi.runtime.async.StateFull;
import com.wiredi.runtime.async.state.State;
import com.wiredi.runtime.domain.Eager;
import com.wiredi.runtime.domain.provider.TypeIdentifier;
import com.wiredi.runtime.time.Timed;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Wire(proxy = false)
public class KafkaListenerRegistry implements Eager, StateFull<List<KafkaListenerContainer>> {

    private static final Logging logger = Logging.getInstance(KafkaListenerRegistry.class);

    private final KafkaConsumerState state = new KafkaConsumerState();
    private final List<KafkaListener> listeners = new ArrayList<>();
    private final Map<KafkaListenerContainer, KafkaListener> processors = new HashMap<>();
    private final MaintenanceShutdownHandler shutdownHandler = new MaintenanceShutdownHandler();
    @NotNull
    private final KafkaListenerExecutor executorService;
    @NotNull
    private final KafkaListenerContext kafkaListenerContext;
    @NotNull
    private final KafkaProperties kafkaProperties;
    @NotNull
    private final ConsumerRecordHandler consumerRecordHandler;

    public KafkaListenerRegistry(
            @NotNull KafkaListenerExecutor executorService,
            @NotNull KafkaListenerContext kafkaListenerContext,
            @NotNull KafkaProperties kafkaProperties,
            @NotNull ConsumerRecordHandler consumerRecordHandler
    ) {
        this.executorService = executorService;
        this.kafkaListenerContext = kafkaListenerContext;
        this.kafkaProperties = kafkaProperties;
        this.consumerRecordHandler = consumerRecordHandler;
    }

    public KafkaListenerContainer register(KafkaListener kafkaListener) {
        KafkaListenerContainer container = new KafkaListenerContainer(
                kafkaListener,
                kafkaListenerContext,
                Objects.requireNonNullElse(kafkaListener.listenerProperties(), kafkaProperties.listenerProperties()),
                Objects.requireNonNullElse(kafkaListener.consumerProperties(), kafkaProperties.consumerProperties()),
                consumerRecordHandler
        );

        listeners.add(kafkaListener);
        container.forTopics(kafkaListener.topics());
        container.context().setShutdownHandler(shutdownHandler);
        container.prepareStart();
        executorService.submit(new KafkaListenerContainerRunner(container));
        container.state().waitUntilRunning();
        processors.put(container, kafkaListener);
        state.add(container);
        return container;
    }

    public List<KafkaListener> getListenersForTopic(String topic) {
        return listeners.stream()
                .filter(it -> it.topics().matches(topic))
                .toList();
    }

    @Override
    public void setup(WireContainer wireRepository) {
        AsyncLoader.run(() -> {
            try {
                Timed.of(() -> {
                    List<KafkaListener> registeredListeners = wireRepository.getAll(TypeIdentifier.of(KafkaListener.class));
                    if (!registeredListeners.isEmpty()) {
                        logger.info(() -> "Registering " + registeredListeners.size() + " KafkaListeners");
                        registeredListeners.parallelStream()
                                .forEach(this::register);
                        state.complete();
                    }
                    return registeredListeners.size();
                }).then(it -> logger.info(() -> "Registered " + it.value() + " listeners in " + it.time()));
            } catch (Throwable t) {
                state.markAsDirty(t);
            }
        });
    }

    public void shutdown() {
        if (processors.isEmpty()) {
            return;
        }
        logger.info(() -> "Shutting down " + processors.size() + " consumers");
        Timed.of(() -> {
            HashMap<KafkaListenerContainer, KafkaListener> copy = new HashMap<>(processors);
            copy.entrySet()
                    .parallelStream()
                    .forEach(entry -> {
                        try {
                            entry.getKey().stop();
                        } catch (Throwable t) {
                            logger.error(() -> "Error while stopping container " + entry.getKey(), t);
                        }
                    });
            copy.clear();
            processors.clear();
        }).then(it -> logger.info(() -> "Kafka shut down in " + it));
        state.get().clear();
    }

    @Override
    public void dismantleState() {
        shutdown();
    }

    @Override
    public @NotNull State<List<KafkaListenerContainer>> getState() {
        return state;
    }

    class MaintenanceShutdownHandler implements KafkaConsumerContainerController {
        @Override
        public void handleShutdown(KafkaListenerContainer processor) {
            processors.remove(processor);
        }
    }

    class KafkaListenerContainerRunner implements Runnable {

        private final KafkaListenerContainer container;

        KafkaListenerContainerRunner(KafkaListenerContainer container) {
            this.container = container;
        }

        @Override
        public void run() {
            try {
                container.run();
            } catch (Throwable throwable) {
                state.markAsDirty(throwable);
            }
        }
    }
}
