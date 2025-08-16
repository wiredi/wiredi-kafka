package com.wiredi.kafka;

import com.wiredi.kafka.publisher.KafkaPublisher;
import com.wiredi.runtime.WiredApplication;
import com.wiredi.runtime.WiredApplicationInstance;
import com.wiredi.tests.WiredTest;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

public class ErrorHandlingTest {

    private WiredApplicationInstance instance = WiredApplication.start();

    @Test
    public void errorsAreRetried() throws ExecutionException, InterruptedException {
        // Arrange
        ExampleListener exampleListener = instance.wireContainer().get(ExampleListener.class);
        KafkaPublisher publisher = instance.wireContainer().get(KafkaPublisher.class);
        exampleListener.prime(3);

        // Act
        publisher.publish("test", "key", "value").get();

        // Assert
        assertThat(exampleListener.await()).containsExactly(
                new ExampleListener.Handled("value", ExampleListener.ERROR),
                new ExampleListener.Handled("value", ExampleListener.ERROR),
                new ExampleListener.Handled("value", null)
        );
    }
}
