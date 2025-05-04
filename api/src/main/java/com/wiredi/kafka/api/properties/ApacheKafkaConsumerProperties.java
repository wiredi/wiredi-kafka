package com.wiredi.kafka.api.properties;

import com.wiredi.annotations.properties.Property;
import com.wiredi.annotations.properties.PropertyBinding;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

@PropertyBinding(prefix = "apache.kafka.consumer")
public record ApacheKafkaConsumerProperties(
        String bootstrapServers,
        @Property(defaultValue = "PT1S") @NotNull Duration fetchMaxWaitTime,
        @Nullable Duration autoCommitInterval,
        @Nullable String autoOffsetReset,
        @Nullable String clientId,
        @Nullable Boolean enableAutoCommit,
        @Nullable String groupId,
        @Nullable Duration heartbeatInterval,
        @Nullable Duration sessionTimeout,
        @Property(defaultValue = "READ_UNCOMMITTED") @NotNull IsolationLevel isolationLevel,
        @Nullable Integer maxPollRecords,
        @Nullable Integer fetchMinBytes
) {

    public ConsumerConfig toConsumerConfig() {
        Map<String, Object> props = new HashMap<>();

        addTo(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, autoCommitInterval, Duration::toMillis, props);
        addTo(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset, props);
        addTo(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, List.of(bootstrapServers), props);
        addTo(ConsumerConfig.CLIENT_ID_CONFIG, clientId, props);
        addTo(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit, props);
        addTo(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, fetchMaxWaitTime, Duration::toMillisPart, props);
        addTo(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, fetchMinBytes, props);
        addTo(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, sessionTimeout, Duration::toMillisPart, props);
        addTo(ConsumerConfig.GROUP_ID_CONFIG, groupId, props);
        addTo(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, heartbeatInterval, Duration::toMillis, props);
        addTo(ConsumerConfig.ISOLATION_LEVEL_CONFIG, isolationLevel, (it) -> it.name().toLowerCase(Locale.ROOT), props);
        addTo(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class, props);
        addTo(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class, props);
        addTo(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords, props);

        return new ConsumerConfig(props);
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
