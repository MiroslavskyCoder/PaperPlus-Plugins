package com.webx.quests.listeners;

import com.webx.quests.QuestsPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class PlayerKillListener implements Listener {
    private final QuestsPlugin plugin;

    public PlayerKillListener(QuestsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        // TODO: Track kill objectives
    }
}
