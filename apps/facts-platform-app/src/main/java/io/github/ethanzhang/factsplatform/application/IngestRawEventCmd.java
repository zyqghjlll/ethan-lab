package io.github.ethanzhang.factsplatform.application;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
public class IngestRawEventCmd {
    private String source;
    private String identifyKey;
    private String eventBody;
    private Instant ingestTime;
    private String zoneId;
}
