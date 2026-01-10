package com.webx.petsystem.models;

import java.util.UUID;

public class PetAbility {
    private final String name;
    private final int level;
    private final long cooldown;
    
    public PetAbility(String name, int level, long cooldown) {
        this.name = name;
        this.level = level;
        this.cooldown = cooldown;
    }
    
    public String getName() { return name; }
    public int getLevel() { return level; }
    public long getCooldown() { return cooldown; }
}
