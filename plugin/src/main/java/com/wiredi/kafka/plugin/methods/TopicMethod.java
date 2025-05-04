package com.wiredi.kafka.plugin.methods;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.wiredi.compiler.domain.Annotations;
import com.wiredi.compiler.domain.ClassEntity;
import com.wiredi.compiler.domain.entities.methods.StandaloneMethodFactory;
import com.wiredi.kafka.api.KafkaConsumer;
import com.wiredi.kafka.api.topics.Topics;
import org.jetbrains.annotations.NotNull;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import java.util.Arrays;
import java.util.List;

public class TopicMethod implements StandaloneMethodFactory {

    private final ExecutableElement method;

    public TopicMethod(ExecutableElement method) {
        this.method = method;
    }

    @Override
    public String methodName() {
        return "topics";
    }

    @Override
    public void append(MethodSpec.Builder builder, ClassEntity<?> entity) {
        Annotations.getAnnotation(method, KafkaConsumer.class)
                .map(it -> builder.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .addAnnotation(NotNull.class)
                        .returns(Topics.class)
                        .addCode(CodeBlock.builder().addStatement("return $T.of($L)", Topics.class, CodeBlock.join(resolveInEnvironment(it.topics()), ",")).build())
                );
    }

    private List<CodeBlock> resolveInEnvironment(String[] keys) {
        return Arrays.stream(keys)
                .map(it -> CodeBlock.of("wireRepository.environment().resolve($S)", it))
                .toList();
    }
}
