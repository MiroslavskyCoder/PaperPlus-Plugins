package com.webx.pvpevents;

import com.webx.pvpevents.managers.PvPEventManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class PvPEventsPlugin extends JavaPlugin {
    private static PvPEventsPlugin instance;
    private PvPEventManager pvpEventManager;
    private PvPEvent currentEvent;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        
        pvpEventManager = new PvPEventManager();
        
        getCommand("pvpevent").setExecutor((sender, cmd, label, args) -> {
            if (!sender.hasPermission("pvpevent.admin")) {
                sender.sendMessage("§cNo permission!");
                return true;
            }
            
            if (args.length == 0) {
                sender.sendMessage("§cUsage: /pvpevent [start/stop/type]");
                return true;
            }
            
            switch (args[0].toLowerCase()) {
                case "start":
                    startEvent();
                    sender.sendMessage("§aPvP Event started!");
                    break;
                case "stop":
                    stopEvent();
                    sender.sendMessage("§aEvent stopped!");
                    break;
                case "type":
                    if (args.length > 1) {
                        currentEvent = new PvPEvent(args[1]);
                        sender.sendMessage("§aEvent type changed!");
                    }
                    break;
            }
            
            return true;
        });
        
        getLogger().info("PvP Events Plugin enabled!");
    }
    
    private void startEvent() {
        if (currentEvent == null) {
            currentEvent = new PvPEvent("Deathmatch");
        }
        
        currentEvent.isActive = true;
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setGameMode(GameMode.SURVIVAL);
            player.sendMessage("§c§lPvP EVENT STARTED: " + currentEvent.type);
        }
    }
    
    private void stopEvent() {
        if (currentEvent != null) {
            currentEvent.isActive = false;
            
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage("§aEvent ended!");
            }
        }
    }
    
    public static PvPEventsPlugin getInstance() {
        return instance;
    }
    
    public PvPEventManager getPvpEventManager() {
        return pvpEventManager;
    }
    
    private static class PvPEvent {
        String type;
        boolean isActive;
        
        PvPEvent(String type) {
            this.type = type;
            this.isActive = false;
        }
    }
}
