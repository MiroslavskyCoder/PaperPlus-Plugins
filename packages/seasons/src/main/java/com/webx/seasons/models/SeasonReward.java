package com.webx.seasons.models;

import java.time.LocalDateTime;

public class SeasonReward {
    private final String name;
    private final int coins;
    private final LocalDateTime claimedAt;
    
    public SeasonReward(String name, int coins) {
        this.name = name;
        this.coins = coins;
        this.claimedAt = LocalDateTime.now();
    }
    
    public String getName() { return name; }
    public int getCoins() { return coins; }
    public LocalDateTime getClaimedAt() { return claimedAt; }
}
