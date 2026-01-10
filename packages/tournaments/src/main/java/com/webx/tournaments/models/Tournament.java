package com.webx.tournaments.models;

public class Tournament {
    private final String name;
    private boolean isActive;
    
    public Tournament(String name) {
        this.name = name;
        this.isActive = false;
    }
    
    public String getName() { return name; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { this.isActive = active; }
}
