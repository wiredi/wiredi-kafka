package com.wiredi.kafka.executor;

public interface KafkaListenerExecutor {

    static BlockingKafkaListenerExecutor blocking() {
        return new BlockingKafkaListenerExecutor();
    }

    void submit(Runnable runnable);

}
