package com.webx.economy.utils;

import com.webx.economy.EconomyPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Map;

public class MessageManager {
    private final EconomyPlugin plugin;
    private String prefix;

    public MessageManager(EconomyPlugin plugin) {
        this.plugin = plugin;
        loadPrefix();
    }

    private void loadPrefix() {
        this.prefix = ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("messages.prefix", "&8[&6Economy&8]&r "));
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
