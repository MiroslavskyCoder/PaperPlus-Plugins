package com.webx.chatformatting.listeners;

import com.webx.chatformatting.ChatFormattingPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatColorListener implements Listener {
    private final ChatFormattingPlugin plugin;
    
    public ChatColorListener(ChatFormattingPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        String formatted = plugin.getChatFormattingManager().formatMessage(event.getPlayer(), event.getMessage());
        event.setFormat(formatted);
    }
}
