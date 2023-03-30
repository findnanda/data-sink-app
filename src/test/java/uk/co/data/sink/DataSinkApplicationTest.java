package uk.co.data.sink;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import uk.co.data.sink.common.CassandraContainerTestConfiguration;
import uk.co.data.sink.model.User;
import uk.co.data.sink.repository.UserRepository;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import static uk.co.data.sink.common.EmbeddedKafkaTestConfiguration.DEMO_TOPIC;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.NONE, classes = DataSinkApplication.class)
@EmbeddedKafka(partitions = 5, topics = {DEMO_TOPIC}, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092", "offsets.topic.replication.factor=1"})
public class DataSinkApplicationTest extends CassandraContainerTestConfiguration {

    @Autowired
    UserRepository userRepository;

    @Test
    public void testUserRecordSuccessfullyStored() throws Exception {
        final User user = User.builder()
                .userId("1234")
                .city("London")
                .firstname("Fname1")
                .lastname("Lname1")
                .build();

        kafkaTemplate.sendDefault(user.getUserId(), user);
        log.info("sent user record");

        Thread.sleep(1000);

        User result = userRepository.findByUserId("1234");
        Assertions.assertEquals(result.getUserId(), "1234");
    }

    @Test
    public void testMessageVolumes() {
        var messages = IntStream.range(0, 100000).mapToObj(i -> buildUser())
                .parallel().collect(Collectors.toList());
        log.info("records created");
        messages.stream()
                .parallel().forEach(u -> kafkaTemplate.sendDefault(u.getUserId(), u));
        log.info("sent user records");

        await().atMost(180, SECONDS).pollInterval(Duration.ofSeconds(10)).until(() -> {
            var size = userRepository.findAll().size();
            log.info("result: {}", size);
            return userRepository.findAll().size() == 100000;

        });
        Assertions.assertEquals(100000, userRepository.findAll().size());
    }

    private User buildUser() {
        return User.builder()
                .userId(UUID.randomUUID().toString())
                .city(RandomStringUtils.randomAlphabetic(4))
                .firstname(RandomStringUtils.randomAlphabetic(6))
                .lastname(RandomStringUtils.randomAlphabetic(7))
                .build();
    }
}
