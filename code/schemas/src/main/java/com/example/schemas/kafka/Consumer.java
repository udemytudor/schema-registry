package com.example.schemas.kafka;

import com.streaming.platform.UserActivityEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class Consumer {


    @KafkaListener(id = "activity-topic-listener", topics = "activity-topic")
    public void listen(ConsumerRecord<String, UserActivityEvent> record) {
        System.out.println("user id: " + record.value().getUserId());
        System.out.println("activity: " + record.value().getActivity());
    }
}
