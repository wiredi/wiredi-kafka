package com.wiredi.kafka.api.properties;

import com.wiredi.annotations.properties.Property;
import com.wiredi.annotations.properties.PropertyBinding;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;

import java.util.Map;

@PropertyBinding(prefix = "wiredi.kafka.producer")
public class KafkaProducerProperties {

    private final String bootstrapServer;
    private final boolean lazy;

    public KafkaProducerProperties(
            String bootstrapServer,
            @Property(defaultValue = "false") boolean lazy
    ) {
        this.bootstrapServer = bootstrapServer;
        this.lazy = lazy;
    }

    public ProducerConfig apacheConfig() {
        Map<String, Object> consumerConfig = Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class
        );

        return new ProducerConfig(consumerConfig);
    }

    public boolean isLazy() {
        return lazy;
    }
}
