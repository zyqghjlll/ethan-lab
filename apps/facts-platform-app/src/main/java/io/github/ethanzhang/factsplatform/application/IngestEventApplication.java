package io.github.ethanzhang.factsplatform.application;

import io.github.ethanzhang.factsplatform.application.ports.EventMessage;
import io.github.ethanzhang.factsplatform.application.ports.EventPublisher;
import io.github.ethanzhang.factsplatform.domain.rawevent.RawEventService;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class IngestEventApplication {

    private final RawEventService rawEventService;

    @Qualifier("kafkaEventRateFailedPublisher")
    private final EventPublisher eventPublisher;
    private final MeterRegistry meterRegistry;

    public IngestRawEventReport ingest(IngestRawEventCmd cmd) {

        if (cmd.getIngestTime() == null) {
            cmd.setIngestTime(LocalDateTime.now());
        }

        // persist raw event
        String id = "";
        try {
            id = rawEventService.ingest(cmd);
            meterRegistry.counter("app_ingress_total", "status","persisted").increment();
        } catch (Exception e) {
            meterRegistry.counter("app_ingress_total", "status","failed").increment();
        }

        // add to queue
        EventMessage message = new EventMessage(cmd.getIdentifyKey(), "", "test", cmd.getEventBody(), 1L);
        eventPublisher.publish(message);

        return new IngestRawEventReport(id);
    }
}
