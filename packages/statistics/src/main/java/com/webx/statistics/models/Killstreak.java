package com.webx.statistics.models;

import java.util.UUID;

public class Killstreak {
    private final UUID playerId;
    private int currentKills;
    private int maxKills;
    
    public Killstreak(UUID playerId) {
        this.playerId = playerId;
        this.currentKills = 0;
        this.maxKills = 0;
    }
    
    public void addKill() {
        currentKills++;
        maxKills = Math.max(maxKills, currentKills);
    }
    
    public void reset() { currentKills = 0; }
    public int getKills() { return currentKills; }
    public int getMaxKills() { return maxKills; }
}
