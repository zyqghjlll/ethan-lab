package io.github.ethanzhang.factsplatform.interfaces.api.dto;

import io.github.ethanzhang.factsplatform.application.IngestRawEventCmd;
import lombok.Data;

import java.time.Instant;

@Data
public class RawEventIngestDto {
    private String source;
    private String identifyKey;
    private String eventBody;
    private String zoneId;
    private Instant ingestTime;

    public IngestRawEventCmd toCommand() {
        return IngestRawEventCmd.builder()
                .source(source)
                .identifyKey(identifyKey)
                .eventBody(eventBody)
                .zoneId(zoneId)
                .ingestTime(ingestTime)
                .build();
    }
}
