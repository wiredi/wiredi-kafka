package com.wiredi.kafka.api.properties;

import com.wiredi.annotations.properties.PropertyBinding;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@PropertyBinding(prefix = "apache.kafka.consumer")
public record ApacheKafkaProducerProperties(
        String bootstrapServers
) {

    public ProducerConfig toProducerConfig() {
        Map<String, Object> props = new HashMap<>();

        addTo(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, List.of(bootstrapServers), props);
        addTo(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class, props);
        addTo(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class, props);

        return new ProducerConfig(props);
    }

    private <K, V> void addTo(@NotNull K key, @Nullable V value, @NotNull Map<K, V> map) {
        if (value != null) {
            map.put(key, value);
        }
    }

    private <K, V, S> void addTo(@NotNull K key, @Nullable V value, @NotNull Function<V, S> translation, @NotNull Map<K, S> map) {
        if (value != null) {
            map.put(key, translation.apply(value));
        }
    }

    public enum IsolationLevel {

        /**
         * Read everything including aborted transactions.
         */
        READ_UNCOMMITTED((byte) 0),

        /**
         * Read records from committed transactions, in addition to records not part of
         * transactions.
         */
        READ_COMMITTED((byte) 1);

        private final byte id;

        IsolationLevel(byte id) {
            this.id = id;
        }

        public byte id() {
            return this.id;
        }
    }
}
