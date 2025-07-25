package org.jnjeaaaat.global.kafka.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.jnjeaaaat.exception.NotificationException;
import org.jnjeaaaat.global.event.NotificationEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static org.jnjeaaaat.global.exception.ErrorCode.INTERNAL_ERROR;

@Component
@RequiredArgsConstructor
public class EventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void send(String topic, NotificationEvent<?> payload) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(payload);
            kafkaTemplate.send(topic, jsonMessage);
        } catch (Exception e) {
            throw new NotificationException(INTERNAL_ERROR, e.getMessage());
        }
    }
}
