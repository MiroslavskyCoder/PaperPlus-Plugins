package com.webx.antispam.listeners;

import com.webx.antispam.AntiSpamPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatSpamListener implements Listener {
    private final AntiSpamPlugin plugin;
    
    public ChatSpamListener(AntiSpamPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (plugin.getSpamManager().isSpamming(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cYou are spamming!");
            plugin.getSpamManager().addViolation(event.getPlayer().getUniqueId());
        }
    }
}
