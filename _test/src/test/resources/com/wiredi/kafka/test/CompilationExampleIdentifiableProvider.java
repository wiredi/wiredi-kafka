package com.wiredi.kafka.test;

import com.google.auto.service.AutoService;
import com.wiredi.runtime.WireRepository;
import com.wiredi.runtime.domain.provider.IdentifiableProvider;
import com.wiredi.runtime.domain.provider.TypeIdentifier;
import com.wiredi.runtime.values.Value;
import jakarta.annotation.Generated;
import java.lang.Override;
import org.jetbrains.annotations.NotNull;

@Generated(
        value = "com.wiredi.compiler.domain.entities.IdentifiableProviderEntity",
        date = "2023-01-01T00:00Z"
)
@AutoService({IdentifiableProvider.class})
public final class CompilationExampleIdentifiableProvider implements IdentifiableProvider<CompilationExample> {
    private static final TypeIdentifier<CompilationExample> PRIMARY_WIRE_TYPE = TypeIdentifier.of(CompilationExample.class);

    private final Value<CompilationExample> instance = Value.empty();

    private CompilationExample createInstance(final WireRepository wireRepository,
            final TypeIdentifier<CompilationExample> concreteType) {
        CompilationExample instance = new CompilationExample();
        return instance;
    }

    @Override
    public final synchronized CompilationExample get(@NotNull final WireRepository wireRepository,
            @NotNull final TypeIdentifier<CompilationExample> concreteType) {
        return instance.getOrSet(() -> createInstance(wireRepository, concreteType));
    }

    @Override
    @NotNull
    public final TypeIdentifier<CompilationExample> type() {
        return PRIMARY_WIRE_TYPE;
    }
}
