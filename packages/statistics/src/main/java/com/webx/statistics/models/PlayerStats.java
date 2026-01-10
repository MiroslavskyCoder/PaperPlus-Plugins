package com.webx.statistics.models;

import java.util.UUID;

public class PlayerStats {
    private final UUID uuid;
    private int kills;
    private int deaths;
    private long playtime;
    
    public PlayerStats(UUID uuid) {
        this.uuid = uuid;
        this.kills = 0;
        this.deaths = 0;
        this.playtime = 0;
    }
    
    public void addKill() { this.kills++; }
    public void addDeath() { this.deaths++; }
    
    public int getKills() { return kills; }
    public int getDeaths() { return deaths; }
    public double getKDRatio() { return deaths == 0 ? kills : (double) kills / deaths; }
}
