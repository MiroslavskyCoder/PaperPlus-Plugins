package com.webx.economy.listeners;

import com.webx.economy.EconomyPlugin;
import com.webx.economy.models.Account;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    private final EconomyPlugin plugin;

    public PlayerQuitListener(EconomyPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Account account = plugin.getAccountManager().getAccount(event.getPlayer());
        plugin.getAccountManager().saveAccount(account);
    }
}
