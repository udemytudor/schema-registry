package com.example.schemas.kafka;

import com.streaming.platform.UserActivityEvent;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class ConsumerHandler implements Consumer<UserActivityEvent> {
    @Override
    public void accept(UserActivityEvent userActivityEvent) {
        System.out.println("user id: " + userActivityEvent.getUserId());
        System.out.println("activity: " + userActivityEvent.getActivity());
    }
}
