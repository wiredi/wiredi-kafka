package com.wiredi.kafka;

import com.wiredi.annotations.Provider;
import com.wiredi.annotations.stereotypes.Configuration;
import com.wiredi.kafka.error.DefaultErrorHandler;
import com.wiredi.kafka.error.KafkaErrorHandler;

import static com.wiredi.kafka.consumer.result.RecordResult.retry;

@Configuration
public class KafkaConfiguration {

    @Provider
    public KafkaErrorHandler customErrorHandler() {
        return new DefaultErrorHandler() {
//            @Override
//            public RecordResult handle(ListenerEvent ListenerEvent, Throwable throwable) {
//                super.handle(ListenerEvent, throwable);
//
//                return retry(new RetryState(10L, Duration.ZERO))
//                        .or(skip());
//            }
        };
    }
}
