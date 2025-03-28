package com.example.schemas.kafka;

import com.streaming.platform.UserActivityEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.serializer.FailedDeserializationInfo;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.function.Function;

@Component
public class Consumer {

    @Autowired
    private ConsumerHandler consumerHandler;

    @KafkaListener(id = "activity-topic-listener", topics = "user-activity")
    public void listen(ConsumerRecord<String, UserActivityEvent> record) {
        consumerHandler.accept(record.value());
    }

    public static class FailedDeserializationFunction implements Function<FailedDeserializationInfo, UserActivityEvent> {

        @Override
        public UserActivityEvent apply(FailedDeserializationInfo failedDeserializationInfo) {
            var msg = String.format("Failed to deserialize bytes %s. UTF-8 decoding %s",
                    Arrays.toString(failedDeserializationInfo.getData()),
                    new String(failedDeserializationInfo.getData(), StandardCharsets.UTF_8));
            System.out.println(msg);
            return null;
        }
    }
}
