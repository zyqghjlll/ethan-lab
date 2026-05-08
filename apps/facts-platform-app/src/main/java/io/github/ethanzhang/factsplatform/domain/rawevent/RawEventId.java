package io.github.ethanzhang.factsplatform.domain.rawevent;

import java.time.LocalDateTime;

public record RawEventId(String source, String eventType, String identifyKey, String zoneId, LocalDateTime ingestTime) {

}
