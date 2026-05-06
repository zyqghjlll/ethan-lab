package io.github.ethanzhang.factsplatform.application;

import io.github.ethanzhang.factsplatform.application.ports.EventPublisher;
import io.github.ethanzhang.factsplatform.domain.rawevent.RawEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class IngestEventApplication {

    private final RawEventService rawEventService;
    private final EventPublisher eventPublisher;

    @Transactional
    public IngestRawEventReport ingest(IngestRawEventCmd cmd) {
        if (cmd.getIngestTime() == null) {
            cmd.setIngestTime(Instant.now());
        }

        // timeline
        // persist timeline

        // persist raw event
        String id = rawEventService.ingest(cmd);


        // add to queue
        eventPublisher.publish(id, cmd.getEventBody());

        return new IngestRawEventReport(id);
    }
}
