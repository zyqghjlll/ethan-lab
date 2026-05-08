package io.github.ethanzhang.factsplatform.application;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class IngestRawEventCmd {
    private String source;
    private String eventType;
    private String identifyKey;
    private String eventBody;
    private LocalDateTime ingestTime;
    private String zoneId;
}
