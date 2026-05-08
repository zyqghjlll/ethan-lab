package io.github.ethanzhang.factsplatform.infrastructure.messaging;

import io.github.ethanzhang.factsplatform.application.ports.EventMessage;
import io.github.ethanzhang.factsplatform.application.ports.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaEventPublisher implements EventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${facts-platform.messaging.raw-event-topic:raw-event-ingested}")
    private String rawEventTopic;

    @Override
    public void publish(EventMessage eventMessage) {
        kafkaTemplate.send(rawEventTopic, eventMessage.eventId(), eventMessage);
    }
}
