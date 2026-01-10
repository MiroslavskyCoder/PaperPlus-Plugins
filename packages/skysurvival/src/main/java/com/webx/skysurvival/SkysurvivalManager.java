package com.webx.skysurvival;

import org.bukkit.entity.Player;
import java.util.*;

public class SkysurvivalManager {
    private Map<UUID, SkyIsland> playerIslands = new HashMap<>();
    
    public void createIsland(Player player) {
        UUID uuid = player.getUniqueId();
        playerIslands.put(uuid, new SkyIsland(player.getName()));
        player.sendMessage("§aYour sky island has been created!");
    }
    
    public SkyIsland getIsland(Player player) {
        return playerIslands.get(player.getUniqueId());
    }
    
    static class SkyIsland {
        String ownerName;
        long createdAt;
        int level;
        
        SkyIsland(String ownerName) {
            this.ownerName = ownerName;
            this.createdAt = System.currentTimeMillis();
            this.level = 1;
        }
    }
}
