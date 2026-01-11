package com.webx.leveling.models;

import java.util.UUID;

public class PlayerLevel {
    private final UUID uuid;
    private int level;
    private long totalExp;
    
    public PlayerLevel(UUID uuid, int level, long totalExp) {
        this.uuid = uuid;
        this.level = level;
        this.totalExp = totalExp;
    }
    
    public void addExp(long amount) {
        this.totalExp += amount;
        while (totalExp >= (level * 1000)) {
            this.level++;
        }
    }
    
    public UUID getUuid() { return uuid; }
    public int getLevel() { return level; }
    public long getTotalExp() { return totalExp; }
}
