package io.github.ethanzhang.factsplatform.application;

import io.github.ethanzhang.factsplatform.domain.rawevent.RawEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IngestEventApplication {

    private final RawEventService rawEventService;

    public IngestRawEventReport ingest(IngestRawEventCmd cmd) {

        // timeline
        // persist timeline

        // persist raw event
        String id = rawEventService.ingest(cmd);


        // add to queue

        return new IngestRawEventReport(id);
    }
}
