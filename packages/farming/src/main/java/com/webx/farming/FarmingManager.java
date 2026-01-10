package com.webx.farming;

import org.bukkit.entity.Player;
import java.util.*;

public class FarmingManager {
    private Map<UUID, Integer> cropsHarvested = new HashMap<>();
    
    public void harvestCrop(Player player) {
        UUID uuid = player.getUniqueId();
        int count = cropsHarvested.getOrDefault(uuid, 0) + 1;
        cropsHarvested.put(uuid, count);
        if (count % 10 == 0) {
            player.sendMessage("§a✓ Harvested §f" + count + " §acrops!");
        }
    }
    
    public int getCropsHarvested(Player player) {
        return cropsHarvested.getOrDefault(player.getUniqueId(), 0);
    }
}
