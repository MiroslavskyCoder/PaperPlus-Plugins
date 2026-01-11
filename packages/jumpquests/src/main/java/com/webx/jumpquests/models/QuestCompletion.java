package com.webx.jumpquests.models;

import java.time.LocalDateTime;
import java.util.UUID;

public class QuestCompletion {
    private final UUID playerId;
    private final String questId;
    private final LocalDateTime completedAt;
    private final long duration;
    
    public QuestCompletion(UUID playerId, String questId, long duration) {
        this.playerId = playerId;
        this.questId = questId;
        this.completedAt = LocalDateTime.now();
        this.duration = duration;
    }
    
    public UUID getPlayerId() { return playerId; }
    public String getQuestId() { return questId; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public long getDuration() { return duration; }
}
