package com.webx.jumpquests.listeners;

import com.webx.jumpquests.JumpQuestsPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class QuestProgressListener implements Listener {
    private final JumpQuestsPlugin plugin;
    
    public QuestProgressListener(JumpQuestsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        // Check quest progress on movement
    }
}
