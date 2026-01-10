package com.webx.levels;

import org.bukkit.entity.Player;
import java.util.*;

public class LevelsManager {
    private Map<UUID, Integer> playerLevels = new HashMap<>();
    
    public void addLevel(Player player) {
        UUID uuid = player.getUniqueId();
        int level = playerLevels.getOrDefault(uuid, 0) + 1;
        playerLevels.put(uuid, level);
        player.sendMessage("§6Level up! You are now level §f" + level);
    }
    
    public int getLevel(Player player) {
        return playerLevels.getOrDefault(player.getUniqueId(), 0);
    }
    
    public void setLevel(Player player, int level) {
        playerLevels.put(player.getUniqueId(), level);
    }
}
