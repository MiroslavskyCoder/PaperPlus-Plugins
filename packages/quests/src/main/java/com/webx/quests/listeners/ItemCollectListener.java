package com.webx.quests.listeners;

import com.webx.quests.QuestsPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class ItemCollectListener implements Listener {
    private final QuestsPlugin plugin;

    public ItemCollectListener(QuestsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onItemCollect(PlayerPickupItemEvent event) {
        // TODO: Track item collection objectives
    }
}
