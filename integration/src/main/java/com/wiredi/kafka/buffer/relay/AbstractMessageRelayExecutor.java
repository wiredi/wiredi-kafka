package com.wiredi.kafka.buffer.relay;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractMessageRelayExecutor<P extends MessageRelayExecutorProperties> implements MessageRelayExecutor {

    protected final P properties;
    private final ExecutorService executor;

    public AbstractMessageRelayExecutor(P properties) {
        this.properties = properties;
        if (properties.threadPool().pollingThread() <= 0) {
            throw new IllegalArgumentException("Invalid thread pool configuration. The executor must have at least one thread.");
        }

        if (properties.threadPool().virtual()) {
            this.executor = Executors.newVirtualThreadPerTaskExecutor();
        } else {
            this.executor = Executors.newFixedThreadPool(properties.threadPool().pollingThread(), new ThreadFactory() {

                private final AtomicInteger counter = new AtomicInteger(0);

                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r, "OutboxMessageRelayExecutor-" + counter.getAndIncrement());
                    thread.setPriority(properties.threadPool().threadPriority());
                    thread.setDaemon(properties.threadPool().deamon());
                    return thread;
                }
            });
        }
    }

    protected abstract MessageRelayExecution newRelayExecution();

    @Override
    public void start() {
        for (int i = 0; i < properties.threadPool().pollingThread(); i++) {
            executor.execute(new MessageRelayExecutionRunnable(newRelayExecution(), properties.polling()));
        }
    }

    @Override
    public void stop() {
        executor.shutdown();
    }
}
