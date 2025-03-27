package com.example.schemas;

import com.streaming.platform.UserActivityEvent;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootApplication
public class SchemasApplication {

	public static void main(String[] args) {
		SpringApplication.run(SchemasApplication.class, args);
	}

	@Bean
	public ApplicationRunner runner(KafkaTemplate<String, UserActivityEvent> template) {
		var event = UserActivityEvent.newBuilder().setUserId(123).setActivity("logged in").build();
		return args -> template.send("activity-topic", event);
	}
}
