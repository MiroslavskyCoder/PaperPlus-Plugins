package com.webx.jumpquests.listeners;

import com.webx.jumpquests.JumpQuestsPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class QuestJoinListener implements Listener {
    private final JumpQuestsPlugin plugin;
    
    public QuestJoinListener(JumpQuestsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        int questCount = plugin.getQuestManager().getAllQuests().size();
        event.getPlayer().sendMessage("§6There are §f" + questCount + "§6 quests available!");
    }
}
