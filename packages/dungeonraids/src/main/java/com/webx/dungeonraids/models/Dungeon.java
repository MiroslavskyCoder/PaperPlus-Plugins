package com.webx.dungeonraids.models;

public class Dungeon {
    private final String name;
    private final int difficulty;
    
    public Dungeon(String name, int difficulty) {
        this.name = name;
        this.difficulty = difficulty;
    }
    
    public String getName() { return name; }
    public int getDifficulty() { return difficulty; }
}
