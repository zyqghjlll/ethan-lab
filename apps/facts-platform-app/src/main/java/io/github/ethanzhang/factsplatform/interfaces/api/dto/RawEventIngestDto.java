package io.github.ethanzhang.factsplatform.interfaces.api.dto;

import io.github.ethanzhang.factsplatform.application.IngestRawEventCmd;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.ZoneId;

@Data
public class RawEventIngestDto {
    private String source;
    private String identifyKey;
    private String eventBody;
    private String zoneId;

    public IngestRawEventCmd toCommand() {
        return IngestRawEventCmd.builder()
                .source(source)
                .identifyKey(identifyKey)
                .eventBody(eventBody)
                .zoneId(zoneId)
                .build();
    }
}
