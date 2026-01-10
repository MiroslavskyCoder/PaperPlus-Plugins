package com.webx.clans.utils;

import com.webx.clans.ClansPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Map;

public class MessageManager {
    private final ClansPlugin plugin;
    private String prefix;

    public MessageManager(ClansPlugin plugin) {
        this.plugin = plugin;
        loadPrefix();
    }

    private void loadPrefix() {
        this.prefix = ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("messages.prefix", "&8[&6Clans&8]&r "));
    }

    public void send(Player player, String key, Map<String, String> placeholders) {
        String message = plugin.getConfig().getString("messages." + key, key);
        message = ChatColor.translateAlternateColorCodes('&', message);

        if (placeholders != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                message = message.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }

        player.sendMessage(prefix + message);
    }

    public void send(Player player, String key) {
        send(player, key, null);
    }
}
