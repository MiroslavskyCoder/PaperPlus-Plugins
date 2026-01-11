package com.webx.pvpevents.models;

public class PvPEvent {
    private final String name;
    private final String description;
    private boolean isActive;
    
    public PvPEvent(String name, String description) {
        this.name = name;
        this.description = description;
        this.isActive = false;
    }
    
    public String getName() { return name; }
    public String getDescription() { return description; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { this.isActive = active; }
}
