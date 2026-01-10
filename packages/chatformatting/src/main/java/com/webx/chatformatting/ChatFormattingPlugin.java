package com.webx.chatformatting;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatFormattingPlugin extends JavaPlugin implements Listener {
    private static ChatFormattingPlugin instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
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
}
