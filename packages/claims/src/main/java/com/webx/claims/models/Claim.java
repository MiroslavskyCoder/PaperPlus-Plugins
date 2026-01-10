package com.webx.claims.models;

import org.bukkit.Location;

public class Claim {
    private final String id;
    private final Location pos1;
    private final Location pos2;
    
    public Claim(String id, Location pos1, Location pos2) {
        this.id = id;
        this.pos1 = pos1;
        this.pos2 = pos2;
    }
    
    public String getId() { return id; }
    public Location getPos1() { return pos1; }
    public Location getPos2() { return pos2; }
}
