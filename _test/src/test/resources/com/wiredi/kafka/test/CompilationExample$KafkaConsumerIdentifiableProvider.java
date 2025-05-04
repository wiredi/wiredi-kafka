package com.wiredi.kafka.test;

import com.google.auto.service.AutoService;
import com.wiredi.kafka.api.KafkaListener;
import com.wiredi.runtime.messaging.MessageConverter;
import com.wiredi.runtime.WireRepository;
import com.wiredi.runtime.domain.provider.IdentifiableProvider;
import com.wiredi.runtime.domain.provider.TypeIdentifier;
import com.wiredi.runtime.values.Value;
import jakarta.annotation.Generated;
import java.lang.Override;
import java.lang.String;
import java.util.List;
import org.jetbrains.annotations.NotNull;

@Generated(
        value = "com.wiredi.compiler.domain.entities.IdentifiableProviderEntity",
        date = "2023-01-01T00:00Z"
)
@AutoService({IdentifiableProvider.class})
public final class CompilationExample$KafkaConsumerIdentifiableProvider implements IdentifiableProvider<CompilationExample$KafkaConsumer> {
    private static final TypeIdentifier<CompilationExample$KafkaConsumer> PRIMARY_WIRE_TYPE = TypeIdentifier.of(CompilationExample$KafkaConsumer.class);

    private static final List<TypeIdentifier<?>> ADDITIONAL_WIRE_TYPES = List.of(
        TypeIdentifier.of(KafkaListener.class)
            .withGeneric(TypeIdentifier.of(String.class))
            .withGeneric(TypeIdentifier.of(String.class))
    );

    private final Value<CompilationExample$KafkaConsumer> instance = Value.empty();

    private CompilationExample$KafkaConsumer createInstance(final WireRepository wireRepository,
            final TypeIdentifier<CompilationExample$KafkaConsumer> concreteType) {
        // We will start by Fetching all 5 constructor parameters
        MessageConverter<String> variable0 = wireRepository.get(TypeIdentifier.of(MessageConverter.class)
                    .withGeneric(TypeIdentifier.of(String.class)));
        MessageConverter<String> variable1 = wireRepository.get(TypeIdentifier.of(MessageConverter.class)
                    .withGeneric(TypeIdentifier.of(String.class)));
        CompilationExample variable2 = wireRepository.get(TypeIdentifier.of(CompilationExample.class));
        KafkaProperties variable3 = wireRepository.get(TypeIdentifier.of(KafkaProperties.class));
        WireRepository variable4 = wireRepository.get(TypeIdentifier.of(WireRepository.class));
        CompilationExample$KafkaConsumer instance = new CompilationExample$KafkaConsumer(variable0,variable1,variable2,variable3,variable4);
        return instance;
    }

    @Override
    @NotNull
    public final List<TypeIdentifier<?>> additionalWireTypes() {
        return ADDITIONAL_WIRE_TYPES;
    }

    @Override
    public final synchronized CompilationExample$KafkaConsumer get(
            @NotNull final WireRepository wireRepository,
            @NotNull final TypeIdentifier<CompilationExample$KafkaConsumer> concreteType) {
        return instance.getOrSet(() -> createInstance(wireRepository, concreteType));
    }

    @Override
    @NotNull
    public final TypeIdentifier<CompilationExample$KafkaConsumer> type() {
        return PRIMARY_WIRE_TYPE;
    }
}
