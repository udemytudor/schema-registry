package com.example.schemas;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;

@TestConfiguration
public class KafkaTestConfiguration {

    interface StringProducer {
        void send(String topic, String message);
    }

    @Bean
    StringProducer stringKafkaTemplate(ProducerFactory<?, ?> producerFactory) {
        var props = new HashMap<>(producerFactory.getConfigurationProperties());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        var template = new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(props));

        return template::send;
    }
}
