package com.webx.combat;

import org.bukkit.entity.Player;
import java.util.*;

public class CombatManager {
    private Map<UUID, Long> combatTimer = new HashMap<>();
    private static final long COMBAT_COOLDOWN = 5000;
    
    public void enterCombat(Player player) {
        combatTimer.put(player.getUniqueId(), System.currentTimeMillis());
        player.sendMessage("§c⚔ You are now in combat!");
    }
    
    public boolean isInCombat(Player player) {
        Long lastTime = combatTimer.get(player.getUniqueId());
        if (lastTime == null) return false;
        return (System.currentTimeMillis() - lastTime) < COMBAT_COOLDOWN;
    }
    
    public void exitCombat(Player player) {
        combatTimer.remove(player.getUniqueId());
        player.sendMessage("§aYou exited combat!");
    }
    
    public long getTimeInCombat(Player player) {
        Long lastTime = combatTimer.get(player.getUniqueId());
        return lastTime == null ? 0 : System.currentTimeMillis() - lastTime;
    }
}
