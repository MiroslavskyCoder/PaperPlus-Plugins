package com.webx.feed.models;

import java.time.LocalDateTime;
import java.util.UUID;

public class FoodLog {
    private final UUID playerId;
    private final int foodLevel;
    private final LocalDateTime timestamp;
    
    public FoodLog(UUID playerId, int foodLevel) {
        this.playerId = playerId;
        this.foodLevel = foodLevel;
        this.timestamp = LocalDateTime.now();
    }
    
    public UUID getPlayerId() { return playerId; }
    public int getFoodLevel() { return foodLevel; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
