package com.webx.economy.listeners;

import com.webx.economy.EconomyPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    private final EconomyPlugin plugin;

    public PlayerJoinListener(EconomyPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getAccountManager().getAccount(event.getPlayer());
    }
}
