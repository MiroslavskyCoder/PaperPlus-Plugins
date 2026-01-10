package com.webx.leveling;

import org.bukkit.Bukkit;
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

public class LevelingPlugin extends JavaPlugin implements Listener {
    private static LevelingPlugin instance;
    private Map<UUID, PlayerLevel> playerLevels = new HashMap<>();
    
    private int expPerKill;
    private int expPerLevel;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        
        expPerKill = getConfig().getInt("exp-per-kill", 10);
        expPerLevel = getConfig().getInt("exp-per-level", 100);
        
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("level").setExecutor((sender, cmd, label, args) -> {
            if (!(sender instanceof Player)) return true;
            Player player = (Player) sender;
            PlayerLevel level = playerLevels.get(player.getUniqueId());
            if (level == null) {
                player.sendMessage("§cNo level data found!");
                return true;
            }
            player.sendMessage("§a=== Your Level ===");
            player.sendMessage("§6Level: §f" + level.level);
            player.sendMessage("§6EXP: §f" + level.experience + "/" + expPerLevel);
            return true;
        });
        
        getLogger().info("Leveling Plugin enabled!");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        
        if (!playerLevels.containsKey(uuid)) {
            playerLevels.put(uuid, new PlayerLevel());
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Сохранение данных можно добавить позже
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player killed = event.getEntity();
        Player killer = killed.getKiller();
        
        if (killer == null) return;
        
        UUID killerUUID = killer.getUniqueId();
        PlayerLevel killerLevel = playerLevels.get(killerUUID);
        
        if (killerLevel != null) {
            killerLevel.addExperience(expPerKill);
            
            if (killerLevel.experience >= expPerLevel) {
                killerLevel.levelUp();
                killer.sendMessage("§6§l*** LEVEL UP! ***");
                killer.sendMessage("§aYou are now level " + killerLevel.level);
                Bukkit.broadcastMessage("§6" + killer.getName() + " §elevel up to §f" + killerLevel.level);
            }
        }
    }
    
    public static LevelingPlugin getInstance() {
        return instance;
    }
    
    public int getPlayerLevel(Player player) {
        PlayerLevel level = playerLevels.get(player.getUniqueId());
        return level != null ? level.level : 1;
    }
    
    private static class PlayerLevel {
        int level = 1;
        int experience = 0;
        
        void addExperience(int exp) {
            this.experience += exp;
        }
        
        void levelUp() {
            level++;
            experience = 0;
        }
    }
}
