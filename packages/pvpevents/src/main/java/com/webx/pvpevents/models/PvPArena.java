package com.webx.pvpevents.models;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PvPArena {
    private final String name;
    private final Set<UUID> participants = new HashSet<>();
    private boolean active;
    
    public PvPArena(String name) {
        this.name = name;
        this.active = false;
    }
    
    public void addParticipant(UUID uuid) { participants.add(uuid); }
    public void removeParticipant(UUID uuid) { participants.remove(uuid); }
    public Set<UUID> getParticipants() { return new HashSet<>(participants); }
    public String getName() { return name; }
    public boolean isActive() { return active; }
}
