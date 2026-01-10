package com.webx.afk.models;

import java.util.UUID;

public class AFKSession {
    private final UUID playerUuid;
    private final long startTime;
    private long endTime;
    
    public AFKSession(UUID playerUuid) {
        this.playerUuid = playerUuid;
        this.startTime = System.currentTimeMillis();
    }
    
    public UUID getPlayerUuid() { return playerUuid; }
    public long getDuration() { return (endTime > 0 ? endTime : System.currentTimeMillis()) - startTime; }
    public void end() { this.endTime = System.currentTimeMillis(); }
}
