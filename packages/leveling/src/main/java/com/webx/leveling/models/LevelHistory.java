package com.webx.leveling.models;

import java.time.LocalDateTime;
import java.util.UUID;

public class LevelHistory {
    private final UUID playerId;
    private final int level;
    private final LocalDateTime timestamp;
    
    public LevelHistory(UUID playerId, int level) {
        this.playerId = playerId;
        this.level = level;
        this.timestamp = LocalDateTime.now();
    }
    
    public UUID getPlayerId() { return playerId; }
    public int getLevel() { return level; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
