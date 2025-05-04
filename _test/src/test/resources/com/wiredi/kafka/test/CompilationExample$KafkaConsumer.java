package com.wiredi.kafka.test;

import com.wiredi.annotations.Wire;
import com.wiredi.kafka.api.KafkaListener;
import com.wiredi.kafka.api.messaging.KafkaConsumerMessageDetails;
import com.wiredi.kafka.api.properties.KafkaListenerProperties;
import com.wiredi.runtime.messaging.Message;
import com.wiredi.runtime.messaging.MessageConverter;
import com.wiredi.kafka.api.topics.Topics;
import com.wiredi.runtime.WireRepository;
import com.wiredi.runtime.domain.provider.TypeIdentifier;
import com.wiredi.runtime.properties.TypeMapper;
import jakarta.annotation.Generated;
import java.lang.Override;
import java.lang.String;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.jetbrains.annotations.NotNull;

@Generated(
        value = "com.wiredi.kafka.plugin.KafkaConsumerClassEntity",
        date = "2023-01-01T00:00Z"
)
@Wire
public final class CompilationExample$KafkaConsumer implements KafkaListener<String, String> {
    private final CompilationExample delegate;

    private final MessageConverter<String> valueConverter;

    private final KafkaListenerProperties consumerProperties;

    private final WireRepository wireRepository;

    private final MessageConverter<String> keyConverter;

    CompilationExample$KafkaConsumer(@NotNull final MessageConverter<String> keyConverter,
            @NotNull final MessageConverter<String> valueConverter,
            @NotNull final CompilationExample delegate,
            @NotNull final KafkaProperties kafkaProperties,
            @NotNull final WireRepository wireRepository) {
        this.keyConverter = keyConverter;
        this.valueConverter = valueConverter;
        this.consumerProperties = kafkaProperties.consumerProperties();
        this.delegate = delegate;
        this.wireRepository = wireRepository;
    }

    @NotNull
    public final MessageConverter<String> valueConverter() {
        return valueConverter;
    }

    @NotNull
    public final Topics topics() {
        return Topics.of("test");
    }

    @Override
    public final void handle(@NotNull final String key,
                             @NotNull Message<String, KafkaConsumerMessageDetails> message,
                             @NotNull ConsumerRecord<byte[], byte[]> consumerRecord) {
        TypeMapper parameter0 = wireRepository.get(TypeIdentifier.of(TypeMapper.class));
        delegate.listen(record, parameter0);
    }

    @NotNull
    public final MessageConverter<String> keyConverter() {
        return keyConverter;
    }

    @NotNull
    public final KafkaListenerProperties properties() {
        return consumerProperties;
    }
}
