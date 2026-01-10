package com.webx.antispam.models;

import java.util.UUID;

public class MuteData {
    private final UUID playerUuid;
    private long muteEndTime;
    private String reason;
    
    public MuteData(UUID playerUuid, long duration, String reason) {
        this.playerUuid = playerUuid;
        this.muteEndTime = System.currentTimeMillis() + duration;
        this.reason = reason;
    }
    
    public boolean isMuted() { return System.currentTimeMillis() < muteEndTime; }
    public String getReason() { return reason; }
}
