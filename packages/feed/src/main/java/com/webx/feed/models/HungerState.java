package com.webx.feed.models;

public class HungerState {
    private int foodLevel;
    private float saturation;
    private int exhaustion;
    
    public HungerState(int foodLevel, float saturation) {
        this.foodLevel = foodLevel;
        this.saturation = saturation;
        this.exhaustion = 0;
    }
    
    public int getFoodLevel() { return foodLevel; }
    public float getSaturation() { return saturation; }
    public int getExhaustion() { return exhaustion; }
    
    public void setFoodLevel(int level) { this.foodLevel = level; }
    public void setSaturation(float sat) { this.saturation = sat; }
}
