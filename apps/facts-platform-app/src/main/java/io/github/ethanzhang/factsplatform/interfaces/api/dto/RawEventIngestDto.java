package io.github.ethanzhang.factsplatform.interfaces.api.dto;

import io.github.ethanzhang.factsplatform.application.IngestRawEventCmd;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RawEventIngestDto {
    private String source;
    private String eventType;
    private String identifyKey;
    private String eventBody;
    private String zoneId;
    private LocalDateTime ingestTime;

    public IngestRawEventCmd toCommand() {
        return IngestRawEventCmd.builder()
                .source(source)
                .eventType(eventType)
                .identifyKey(identifyKey)
                .eventBody(eventBody)
                .zoneId(zoneId)
                .ingestTime(ingestTime)
                .build();
    }
}
