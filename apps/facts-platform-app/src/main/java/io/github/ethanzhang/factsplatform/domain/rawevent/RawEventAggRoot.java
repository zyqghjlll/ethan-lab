package io.github.ethanzhang.factsplatform.domain.rawevent;

import common.core.AggregateRoot;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RawEventAggRoot implements AggregateRoot<RawEventId> {
    private final RawEventId rawEventId;
    private final String source;
    private final String eventType;
    private final String identifyKey;
    private final String zoneId;
    private final LocalDateTime ingestTime;
    private final String eventBody;

    public RawEventAggRoot(RawEventId rawEventId, String eventBody) {
        this.rawEventId = rawEventId;
        this.source = rawEventId.source();
        this.eventType = rawEventId.eventType();
        this.identifyKey = rawEventId.identifyKey();
        this.zoneId = rawEventId.zoneId();
        this.ingestTime = rawEventId.ingestTime();
        this.eventBody = eventBody;
    }

    @Override
    public RawEventId getId() {
        return rawEventId;
    }
}
