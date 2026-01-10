package com.webx.chatformatting.listeners;

import com.webx.chatformatting.ChatFormattingPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ChatPrefixListener implements Listener {
    private final ChatFormattingPlugin plugin;
    
    public ChatPrefixListener(ChatFormattingPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage("§6Chat formatting enabled!");
    }
}
