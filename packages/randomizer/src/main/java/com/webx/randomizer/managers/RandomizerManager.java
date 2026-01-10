package com.webx.randomizer.managers;

import java.util.*;

public class RandomizerManager {
    private final Random random = new Random();
    
    public int getRandomReward() {
        return random.nextInt(1000);
    }
    
    public List<String> getRandomItems(int count) {
        List<String> items = Arrays.asList("Diamond", "Gold", "Iron", "Emerald", "Lapis");
        Collections.shuffle(items);
        return items.subList(0, Math.min(count, items.size()));
    }
}
