package com.webx.mounts;

import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import java.util.*;

public class MountsManager {
    private Map<UUID, String> playerMounts = new HashMap<>();
    
    public void setMount(Player player, String mount) {
        playerMounts.put(player.getUniqueId(), mount);
        player.sendMessage("§6✓ Mount set to: " + mount);
    }
    
    public String getMount(Player player) {
        return playerMounts.getOrDefault(player.getUniqueId(), "none");
    }
    
    public boolean hasMount(Player player) {
        return playerMounts.containsKey(player.getUniqueId());
    }
    
    public void summonMount(Player player) {
        if (hasMount(player)) {
            player.sendMessage("§aMount summoned!");
        }
    }
}
