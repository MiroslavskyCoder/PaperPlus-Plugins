package com.webx.quests.listeners;

import com.webx.quests.QuestsPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {
    private final QuestsPlugin plugin;

    public PlayerMoveListener(QuestsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // TODO: Track location objectives
    }
}
