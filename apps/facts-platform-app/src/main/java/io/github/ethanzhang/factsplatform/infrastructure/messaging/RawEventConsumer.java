package io.github.ethanzhang.factsplatform.infrastructure.messaging;

import io.github.ethanzhang.factsplatform.application.ports.EventMessage;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class RawEventConsumer {
    private static final Logger log = LoggerFactory.getLogger(RawEventConsumer.class);

    private final MeterRegistry meterRegistry;

    private final Set<String> processedIds = ConcurrentHashMap.newKeySet();

    @SneakyThrows
    @KafkaListener(
            topics = "${facts-platform.messaging.raw-event-topic:raw-event-ingested}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(EventMessage eventMessage) {
        log.info("[Consumer] Accepted message: {},{}", eventMessage.eventId(), eventMessage);

        // 检查是否重复消费
        String eventId = eventMessage.eventId();

        if (processedIds.contains(eventId)) {
            // 重复消费！
            meterRegistry.counter("app_kafka_duplicate_consume_total").increment();
            log.warn("[Consumer] Duplicate message detected: {}", eventId);
            return;
        }

        processedIds.add(eventId);

        // Thread.sleep(100);
        meterRegistry.counter("app_kafka_consume_total").increment();
        // add idempotency check here next
    }
}
