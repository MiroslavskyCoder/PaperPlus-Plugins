package com.webx.leveling.managers;

import com.webx.leveling.models.PlayerLevel;
import org.bukkit.entity.Player;
import java.util.*;

public class LevelingManager {
    private final Map<UUID, PlayerLevel> playerLevels = new HashMap<>();
    private final int expPerLevel;
    
    public LevelingManager(int expPerLevel) {
        this.expPerLevel = expPerLevel;
    }
    
    public void addExp(Player player, int amount) {
        PlayerLevel level = playerLevels.computeIfAbsent(player.getUniqueId(), 
            uuid -> new PlayerLevel(uuid, 1, 0));
        level.addExp(amount);
    }
    
    public int getLevel(UUID uuid) {
        PlayerLevel level = playerLevels.get(uuid);
        return level != null ? level.getLevel() : 1;
    }
}
