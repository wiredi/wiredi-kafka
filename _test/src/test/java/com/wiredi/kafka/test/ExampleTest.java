package com.wiredi.kafka.test;

import com.wiredi.compiler.logger.LoggerCache;
import com.wiredi.compiler.processors.WireProcessor;
import com.wiredi.compiler.tests.files.FileManagerState;
import com.wiredi.compiler.tests.junit.CompilerSetup;
import com.wiredi.compiler.tests.junit.CompilerTest;
import com.wiredi.compiler.tests.result.Compilation;

import static com.wiredi.compiler.tests.Assertions.assertThat;

@CompilerSetup(processors = WireProcessor.class, rootFolder = "com.wiredi.kafka.test")
public class ExampleTest {

    static {
        LoggerCache.getDefaultLoggerProperties().logToSystemOut(false);
        System.setProperty("wire-di.generation-time", "2023-01-01T00:00Z");
    }

    @CompilerTest(classes = "CompilationExample")
    public void testCompile(Compilation compilation, FileManagerState state) {
        assertThat(compilation).wasSuccessful();
        assertThat(state)
                .containsGeneratedFile("com.wiredi.kafka.test.CompilationExampleIdentifiableProvider")
                .containsGeneratedFile("com.wiredi.kafka.test.CompilationExample$KafkaConsumer")
                .containsGeneratedFile("com.wiredi.kafka.test.CompilationExample$KafkaConsumerIdentifiableProvider");
    }

}
