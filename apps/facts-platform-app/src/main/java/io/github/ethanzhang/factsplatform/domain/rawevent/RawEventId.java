package io.github.ethanzhang.factsplatform.domain.rawevent;

import java.time.Instant;
import java.time.ZoneId;

public record RawEventId(String source, String identifyKey, String zoneId, Instant ingestTime) {

}
