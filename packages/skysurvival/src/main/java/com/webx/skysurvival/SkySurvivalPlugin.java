package com.webx.skysuvival;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SkySurvivalPlugin extends JavaPlugin {
    private static SkySurvivalPlugin instance;
    private World skyWorld;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        
        // Проверяем наличие мира для выживания в небе
        String worldName = getConfig().getString("world-name", "sky_survival");
        skyWorld = Bukkit.getWorld(worldName);
        
        if (skyWorld == null) {
            getLogger().warning("Sky Survival world not found!");
        }
        
        getCommand("skysurvival").setExecutor((sender, cmd, label, args) -> {
            if (!(sender instanceof Player)) return true;
            Player player = (Player) sender;
            
            if (skyWorld != null) {
                Location spawnLoc = skyWorld.getSpawnLocation();
                player.teleport(spawnLoc);
                player.sendMessage("§aTeleported to Sky Survival!");
            } else {
                player.sendMessage("§cSky Survival world not available!");
            }
            
            return true;
        });
        
        getLogger().info("Sky Survival Plugin enabled!");
    }
    
    public static SkySurvivalPlugin getInstance() {
        return instance;
    }
}
