package com.webx.bedwarsevent.models;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BedWarsTeam {
    private final String name;
    private final Set<UUID> players = new HashSet<>();
    private boolean bedDestroyed;
    
    public BedWarsTeam(String name) {
        this.name = name;
        this.bedDestroyed = false;
    }
    
    public void addPlayer(UUID uuid) { players.add(uuid); }
    public Set<UUID> getPlayers() { return new HashSet<>(players); }
    public String getName() { return name; }
    public boolean isBedDestroyed() { return bedDestroyed; }
    public void destroyBed() { this.bedDestroyed = true; }
}
