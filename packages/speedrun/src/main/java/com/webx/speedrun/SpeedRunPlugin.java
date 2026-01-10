package com.webx.speedrun;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpeedRunPlugin extends JavaPlugin implements Listener {
    private static SpeedRunPlugin instance;
    private Map<UUID, SpeedRunData> playerRuns = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
        
        getCommand("speedrun").setExecutor((sender, cmd, label, args) -> {
            if (!(sender instanceof Player)) return true;
            Player player = (Player) sender;
            
            if (args.length == 0) {
                player.sendMessage("§cUsage: /speedrun [start/stop/time]");
                return true;
            }
            
            if (args[0].equalsIgnoreCase("start")) {
                startRun(player);
            } else if (args[0].equalsIgnoreCase("stop")) {
                stopRun(player);
            } else if (args[0].equalsIgnoreCase("time")) {
                showTime(player);
            }
            
            return true;
        });
        
        getLogger().info("Speed Run Plugin enabled!");
    }
    
    private void startRun(Player player) {
        playerRuns.put(player.getUniqueId(), new SpeedRunData());
        player.sendMessage("§aSpeed run started!");
    }
    
    private void stopRun(Player player) {
        SpeedRunData data = playerRuns.remove(player.getUniqueId());
        if (data != null) {
            long time = System.currentTimeMillis() - data.startTime;
            player.sendMessage("§aSpeed run completed in §f" + (time / 1000) + "§a seconds!");
        }
    }
    
    private void showTime(Player player) {
        SpeedRunData data = playerRuns.get(player.getUniqueId());
        if (data != null) {
            long elapsed = System.currentTimeMillis() - data.startTime;
            player.sendMessage("§aTime: §f" + (elapsed / 1000) + "§a seconds");
        } else {
            player.sendMessage("§cNo active speed run!");
        }
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // Проверка контрольных точек
    }
    
    public static SpeedRunPlugin getInstance() {
        return instance;
    }
    
    private static class SpeedRunData {
        long startTime = System.currentTimeMillis();
        Location startLocation;
    }
}
