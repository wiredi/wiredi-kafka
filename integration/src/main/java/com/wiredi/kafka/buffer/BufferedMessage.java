package com.wiredi.kafka.buffer;

import com.wiredi.runtime.messaging.Message;
import com.wiredi.runtime.messaging.MessageDetails;
import com.wiredi.runtime.messaging.MessageHeader;
import com.wiredi.runtime.messaging.messages.SimpleMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public record BufferedMessage<ID>(
        @NotNull ID id,
        @NotNull String topic,
        byte[] key,
        byte[] payload,

        @Nullable Integer partition,
        @Nullable Long offset,
        @Nullable Long timestamp,
        @NotNull List<@NotNull MessageHeader> headers,
        int errorCount,
        @Nullable String lastErrorMessage,
        @NotNull Instant createdAt,
        @NotNull Instant lastTryAt,

        @NotNull Runnable ack,
        @NotNull Consumer<Throwable> nack
) {

    public <D extends MessageDetails> Message<D> toMessage(Function<BufferedMessage<ID>, D> detailsMapper) {
        SimpleMessage.Builder<D> builder = Message.builder(payload())
                .withDetails(detailsMapper.apply(this));

        headers().forEach(builder::addHeader);

        return builder.build();
    }
}
