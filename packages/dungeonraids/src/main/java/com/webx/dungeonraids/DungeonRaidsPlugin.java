package com.webx.dungeonraids;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class DungeonRaidsPlugin extends JavaPlugin {
    private static DungeonRaidsPlugin instance;
    private List<Dungeon> dungeons = new ArrayList<>();
    private Map<UUID, DungeonProgress> playerProgress = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        
        // Инициализируем подземелья
        initializeDungeons();
        
        getCommand("dungeon").setExecutor((sender, cmd, label, args) -> {
            if (!(sender instanceof Player)) return true;
            Player player = (Player) sender;
            
            if (args.length == 0) {
                listDungeons(player);
                return true;
            }
            
            if (args[0].equalsIgnoreCase("join") && args.length > 1) {
                enterDungeon(player, Integer.parseInt(args[1]));
            }
            
            return true;
        });
        
        getLogger().info("Dungeon Raids Plugin enabled!");
    }
    
    private void initializeDungeons() {
        dungeons.add(new Dungeon("Forest Crypt", 1, 5));
        dungeons.add(new Dungeon("Obsidian Vault", 3, 10));
        dungeons.add(new Dungeon("Ancient Temple", 5, 15));
    }
    
    private void listDungeons(Player player) {
        player.sendMessage("§a=== Available Dungeons ===");
        for (int i = 0; i < dungeons.size(); i++) {
            Dungeon dungeon = dungeons.get(i);
            player.sendMessage("§f" + i + ". " + dungeon.name + " (Level " + dungeon.minLevel + "-" + dungeon.maxLevel + ")");
        }
    }
    
    private void enterDungeon(Player player, int dungeonId) {
        if (dungeonId < 0 || dungeonId >= dungeons.size()) {
            player.sendMessage("§cInvalid dungeon!");
            return;
        }
        
        playerProgress.put(player.getUniqueId(), new DungeonProgress(dungeonId));
        player.sendMessage("§aYou entered: " + dungeons.get(dungeonId).name);
    }
    
    public static DungeonRaidsPlugin getInstance() {
        return instance;
    }
    
    private static class Dungeon {
        String name;
        int minLevel;
        int maxLevel;
        
        Dungeon(String name, int minLevel, int maxLevel) {
            this.name = name;
            this.minLevel = minLevel;
            this.maxLevel = maxLevel;
        }
    }
    
    private static class DungeonProgress {
        int dungeonId;
        int progress = 0;
        
        DungeonProgress(int dungeonId) {
            this.dungeonId = dungeonId;
        }
    }
}
