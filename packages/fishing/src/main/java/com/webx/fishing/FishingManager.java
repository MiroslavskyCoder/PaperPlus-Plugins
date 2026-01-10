package com.webx.fishing;

import org.bukkit.entity.Player;
import java.util.*;

public class FishingManager {
    private Map<UUID, Integer> fishCount = new HashMap<>();
    
    public void addFish(Player player, int amount) {
        UUID uuid = player.getUniqueId();
        fishCount.put(uuid, fishCount.getOrDefault(uuid, 0) + amount);
        player.sendMessage("§b+§f" + amount + " fish! (Total: " + fishCount.get(uuid) + ")");
    }
    
    public int getFishCount(Player player) {
        return fishCount.getOrDefault(player.getUniqueId(), 0);
    }
    
    public void resetFishCount(Player player) {
        fishCount.remove(player.getUniqueId());
    }
}
