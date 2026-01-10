package com.webx.customenchants.models;

public class CustomEnchant {
    private final String name;
    private final String description;
    private final int maxLevel;
    
    public CustomEnchant(String name, String description, int maxLevel) {
        this.name = name;
        this.description = description;
        this.maxLevel = maxLevel;
    }
    
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getMaxLevel() { return maxLevel; }
}
