package com.webx.chatformatting;

import com.webx.chatformatting.managers.ChannelManager;
import com.webx.chatformatting.managers.ChatFormattingManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatFormattingPlugin extends JavaPlugin implements Listener {
    private static ChatFormattingPlugin instance;
    private ChannelManager channelManager;
    private ChatFormattingManager chatFormattingManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        channelManager = new ChannelManager();
        chatFormattingManager = new ChatFormattingManager();
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("Chat Formatting Plugin enabled!");
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String format = event.getFormat();
        
        // Пример форматирования чата
        String prefix = getConfig().getString("chat-prefix", "§8[%player%]§r ");
        String formatted = prefix + event.getMessage();
        
        formatted = formatted.replace("%player%", event.getPlayer().getName());
        formatted = formatted.replace("%displayname%", event.getPlayer().getDisplayName());
        formatted = formatted.replace("&", "§");
        
        event.setFormat(formatted);
    }
    
    public static ChatFormattingPlugin getInstance() {
        return instance;
    }
    
    public ChannelManager getChannelManager() {
        return channelManager;
    }
    
    public ChatFormattingManager getChatFormattingManager() {
        return chatFormattingManager;
    }
}
