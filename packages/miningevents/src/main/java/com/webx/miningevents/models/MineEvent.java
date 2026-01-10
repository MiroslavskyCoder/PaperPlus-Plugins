package com.webx.miningevents.models;

public class MineEvent {
    private final String name;
    private double oreMultiplier;
    
    public MineEvent(String name, double oreMultiplier) {
        this.name = name;
        this.oreMultiplier = oreMultiplier;
    }
    
    public String getName() { return name; }
    public double getOreMultiplier() { return oreMultiplier; }
    public void setOreMultiplier(double mult) { this.oreMultiplier = mult; }
}
