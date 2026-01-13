package io.github.ethanzhang.factsplatform.interfaces.api;

import common.web.Message;
import io.github.ethanzhang.factsplatform.application.IngestEventApplication;
import io.github.ethanzhang.factsplatform.application.IngestRawEventReport;
import io.github.ethanzhang.factsplatform.interfaces.api.dto.RawEventIngestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/event")
@RequiredArgsConstructor
public class IngestEventController {

    private final IngestEventApplication ingestEventApplication;

    @PostMapping("ingest")
    public Message<IngestRawEventReport> ingestEvent(@RequestBody RawEventIngestDto event) {
        IngestRawEventReport report = ingestEventApplication.ingest(event.toCommand());
        return Message.succeed(report);
    }
}
