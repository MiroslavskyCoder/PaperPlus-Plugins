package com.webx.ranks.models;

import java.util.*;

/**
 * Associates a player with their rank(s) and tracks rank history
 */
public class PlayerRank {
    private UUID playerId;
    private String playerName;
    private String primaryRank;            // Main rank displayed in chat/tab
    private Set<String> additionalRanks;   // Secondary ranks for permission inheritance
    private long assignedAt;
    private String assignedBy;             // Admin who assigned (UUID or "SYSTEM")
    private String reason;                 // Assignment reason
    private long expiresAt;                // 0 = permanent
    private boolean active;
    private Map<String, Long> rankHistory; // rankId -> timestamp

    // Constructors
    public PlayerRank() {
        this.additionalRanks = new HashSet<>();
        this.rankHistory = new LinkedHashMap<>();
    }

    public PlayerRank(UUID playerId, String playerName) {
        this();
        this.playerId = playerId;
        this.playerName = playerName;
        this.active = true;
        this.assignedAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public UUID getPlayerId() { return playerId; }
    public void setPlayerId(UUID playerId) { this.playerId = playerId; }

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    public String getPrimaryRank() { return primaryRank; }
    public void setPrimaryRank(String primaryRank) { 
        if (this.primaryRank != null && !this.primaryRank.equals(primaryRank)) {
            rankHistory.put(this.primaryRank, System.currentTimeMillis());
        }
        this.primaryRank = primaryRank;
        this.assignedAt = System.currentTimeMillis();
    }

    public Set<String> getAdditionalRanks() { return additionalRanks; }
    public void setAdditionalRanks(Set<String> additionalRanks) { 
        this.additionalRanks = additionalRanks;
    }

    public void addAdditionalRank(String rankId) {
        additionalRanks.add(rankId);
    }

    public void removeAdditionalRank(String rankId) {
        additionalRanks.remove(rankId);
    }

    public boolean hasRank(String rankId) {
        return (primaryRank != null && primaryRank.equals(rankId)) || 
               additionalRanks.contains(rankId);
    }

    public long getAssignedAt() { return assignedAt; }
    public String getAssignedBy() { return assignedBy; }
    public void setAssignedBy(String assignedBy) { this.assignedBy = assignedBy; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public long getExpiresAt() { return expiresAt; }
    public void setExpiresAt(long expiresAt) { this.expiresAt = expiresAt; }

    public boolean isExpired() {
        return expiresAt > 0 && System.currentTimeMillis() > expiresAt;
    }

    public boolean isActive() { return active && !isExpired(); }
    public void setActive(boolean active) { this.active = active; }

    public Map<String, Long> getRankHistory() { return rankHistory; }
}
