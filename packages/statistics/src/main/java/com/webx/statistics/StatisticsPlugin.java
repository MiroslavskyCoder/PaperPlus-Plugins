package com.webx.statistics;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StatisticsPlugin extends JavaPlugin implements Listener {
    private static StatisticsPlugin instance;
    private Map<UUID, PlayerStats> playerStats = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        
        getServer().getPluginManager().registerEvents(this, this);
        
        getCommand("stats").setExecutor((sender, cmd, label, args) -> {
            if (!(sender instanceof Player)) return true;
            Player player = (Player) sender;
            PlayerStats stats = playerStats.get(player.getUniqueId());
            
            if (stats == null) {
                player.sendMessage("§cNo stats yet!");
                return true;
            }
            
            player.sendMessage("§a=== Your Statistics ===");
            player.sendMessage("§6Joins: §f" + stats.joins);
            player.sendMessage("§6Kills: §f" + stats.kills);
            player.sendMessage("§6Deaths: §f" + stats.deaths);
            player.sendMessage("§6K/D Ratio: §f" + String.format("%.2f", stats.getKDRatio()));
            player.sendMessage("§6Play Time: §f" + stats.getPlaytimeHours() + " hours");
            
            return true;
        });
        
        getLogger().info("Statistics Plugin enabled!");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        
        playerStats.computeIfAbsent(uuid, k -> new PlayerStats()).joins++;
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Сохранение статистики
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player killed = event.getEntity();
        Player killer = killed.getKiller();
        
        PlayerStats killedStats = playerStats.get(killed.getUniqueId());
        if (killedStats != null) {
            killedStats.deaths++;
        }
        
        if (killer != null) {
            PlayerStats killerStats = playerStats.get(killer.getUniqueId());
            if (killerStats != null) {
                killerStats.kills++;
            }
        }
    }
    
    public static StatisticsPlugin getInstance() {
        return instance;
    }
    
    private static class PlayerStats {
        int joins = 0;
        int kills = 0;
        int deaths = 0;
        long joinTime = System.currentTimeMillis();
        
        double getKDRatio() {
            return deaths == 0 ? kills : (double) kills / deaths;
        }
        
        long getPlaytimeHours() {
            return (System.currentTimeMillis() - joinTime) / 3600000;
        }
    }
}
