package com.webx.jumpquests.models;

import org.bukkit.Location;

public class Quest {
    private final String id;
    private final Location startLocation;
    private final Location endLocation;
    
    public Quest(String id, Location startLocation, Location endLocation) {
        this.id = id;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
    }
    
    public String getId() { return id; }
    public Location getStartLocation() { return startLocation; }
    public Location getEndLocation() { return endLocation; }
}
