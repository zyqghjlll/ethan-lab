package io.github.ethanzhang.factsplatform.infrastructure.messaging;

import io.github.ethanzhang.factsplatform.application.ports.EventMessage;
import io.github.ethanzhang.factsplatform.application.ports.EventPublisher;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaEventRateFailedPublisher implements EventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${facts-platform.messaging.raw-event-topic:raw-event-ingested}")
    private String rawEventTopic;
    private static final double MOCK_ERROR_RATE = 0.3;
    private final MeterRegistry meterRegistry;

    @SneakyThrows
    @Override
    public void publish(EventMessage eventMessage) {

        if (Math.random() < MOCK_ERROR_RATE) {
            meterRegistry.counter("app_publish_total", "status", "unsent").increment();
            throw new RuntimeException("Simulated publish failure");
        }

        try {
            kafkaTemplate.send(rawEventTopic, eventMessage.eventId(), eventMessage);
            meterRegistry.counter("app_publish_total", "status", "sent").increment();
        } catch (Exception e) {
            meterRegistry.counter("app_publish_total", "status", "unsent").increment();
        }

        // Mock spent time
        Thread.sleep(50);
    }
}
