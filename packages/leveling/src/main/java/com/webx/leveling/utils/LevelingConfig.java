package com.webx.leveling.utils;

public class LevelingConfig {
    private final int expPerLevel;
    private final boolean announceLevel;
    
    public LevelingConfig(int expPerLevel, boolean announceLevel) {
        this.expPerLevel = expPerLevel;
        this.announceLevel = announceLevel;
    }
    
    public int getExpPerLevel() { return expPerLevel; }
    public boolean shouldAnnounceLevel() { return announceLevel; }
}
