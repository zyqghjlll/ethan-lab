package io.github.ethanzhang.factsplatform.application;

import io.github.ethanzhang.factsplatform.application.ports.EventMessage;
import io.github.ethanzhang.factsplatform.application.ports.EventPublisher;
import io.github.ethanzhang.factsplatform.domain.rawevent.RawEventService;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class IngestEventApplication {

    private final RawEventService rawEventService;
    private final EventPublisher eventPublisher;
    private final MeterRegistry meterRegistry;

    @SneakyThrows
    @Transactional
    public IngestRawEventReport ingest(IngestRawEventCmd cmd) {
        meterRegistry.counter("app_request_total").increment();

        if (cmd.getIngestTime() == null) {
            cmd.setIngestTime(LocalDateTime.now());
        }

        // persist raw event
        String id = rawEventService.ingest(cmd);
        Thread.sleep(50);
        meterRegistry.counter("app_db_insert_total").increment();

        // 模拟30%概率发布失败
        if (Math.random() < 0.3) {
            meterRegistry.counter("app_kafka_publish_failed_total").increment();
            throw new RuntimeException("Simulated publish failure");
        }

        // add to queue
        EventMessage message = new EventMessage(cmd.getIdentifyKey(), "", "test", cmd.getEventBody(), 1L);
        eventPublisher.publish(message);
        Thread.sleep(50);
        meterRegistry.counter("app_kafka_publish_total").increment();

        return new IngestRawEventReport(id);
    }
}
