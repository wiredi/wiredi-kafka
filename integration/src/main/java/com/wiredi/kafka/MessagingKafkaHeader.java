package com.wiredi.kafka;

import com.wiredi.runtime.messaging.MessageHeader;
import org.apache.kafka.common.header.Header;

public class MessagingKafkaHeader implements Header {

    private final MessageHeader messagingHeader;

    public MessagingKafkaHeader(MessageHeader messagingHeader) {
        this.messagingHeader = messagingHeader;
    }

    @Override
    public String key() {
        return messagingHeader.name();
    }

    @Override
    public byte[] value() {
        return messagingHeader.content();
    }
}
