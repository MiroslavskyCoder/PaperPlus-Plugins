package com.webx.skyesurvival.managers;

import java.util.*;

public class SurvivalManager {
    private final Map<UUID, Integer> playerResources = new HashMap<>();
    
    public void addResource(UUID uuid, int amount) {
        playerResources.put(uuid, playerResources.getOrDefault(uuid, 0) + amount);
    }
    
    public int getResources(UUID uuid) {
        return playerResources.getOrDefault(uuid, 0);
    }
}
