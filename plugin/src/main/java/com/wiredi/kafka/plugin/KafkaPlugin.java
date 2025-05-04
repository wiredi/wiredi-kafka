package com.wiredi.kafka.plugin;

import com.google.auto.service.AutoService;
import com.wiredi.compiler.domain.WireRepositories;
import com.wiredi.compiler.domain.entities.IdentifiableProviderEntity;
import com.wiredi.compiler.logger.Logger;
import com.wiredi.compiler.processor.lang.utils.TypeElements;
import com.wiredi.compiler.processor.plugins.CompilerEntityPlugin;
import com.wiredi.compiler.repository.CompilerRepository;
import com.wiredi.kafka.api.KafkaConsumer;
import com.wiredi.kafka.plugin.methods.TopicMethod;
import jakarta.inject.Inject;
import org.jetbrains.annotations.NotNull;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.util.List;
import java.util.UUID;

import static com.wiredi.compiler.domain.Annotations.isAnnotatedWith;

@AutoService(CompilerEntityPlugin.class)
public class KafkaPlugin implements CompilerEntityPlugin {

    private static final Logger logger = Logger.get(KafkaPlugin.class);

    @Inject
    private CompilerRepository compilerRepository;

    @Inject
    private TypeElements typeElements;

    @Inject
    private Elements elements;

    @Inject
    private WireRepositories wireRepositories;

    @Override
    public void handle(@NotNull IdentifiableProviderEntity entity) {
        Element source = entity.getSource();
        logger.info(source, () -> "Analyzing " + source.getSimpleName() + " for KafkaConsumer annotations");
        if (source.getKind() == ElementKind.CLASS && source instanceof TypeElement typeElement) {
            List<KafkaConsumerClassEntity> kafkaConsumers = typeElements.methodsOf(typeElement).stream()
                    .filter(it -> isAnnotatedWith(it, KafkaConsumer.class))
                    .map(it -> newEntity(it, typeElement)).toList();

            if (kafkaConsumers.isEmpty()) {
                return;
            }

            logger.info(() -> "Detected " + kafkaConsumers.size() + " consumers on " + typeElement);
            kafkaConsumers.forEach(compilerRepository::save);
        } else if (source.getKind() == ElementKind.METHOD
                && source instanceof ExecutableElement method
                && isAnnotatedWith(method, KafkaConsumer.class)
        ) {
            TypeElement parent = typeElements.outerMostTypeElementOf(method);
            KafkaConsumerClassEntity consumerEntity = newEntity(method, parent);
            compilerRepository.save(consumerEntity);
        }
    }

    private KafkaConsumerClassEntity newEntity(ExecutableElement method, TypeElement parent) {
        logger.info(() -> "Creating KafkaConsumer for " + method);
        return new KafkaConsumerClassEntity(
                method,
                parent.asType(),
                nameOf(method, parent),
                wireRepositories
        ).delegateHandleMethod(typeElements, method)
                .addConstructor(parent)
                .addMethod(new TopicMethod(method));
    }

    private String nameOf(ExecutableElement method, TypeElement declaringClass) {
        List<? extends ExecutableElement> allMethods = typeElements.methodsOf(declaringClass);
        List<? extends ExecutableElement> kafkaConsumerMethods = allMethods.stream()
                .filter(it -> isAnnotatedWith(it, KafkaConsumer.class))
                .toList();

        if (kafkaConsumerMethods.size() == 1) {
            return declaringClass.getSimpleName() + "$KafkaConsumer";
        }

        String syntheticName = declaringClass.getSimpleName() + "$KafkaConsumer$" + method.getSimpleName();
        logger.info(() -> "Synthetic name for " + method + " is " + syntheticName);
        if (kafkaConsumerMethods.stream().filter(it -> it.getSimpleName().toString().equals(method.getSimpleName().toString())).count() == 1) {
            return syntheticName;
        } else {
            return syntheticName + UUID.randomUUID();
        }
    }
}
