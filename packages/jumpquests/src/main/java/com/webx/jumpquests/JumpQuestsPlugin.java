package com.webx.jumpquests;
import com.webx.jumpquests.managers.QuestManager;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class JumpQuestsPlugin extends JavaPlugin implements Listener {
    private static JumpQuestsPlugin instance;
    private List<JumpQuest> quests = new ArrayList<>();
    private Map<UUID, Integer> playerProgress = new HashMap<>();
    private QuestManager questManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
            questManager = new QuestManager();
        getServer().getPluginManager().registerEvents(this, this);
        
        getCommand("jumpquest").setExecutor((sender, cmd, label, args) -> {
            if (!(sender instanceof Player)) return true;
            Player player = (Player) sender;
            
            if (args.length == 0) {
                player.sendMessage("§cUsage: /jumpquest [list/join <id>/checkpoint]");
                return true;
            }
            
            if (args[0].equalsIgnoreCase("list")) {
                listQuests(player);
            } else if (args[0].equalsIgnoreCase("join") && args.length > 1) {
                joinQuest(player, Integer.parseInt(args[1]));
            } else if (args[0].equalsIgnoreCase("checkpoint")) {
                setCheckpoint(player);
            }
            
            return true;
        });
        
        getLogger().info("Jump Quests Plugin enabled!");
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        // Проверка на достижение контрольных точек
    }
    
    private void listQuests(Player player) {
        player.sendMessage("§a=== Available Jump Quests ===");
        for (int i = 0; i < quests.size(); i++) {
            player.sendMessage("§f" + i + ". " + quests.get(i).name + " (Difficulty: " + quests.get(i).difficulty + ")");
        }
    }
    
    private void joinQuest(Player player, int questId) {
        if (questId < 0 || questId >= quests.size()) {
            player.sendMessage("§cInvalid quest!");
            return;
        }
        
        playerProgress.put(player.getUniqueId(), 0);
        player.sendMessage("§aYou joined quest: " + quests.get(questId).name);
    }
    
    private void setCheckpoint(Player player) {
        player.sendMessage("§aCheckpoint saved!");
    }
    
    public static JumpQuestsPlugin getInstance() {
        return instance;
    }

    public QuestManager getQuestManager() {
        return questManager;
    }
    
    private static class JumpQuest {
        String name;
        List<Location> checkpoints = new ArrayList<>();
        int difficulty;
        
        JumpQuest(String name, int difficulty) {
            this.name = name;
            this.difficulty = difficulty;
        }
    }
}
