package com.wiredi.kafka.executor;

import java.util.ArrayList;
import java.util.List;

public class BlockingKafkaListenerExecutor implements KafkaListenerExecutor {

    private final List<Thread> threadList = new ArrayList<>();

    @Override
    public void submit(Runnable runnable) {
        String name;
        synchronized (threadList) {
            name = "kafka-listener-thread-" + threadList.size();
        }
        Thread thread = new Thread(() -> {
            Thread reference = Thread.currentThread();
            try {
                runnable.run();
            } finally {
                synchronized (threadList) {
                    threadList.remove(reference);
                }
            }
        });
        thread.setPriority(7);
        thread.setName(name);
        threadList.add(thread);
        thread.start();
    }
}
