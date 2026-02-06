package io.github.ethanzhang.factsplatform.application;

import lombok.Data;

@Data
public class IngestRawEventReport {
    private String eventId;

    public IngestRawEventReport(String eventId) {
        this.eventId = eventId;
    }
}
