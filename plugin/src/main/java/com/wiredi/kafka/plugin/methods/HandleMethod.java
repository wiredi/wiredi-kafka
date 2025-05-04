package com.wiredi.kafka.plugin.methods;

import com.squareup.javapoet.*;
import com.wiredi.compiler.domain.ClassEntity;
import com.wiredi.compiler.domain.WireRepositories;
import com.wiredi.compiler.domain.entities.methods.StandaloneMethodFactory;
import com.wiredi.compiler.domain.injection.VariableContext;
import com.wiredi.compiler.processor.lang.utils.TypeElements;
import com.wiredi.kafka.api.KafkaConsumer.*;
import com.wiredi.kafka.api.KafkaListener;
import com.wiredi.kafka.api.ListenerEvent;
import com.wiredi.kafka.api.messaging.KafkaConsumerMessageDetails;
import com.wiredi.kafka.api.messaging.KafkaMessageDetails;
import com.wiredi.runtime.messaging.Message;
import com.wiredi.runtime.messaging.MessageDetails;
import com.wiredi.runtime.messaging.MessageHeaders;
import com.wiredi.runtime.messaging.annotations.Header;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.jetbrains.annotations.NotNull;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import static com.wiredi.compiler.domain.Annotations.isAnnotatedWith;

public class HandleMethod implements StandaloneMethodFactory {

    private final ExecutableElement method;
    private final TypeElements typeElements;
    private final WireRepositories wireRepositories;

    public HandleMethod(
            ExecutableElement method,
            TypeElements typeElements,
            WireRepositories wireRepositories
    ) {
        this.method = method;
        this.typeElements = typeElements;
        this.wireRepositories = wireRepositories;
    }

    @Override
    public String methodName() {
        return "handle";
    }

    @Override
    public void append(MethodSpec.Builder builder, ClassEntity<?> entity) {
        List<CodeBlock> parameters = new ArrayList<>();
        VariableContext variableContext = new VariableContext("parameter");
        CodeBlock.Builder methodBody = CodeBlock.builder();
        builder.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addAnnotation(Override.class);

        AtomicBoolean addedMessage = new AtomicBoolean(false);
        Supplier<String> getMessage = () -> {
            if (!addedMessage.get()) {
                methodBody.addStatement("$T<$T> message = event.message()", ClassName.get(Message.class), ClassName.get(KafkaConsumerMessageDetails.class));
                addedMessage.set(true);
            }
            return "message";
        };

        AtomicBoolean addedRecord = new AtomicBoolean(false);
        Supplier<String> getRecord = () -> {
            if (!addedRecord.get()) {
                methodBody.addStatement("$T<$T[], $T[]> record = event.details().record()", ConsumerRecord.class, byte.class, byte.class, getMessage.get());
                addedRecord.set(true);
            }
            return "record";
        };

        builder.addParameter(
                ParameterSpec.builder(ClassName.get(ListenerEvent.class),
                                "event"
                        )
                        .addAnnotation(NotNull.class)
                        .build()
        );

        method.getParameters().forEach(parameter -> {
            if (isAnnotatedWith(parameter, Payload.class)) {
                parameters.add(CodeBlock.of("event.value($T.class)", parameter.asType()));
            } else if (isAnnotatedWith(parameter, com.wiredi.runtime.messaging.annotations.Payload.class)) {
                parameters.add(CodeBlock.of("event.value($T.class)", parameter.asType()));
            } else if (isAnnotatedWith(parameter, Key.class)) {
                parameters.add(CodeBlock.of("event.key($T.class)", ClassName.get(parameter.asType())));
            } else if (isAnnotatedWith(parameter, Timestamp.class)) {
                parameters.add(CodeBlock.of("event.timestamp($T.class)", TypeName.get(parameter.asType())));
            } else if (isAnnotatedWith(parameter, Partition.class)) {
                parameters.add(CodeBlock.of("event.partition($T.class)", TypeName.get(parameter.asType())));
            } else if (isAnnotatedWith(parameter, Offset.class)) {
                parameters.add(CodeBlock.of("event.offset($T.class)", TypeName.get(parameter.asType())));
            } else if (isAnnotatedWith(parameter, Topic.class)) {
                parameters.add(CodeBlock.of("event.topic($T.class)", TypeName.get(parameter.asType())));
            } else if (isMessageDetails(parameter)) {
                parameters.add(CodeBlock.of("event.details()"));
            } else if (typeElements.iSOfType(parameter.asType(), ConsumerRecord.class)) {
                parameters.add(CodeBlock.of("event.details().record()"));
            } else if (typeElements.iSOfType(parameter.asType(), KafkaConsumer.class)) {
                parameters.add(CodeBlock.of("event.details().kafkaConsumer()"));
            } else if (typeElements.iSOfType(parameter.asType(), KafkaListener.class)) {
                parameters.add(CodeBlock.of("event.details().kafkaListener()"));
            } else if (isAnnotatedWith(parameter, Header.class)) {
                // TODO: Make me work
                throw new UnsupportedOperationException("Header values are not yet supported");
//                getAnnotation(parameter, Header.class).ifPresent(annotation -> {
//                    String name = annotation.name();
//                    if (name.isBlank()) {
//                        name = parameter.getSimpleName().toString();
//                    }
//
//                    parameters.add(CodeBlock.of("$L.getHeaders().lastValue($S)", getMessage.get(), name));
//                });
            } else if (typeElements.iSOfType(parameter.asType(), org.apache.kafka.common.header.Headers.class)) {
                parameters.add(CodeBlock.of("event.details().record().headers()"));
            } else if (typeElements.iSOfType(parameter.asType(), MessageHeaders.class)) {
                parameters.add(CodeBlock.of("$L.headers()", getMessage.get()));
            } else {
                String parameterName = variableContext.instantiateVariableIfRequired(parameter, wireRepositories, methodBody);
                parameters.add(CodeBlock.of(parameterName));
            }
        });

        builder.addCode(methodBody
                .addStatement("delegate.$L($L)", method.getSimpleName(), joinParameters(parameters))
                .build());
    }

    private CodeBlock joinParameters(List<CodeBlock> parameterNames) {
        if (parameterNames.isEmpty()) {
            return CodeBlock.builder().build();
        } else {
            return CodeBlock.builder()
                    .add("\n")
                    .indent()
                    .add(CodeBlock.join(parameterNames, ",\n"))
                    .unindent()
                    .add("\n")
                    .build();
        }
    }

    private boolean isMessageDetails(VariableElement parameter) {
        return typeElements.iSOfType(parameter.asType(), KafkaConsumerMessageDetails.class)
                || typeElements.iSOfType(parameter.asType(), KafkaMessageDetails.class)
                || typeElements.iSOfType(parameter.asType(), MessageDetails.class);
    }
}
