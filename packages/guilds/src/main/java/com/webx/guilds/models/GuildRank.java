package com.webx.guilds.models;

import java.time.LocalDateTime;

public class GuildRank {
    private final String name;
    private final int level;
    private final LocalDateTime createdAt;
    
    public GuildRank(String name, int level) {
        this.name = name;
        this.level = level;
        this.createdAt = LocalDateTime.now();
    }
    
    public String getName() { return name; }
    public int getLevel() { return level; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
