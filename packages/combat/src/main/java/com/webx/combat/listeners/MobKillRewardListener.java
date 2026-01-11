package com.webx.combat.listeners;

import com.webx.economy.EconomyPlugin;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

/**
 * Listener that rewards players with coins for killing mobs
 * Integration with Economy plugin: +1 coin per mob kill
 */
public class MobKillRewardListener implements Listener {
    private static final double COINS_PER_KILL = 1.0;

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();
        
        // Check if killed by player
        if (killer == null) return;
        
        // Don't reward for killing players (PvP)
        if (entity instanceof Player) return;
        
        // Get Economy plugin
        EconomyPlugin economyPlugin = (EconomyPlugin) killer.getServer()
                .getPluginManager()
                .getPlugin("Economy");
        
        if (economyPlugin == null) {
            // Economy plugin not loaded
            return;
        }
        
        // Add coins to player account
        boolean success = economyPlugin.getAccountManager()
                .deposit(killer.getUniqueId(), COINS_PER_KILL);
        
        if (success) {
            killer.sendMessage("§6+§e" + COINS_PER_KILL + " §6coin" + (COINS_PER_KILL == 1 ? "" : "s"));
        }
    }
}
