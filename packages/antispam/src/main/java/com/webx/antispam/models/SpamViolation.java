package com.webx.antispam.models;

import java.util.UUID;

public class SpamViolation {
    private final UUID playerUuid;
    private final long timestamp;
    private final String message;
    
    public SpamViolation(UUID playerUuid, String message) {
        this.playerUuid = playerUuid;
        this.timestamp = System.currentTimeMillis();
        this.message = message;
    }
    
    public UUID getPlayerUuid() { return playerUuid; }
    public long getTimestamp() { return timestamp; }
    public String getMessage() { return message; }
}
