package com.webx.bedwarsevent;
import com.webx.bedwarsevent.managers.BedWarsManager;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class BedWarsEventPlugin extends JavaPlugin {
    private static BedWarsEventPlugin instance;
    private BedWarsGame game;
    private BedWarsManager bedWarsManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
            bedWarsManager = new BedWarsManager();
        
        getCommand("bedwars").setExecutor((sender, cmd, label, args) -> {
            if (!(sender instanceof Player)) return true;
            Player player = (Player) sender;
            
            if (args.length == 0) {
                if (game != null && game.isActive) {
                    player.sendMessage("§cA game is already running!");
                } else {
                    game = new BedWarsGame();
                    game.isActive = true;
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        game.players.add(p.getUniqueId());
                    }
                    player.sendMessage("§aBedWars game started!");
                    Bukkit.broadcastMessage("§c§lBED WARS EVENT STARTED!");
                }
            }
            
            return true;
        });
        
        getLogger().info("BedWars Event Plugin enabled!");
    }
    
    public static BedWarsEventPlugin getInstance() {
        return instance;
    
        public BedWarsManager getBedWarsManager() {
            return bedWarsManager;
        }
    }
    
    private static class BedWarsGame {
        Set<UUID> players = new HashSet<>();
        boolean isActive = false;
        Map<UUID, Integer> teamAssignments = new HashMap<>();
    }
}
