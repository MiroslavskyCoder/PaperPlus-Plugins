package com.webx.randomizer.models;

public class Reward {
    private final String name;
    private final int value;
    private final double probability;
    
    public Reward(String name, int value, double probability) {
        this.name = name;
        this.value = value;
        this.probability = probability;
    }
    
    public String getName() { return name; }
    public int getValue() { return value; }
    public double getProbability() { return probability; }
}
