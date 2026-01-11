package com.webx.speedrun.models;

import java.time.LocalDateTime;
import java.util.UUID;

public class SpeedRunRecord {
    private final UUID playerId;
    private final String playerName;
    private final long timeMillis;
    private final LocalDateTime recordedAt;
    
    public SpeedRunRecord(UUID playerId, String playerName, long timeMillis) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.timeMillis = timeMillis;
        this.recordedAt = LocalDateTime.now();
    }
    
    public UUID getPlayerId() { return playerId; }
    public String getPlayerName() { return playerName; }
    public long getTimeMillis() { return timeMillis; }
    public LocalDateTime getRecordedAt() { return recordedAt; }
}
