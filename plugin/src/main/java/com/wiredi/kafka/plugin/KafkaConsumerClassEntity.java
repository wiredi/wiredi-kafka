package com.wiredi.kafka.plugin;

import com.squareup.javapoet.*;
import com.wiredi.annotations.Wire;
import com.wiredi.compiler.domain.AbstractClassEntity;
import com.wiredi.compiler.domain.Annotations;
import com.wiredi.compiler.domain.WireRepositories;
import com.wiredi.compiler.processor.lang.utils.TypeElements;
import com.wiredi.kafka.api.KafkaListener;
import com.wiredi.kafka.plugin.methods.HandleMethod;
import com.wiredi.runtime.WireContainer;
import org.jetbrains.annotations.NotNull;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

public class KafkaConsumerClassEntity extends AbstractClassEntity<KafkaConsumerClassEntity> {

    private final ExecutableElement method;
    private final WireRepositories wireRepositories;

    public KafkaConsumerClassEntity(
            @NotNull ExecutableElement source,
            @NotNull TypeMirror rootElement,
            @NotNull String className,
            Annotations annotations,
            WireRepositories wireRepositories
    ) {
        super(source, rootElement, className, annotations);
        this.method = source;
        this.wireRepositories = wireRepositories;
    }

    public KafkaConsumerClassEntity addConstructor(TypeElement kafkaListenerClass) {
        builder.addSuperinterface(ClassName.get(KafkaListener.class));

        addField(ClassName.get(kafkaListenerClass), "delegate", (it) -> it.addModifiers(Modifier.PRIVATE, Modifier.FINAL));
        addField(WireContainer.class, "wireContainer", (it) -> it.addModifiers(Modifier.PRIVATE, Modifier.FINAL));
        addAnnotation(Wire.class);

        builder.addMethod(MethodSpec.constructorBuilder()
                .addParameter(ParameterSpec.builder(ClassName.get(kafkaListenerClass), "delegate", Modifier.FINAL).addAnnotation(NotNull.class).build())
                .addParameter(ParameterSpec.builder(WireContainer.class, "wireContainer", Modifier.FINAL).addAnnotation(NotNull.class).build())
                .addCode(CodeBlock.builder()
                        .addStatement("this.delegate = delegate")
                        .addStatement("this.wireContainer = wireContainer")
                        .build())
                .build()
        );

        return this;
    }

    @Override
    protected TypeSpec.Builder createBuilder(TypeMirror type) {
        return TypeSpec.classBuilder(className())
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);
    }

    public KafkaConsumerClassEntity delegateHandleMethod(TypeElements typeElements, ExecutableElement method) {
        return addMethod(new HandleMethod(method, typeElements, wireRepositories));
    }
}
