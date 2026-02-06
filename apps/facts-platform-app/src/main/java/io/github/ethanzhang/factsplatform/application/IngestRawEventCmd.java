package io.github.ethanzhang.factsplatform.application;


import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.time.ZoneId;

@Getter
@Builder
public class IngestRawEventCmd {
    private String source;
    private String identifyKey;
    private String eventBody;
    private Instant ingestTime;
    private String zoneId;
}
