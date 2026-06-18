package io.github.ethanzhang.factsplatform.interfaces.api;

import common.web.Message;
import io.github.ethanzhang.factsplatform.application.IngestEventApplication;
import io.github.ethanzhang.factsplatform.application.IngestRawEventReport;
import io.github.ethanzhang.factsplatform.interfaces.api.dto.RawEventIngestDto;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/event")
@RequiredArgsConstructor
public class IngestEventController {

    private final IngestEventApplication ingestEventApplication;
    private final MeterRegistry meterRegistry;

    @PostMapping("/ingest")
    public Message<IngestRawEventReport> ingestEvent(@RequestBody @Validated RawEventIngestDto event) {
        meterRegistry.counter("app_ingress_total", "status", "reached").increment();
        IngestRawEventReport report = ingestEventApplication.ingest(event.toCommand());
        return Message.succeed(report);
    }
}
