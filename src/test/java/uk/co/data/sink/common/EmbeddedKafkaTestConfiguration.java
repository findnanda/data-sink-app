package uk.co.data.sink.common;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;

@ExtendWith(SpringExtension.class)
public class EmbeddedKafkaTestConfiguration {

    public static final String DEMO_TOPIC = "nord.demo";
    public static final String GENERIC_FAILURE_TOPIC = "nord.demo.generic.failures";
    public static final String CUSTOM_FAILURE_TOPIC = "nord.demo.custom.failures";

    protected KafkaTemplate<String, Object> kafkaTemplate;

    protected Consumer<String,Object> testConsumer;

    @Autowired
    protected EmbeddedKafkaBroker embeddedKafkaBroker;

    @BeforeEach
    public void setupKafka() {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafkaBroker);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        testConsumer = consumerTestFactory(consumerProps).createConsumer("testClient");

        Map<String, Object> producerProps = KafkaTestUtils.producerProps(embeddedKafkaBroker);
        producerProps.put(ProducerConfig.BATCH_SIZE_CONFIG, 2000);
        producerProps.put(ProducerConfig.LINGER_MS_CONFIG, 1000);
        producerProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");

        this.kafkaTemplate = new KafkaTemplate<>(createProducerFactor(producerProps));

        this.kafkaTemplate.setDefaultTopic(DEMO_TOPIC);

    }

    private <T> DefaultKafkaProducerFactory<String, T> createProducerFactor(Map<String, Object> producerProps) {
        return new DefaultKafkaProducerFactory<>(
                producerProps,
                new StringSerializer(),
                new JsonSerializer<>()
        );
    }

    private <T> DefaultKafkaConsumerFactory<String, Object> consumerTestFactory(Map<String, Object> consumerProps) {
        return new DefaultKafkaConsumerFactory<>(consumerProps);
    }
}
