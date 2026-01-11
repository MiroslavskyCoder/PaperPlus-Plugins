package com.webx.woodcutting;

import org.bukkit.entity.Player;
import java.util.*;

public class WoodcuttingManager {
    private Map<UUID, Integer> logsChopped = new HashMap<>();
    
    public void chopLog(Player player) {
        UUID uuid = player.getUniqueId();
        int count = logsChopped.getOrDefault(uuid, 0) + 1;
        logsChopped.put(uuid, count);
        if (count % 5 == 0) {
            player.sendMessage("§a✓ Chopped §f" + count + " §alogs!");
        }
    }
    
    public int getLogsChopped(Player player) {
        return logsChopped.getOrDefault(player.getUniqueId(), 0);
    }
}
