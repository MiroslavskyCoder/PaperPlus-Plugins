package com.webx.clans.listeners;

import com.webx.clans.ClansPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    private final ClansPlugin plugin;

    public PlayerJoinListener(ClansPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        var clan = plugin.getClanManager().getClanByMember(event.getPlayer().getUniqueId());
        if (clan != null) {
            event.getPlayer().sendMessage("§7Welcome back to §6" + clan.getName());
        }
    }
}
