package com.webx.afk.models;

import java.util.UUID;

public class AFKPlayer {
    private final UUID uuid;
    private long lastActivity;
    private boolean isAFK;
    private long afkSince;
    private int afkCounter;
    
    public AFKPlayer(UUID uuid) {
        this.uuid = uuid;
        this.lastActivity = System.currentTimeMillis();
        this.isAFK = false;
        this.afkCounter = 0;
    }
    
    public void updateActivity() {
        this.lastActivity = System.currentTimeMillis();
        if (isAFK) {
            this.isAFK = false;
        }
    }
    
    public long getLastActivity() {
        return lastActivity;
    }
    
    public void setAFK(boolean afk) {
        if (afk && !isAFK) {
            this.afkSince = System.currentTimeMillis();
            this.afkCounter++;
        }
        this.isAFK = afk;
    }
    
    public boolean isAFK() {
        return isAFK;
    }
    
    public long getAFKDuration() {
        return isAFK ? System.currentTimeMillis() - afkSince : 0;
    }
    
    public UUID getUUID() {
        return uuid;
    }
    
    public int getAFKCounter() {
        return afkCounter;
    }
}
