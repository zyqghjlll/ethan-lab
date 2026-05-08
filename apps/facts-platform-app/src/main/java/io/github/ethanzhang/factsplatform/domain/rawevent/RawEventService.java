package io.github.ethanzhang.factsplatform.domain.rawevent;

import io.github.ethanzhang.factsplatform.application.IngestRawEventCmd;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RawEventService {
    private final RawEventRepository rawEventRepository;

    public String ingest(IngestRawEventCmd cmd) {
        RawEventId rawEventId = new RawEventId(
                cmd.getSource(),
                cmd.getEventType(),
                cmd.getIdentifyKey(),
                cmd.getZoneId(),
                cmd.getIngestTime()
        );
        RawEventAggRoot rawEvent = new RawEventAggRoot(rawEventId, cmd.getEventBody());
        long persistedId = rawEventRepository.save(rawEvent);
        return String.valueOf(persistedId);
    }
}
