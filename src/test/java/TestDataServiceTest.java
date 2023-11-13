import com.example.datageneratormicroservice.DataGeneratorMicroserviceApplication;
import com.example.datageneratormicroservice.model.Data;
import com.example.datageneratormicroservice.web.dto.DataTestOptionsDto;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Testcontainers
@TestConfiguration(proxyBeanMethods = false)
@SpringBootTest(classes = DataGeneratorMicroserviceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestDataServiceTest {

    @Container
    @ServiceConnection
    protected static final KafkaContainer KAFKA = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:latest")
    );

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
    }

    @BeforeAll
    static void setup() {
        KAFKA.addEnv("KAFKA_BROKER_ID", "1");
        KAFKA.addEnv("KAFKA_ADVERTISED_LISTENERS", KAFKA.getBootstrapServers());
        KAFKA.addEnv("KAFKA_LISTENERS", "LISTENER_PUBLIC://" + KAFKA.getContainerName() + ":29092,LISTENER_INTERNAL://" + KAFKA.getBootstrapServers());
        KAFKA.addEnv("KAFKA_LISTENER_SECURITY_PROTOCOL_MAP", "LISTENER_PUBLIC:PLAINTEXT,LISTENER_INTERNAL:PLAINTEXT");
        adminClient = AdminClient.create(Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers()));
    }

    @LocalServerPort
    private int port;

    private static AdminClient adminClient;

    @BeforeEach
    void setupRest() {
        RestAssured.baseURI = "http://localhost:" + port;
    }

    @Test
    @Order(1)
    public void testSendPower() {
        DataTestOptionsDto data = new DataTestOptionsDto();
        data.setDelayInSeconds(3);
        data.setMeasurementTypes(new Data.MeasurementType[]{Data.MeasurementType.POWER, Data.MeasurementType.VOLTAGE});

        Awaitility.await()
                .pollInterval(Duration.ofSeconds(3))
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    RestAssured.given()
                            .contentType(ContentType.JSON)
                            .body(data)
                            .post("/api/v1/data/test/send")
                            .then()
                            .statusCode(200);

                    ListTopicsResult ltr = adminClient.listTopics();
                    Assertions.assertTrue(ltr.names().get().contains("data-power"));
                    Assertions.assertTrue(ltr.names().get().contains("data-voltage"));
                });
    }

//    @Test
//    @Order(2)
//    void testConsumeMessagesFromKafka() {
//        Properties consumerProps = new Properties();
//        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers());
//        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "1");
//        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
//        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
//        ReceiverOptions<String, Object> options = ReceiverOptions.create(consumerProps);
//        ReceiverOptions<String, Object> subscribedOptions = options.subscription(List.of("data-power", "data-voltage"));
//
//        KafkaReceiver<String, Object> consumer = KafkaReceiver.create(subscribedOptions);
//        Awaitility.await()
//                .pollInterval(Duration.ofSeconds(15))
//                .atMost(45, TimeUnit.SECONDS)
//                .untilAsserted(() -> consumer.receive().subscribe(r ->
//                                Assertions.assertNotNull(r.value())
//                        )
//                );
//    }

}
