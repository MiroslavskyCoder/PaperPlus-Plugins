package com.webx.clans.models;

import java.time.Instant;

public class ClanHistoryEvent {
    private String type;
    private String details;
    private Instant timestamp;
    // TODO: Add player/actor info, event types, etc.

    public ClanHistoryEvent(String type, String details, Instant timestamp) {
        this.type = type;
        this.details = details;
        this.timestamp = timestamp;
    }
    // Getters and setters...
}
