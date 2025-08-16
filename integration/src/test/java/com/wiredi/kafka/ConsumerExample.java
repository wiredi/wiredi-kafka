package com.wiredi.kafka;

import com.wiredi.runtime.WireContainer;
import com.wiredi.runtime.WiredApplication;
import com.wiredi.runtime.WiredApplicationInstance;
import com.wiredi.runtime.time.Timed;
import org.junit.jupiter.api.Test;

public class ConsumerExample {

    public static void main(String[] args) {
        WiredApplicationInstance application = WiredApplication.start();
        WireContainer repository = application.wireContainer();
        TestService testService = repository.get(TestService.class);
        Timed timed = testService.runTestScenario();
        application.shutdown();
        System.out.println("Test scenario executed in " + timed);
    }

//    public static void main(String[] args) {
//        WireRepository repository = WireRepository.open();
//        wireRepository.load();
//        wireRepository.get(TypeIdentifier.of(KafkaPublisher.class).withGeneric(String.class).withGeneric(String.class));
//        ExampleListener exampleListener = wireRepository.get(ExampleListener.class);
//        KafkaPublisher<String, String> publisher = wireRepository.get(TypeIdentifier.of(KafkaPublisher.class).withGeneric(String.class).withGeneric(String.class));
//        exampleListener.prime();
//        publisher.publish("test", "key", "value");
//
//        System.out.println("Awaiting until consumer receives message");
//        exampleListener.await();
//        System.out.println("DONE");
//    }
//    public static void main(String[] args) throws ExecutionException, InterruptedException {
//        KafkaConsumerProcessor<String, String> kafkaConsumerProcessor = new KafkaConsumerProcessor<>(
//                ConsumerExample::handle,
//                new KafkaConsumerProperties(BOOTSTRAP_SERVER, UUID.randomUUID().toString()),
//                StringMessageConverter.INSTANCE,
//                StringMessageConverter.INSTANCE
//        ).forTopics(List.of("test"));
//        kafkaConsumerProcessor.prepareStart();
//        new Thread(kafkaConsumerProcessor).start();
//        kafkaConsumerProcessor.awaitStart();
//
//        KafkaPublisher<String, String> publisher = new KafkaPublisher<>(
//                new KafkaProducerProperties(BOOTSTRAP_SERVER),
//                StringMessageConverter.INSTANCE,
//                StringMessageConverter.INSTANCE
//        );
//
//        Thread.sleep(5000);
//        System.out.println("Sending message!");
//        publisher.publish("test", "key", "value");
//        RECEIVED_BARRIER.traverse();
//        System.out.println("Stopping processor");
//        kafkaConsumerProcessor.prepareShutdown();
//        kafkaConsumerProcessor.stop();
//        kafkaConsumerProcessor.awaitShutdown();
//        System.out.println("COMPLETE");
//    }
//
//    private static void handle(ConsumerRecord<String, String> record) {
//        System.out.println("Received " + record.key() + " => " + record.value());
//        RECEIVED_BARRIER.open();
//    }
}
