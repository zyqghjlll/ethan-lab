package io.github.ethanzhang.factsplatform.infrastructure.messaging;

import io.github.ethanzhang.factsplatform.application.ports.EventMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class RawEventConsumer {
    private static final Logger log = LoggerFactory.getLogger(RawEventConsumer.class);

    @KafkaListener(
            topics = "${facts-platform.messaging.raw-event-topic:raw-event-ingested}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(EventMessage eventMessage) {
        log.info("[Consumer] Accepted message: {},{}", eventMessage.eventId(), eventMessage);
        // add idempotency check here next
    }
}
