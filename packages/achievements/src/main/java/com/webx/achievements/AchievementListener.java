package com.webx.achievements;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.entity.Player;

public class AchievementListener implements Listener {
    private AchievementsPlugin plugin;
    
    public AchievementListener(AchievementsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        var achievements = plugin.getAchievementManager().getPlayerAchievements(player.getUniqueId());
        
        if (achievements.isEmpty()) {
            plugin.getAchievementManager().unlockAchievement(player, "first_step");
        }
    }
    
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() instanceof Player) {
            Player killer = event.getEntity().getKiller();
            var achievements = plugin.getAchievementManager().getPlayerAchievements(killer.getUniqueId());
            
            if (!achievements.contains("first_kill")) {
                plugin.getAchievementManager().unlockAchievement(killer, "first_kill");
            }
        }
    }
}
