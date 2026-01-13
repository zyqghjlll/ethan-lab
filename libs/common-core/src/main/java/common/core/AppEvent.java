package common.core;

import java.time.LocalDateTime;

public abstract class AppEvent {
    private final String eventId;
    private final String operatorId;
    private final LocalDateTime timestamp;

    protected AppEvent(String operatorId) {
        this.eventId = java.util.UUID.randomUUID().toString();
        this.operatorId = operatorId;
        this.timestamp = LocalDateTime.now();
    }

    public String getEventId() {
        return eventId;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
