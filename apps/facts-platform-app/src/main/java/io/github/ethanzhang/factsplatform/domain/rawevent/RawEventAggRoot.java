package io.github.ethanzhang.factsplatform.domain.rawevent;

import common.core.AggregateRoot;
import lombok.Getter;

import java.time.Instant;

@Getter
public class RawEventAggRoot implements AggregateRoot<RawEventId> {
    private RawEventId rawEventId;
    private String source;
    private String identifyKey;
    private String zoneId;
    private Instant ingestTime;
    private String eventBody;

    public RawEventAggRoot(RawEventId rawEventId, String eventBody) {
        this.rawEventId = rawEventId;
        this.source = rawEventId.source();
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
