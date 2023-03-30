package uk.co.data.sink.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.BatchListenerFailedException;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.converter.ConversionException;
import org.springframework.messaging.handler.annotation.Header;
import uk.co.data.sink.exception.RecordNotSavedException;
import uk.co.data.sink.model.User;
import uk.co.data.sink.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {

    private final UserRepository userRepository;

    @KafkaListener(topics = "${sink.topics.demo.name}")
    public void consumer(final List<User> records,
                         @Header(KafkaHeaders.CONVERSION_FAILURES) List<ConversionException> exceptions) {
        final List<User> deserialsedUsers = new ArrayList<>();
        try {
            IntStream.range(0, records.size()).forEach(index -> {
                var user = records.get(index);
                if (user == null && exceptions.get(index) != null) {
                    throw new BatchListenerFailedException("Record couldn't be deserialised", exceptions.get(index), index);
                }
                deserialsedUsers.add(user);
            });
            log.info("consumed messages {}", records.size());
            userRepository.saveAll(deserialsedUsers);
        } catch (Exception ex) {
            log.error("Exception while trying to save User" + ex);
            throw new RecordNotSavedException(ex.getMessage());
        }
    }
}
