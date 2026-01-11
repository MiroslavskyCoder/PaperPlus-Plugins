package com.webx.homesextended.managers;

import com.webx.homesextended.models.Home;
import org.bukkit.Location;
import java.util.*;

public class HomeManager {
    private final Map<UUID, List<Home>> playerHomes = new HashMap<>();
    
    public void setHome(UUID uuid, String name, Location location) {
        playerHomes.computeIfAbsent(uuid, k -> new ArrayList<>()).add(new Home(name, location));
    }
    
    public Home getHome(UUID uuid, String name) {
        List<Home> homes = playerHomes.getOrDefault(uuid, Collections.emptyList());
        return homes.stream().filter(h -> h.getName().equals(name)).findFirst().orElse(null);
    }
}
