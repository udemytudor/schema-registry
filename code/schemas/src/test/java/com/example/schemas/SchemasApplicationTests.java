package com.example.schemas;

import com.example.schemas.kafka.ConsumerHandler;
import com.streaming.platform.UserActivityEvent;
import io.confluent.kafka.schemaregistry.avro.AvroSchema;
import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;

import static org.mockito.Mockito.*;

@SpringBootTest
@Testcontainers
@Import(KafkaTestConfiguration.class)
class SchemasApplicationTests {

	static Network NETWORK = Network.newNetwork();
	static String CONFLUENT_VERSION = "7.8.0";

	@Container
	@ServiceConnection
	static ConfluentKafkaContainer KAFKA = new ConfluentKafkaContainer(
			DockerImageName.parse("confluentinc/cp-kafka").withTag(CONFLUENT_VERSION)
	)
			.withNetwork(NETWORK)
			.withNetworkAliases("tc-kafka")
			.withListener("tc-kafka:19092");

	@Container
	static GenericContainer<?> REGISTRY = new GenericContainer<>(
			DockerImageName.parse("confluentinc/cp-schema-registry")
					.withTag(CONFLUENT_VERSION)
	)
			.withNetwork(NETWORK)
			.withNetworkAliases("schemaregistry")
			.withEnv("SCHEMA_REGISTRY_HOST_NAME", "schemaregistry")
			.withEnv("SCHEMA_REGISTRY_LISTENERS", "http://0.0.0.0:8085")
			.withExposedPorts(8085)
			.withEnv(
					"SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS",
					"PLAINTEXT://tc-kafka:19092"
			)
			.withEnv("SCHEMA_REGISTRY_KAFKASTORE_SECURITY_PROTOCOL", "PLAINTEXT")
			.waitingFor(Wait.forHttp("/subjects").forStatusCode(200));

	@DynamicPropertySource
	static void Properties(DynamicPropertyRegistry registry) {
		var registryUrl = String.format(
				"http://%s:%d", REGISTRY.getHost(),
				REGISTRY.getMappedPort(8085)
		);
		registry.add("spring.kafka.properties.schema.registry.url", () -> registryUrl);
	}

	@BeforeAll
	static void setup() throws RestClientException, IOException {
		var registryUrl = String.format(
				"http://%s:%d", REGISTRY.getHost(),
				REGISTRY.getMappedPort(8085)
		);
		var schemaRegistryClient = new CachedSchemaRegistryClient(registryUrl, 10);
		schemaRegistryClient.register("user-activity-value", new AvroSchema(
				UserActivityEvent.getClassSchema()
		));
	}

	@Autowired
	private KafkaTemplate<String, UserActivityEvent> kafkaTemplate;

	@MockitoBean
	private ConsumerHandler consumerHandler;

	@Autowired
	private KafkaTestConfiguration.StringProducer stringProducer;

	@Test
	void canDeserializeUserActivityEvent() {
		var event = UserActivityEvent.newBuilder().setUserId(12).setActivity("test").build();
		kafkaTemplate.send("user-activity", event);
		verify(consumerHandler, timeout(5000).times(1)).accept(event);
	}

	@Test
	void doesNotGetStuckOnDeserializationPoisonPills() {
		stringProducer.send("activity-topic", "poison pill");
		var event = UserActivityEvent.newBuilder().setUserId(12).setActivity("test").build();
		kafkaTemplate.send("user-activity", event);
		verify(consumerHandler, timeout(5000).times(1)).accept(event);
	}

}
