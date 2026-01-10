package com.webx.antispam.listeners;

import com.webx.antispam.AntiSpamPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandSpamListener implements Listener {
    private final AntiSpamPlugin plugin;
    
    public CommandSpamListener(AntiSpamPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (plugin.getSpamManager().isSpamming(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cCommand spam detected!");
            plugin.getSpamManager().addViolation(event.getPlayer().getUniqueId());
        }
    }
}
