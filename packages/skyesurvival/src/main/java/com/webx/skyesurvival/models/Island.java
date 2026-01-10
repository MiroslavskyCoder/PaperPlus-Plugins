package com.webx.skyesurvival.models;

import java.util.UUID;

public class Island {
    private final UUID ownerId;
    private final String name;
    private int size;
    
    public Island(UUID ownerId, String name) {
        this.ownerId = ownerId;
        this.name = name;
        this.size = 100;
    }
    
    public UUID getOwnerId() { return ownerId; }
    public String getName() { return name; }
    public int getSize() { return size; }
    public void expandIsland(int amount) { this.size += amount; }
}
