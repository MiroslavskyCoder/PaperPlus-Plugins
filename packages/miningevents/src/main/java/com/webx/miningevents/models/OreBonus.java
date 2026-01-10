package com.webx.miningevents.models;

import java.time.LocalDateTime;

public class OreBonus {
    private final String oreName;
    private final double multiplier;
    private final LocalDateTime startTime;
    
    public OreBonus(String oreName, double multiplier) {
        this.oreName = oreName;
        this.multiplier = multiplier;
        this.startTime = LocalDateTime.now();
    }
    
    public String getOreName() { return oreName; }
    public double getMultiplier() { return multiplier; }
    public LocalDateTime getStartTime() { return startTime; }
}
