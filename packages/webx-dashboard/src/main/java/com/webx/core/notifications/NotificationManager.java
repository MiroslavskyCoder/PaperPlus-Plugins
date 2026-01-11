package com.webx.core.notifications;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.time.Duration;
import java.util.Collection;

/**
 * Centralized notification system for all plugins
 */
public class NotificationManager {
    private final Plugin plugin;

    public NotificationManager(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Send a notification to all online players
     */
    public void broadcastNotification(String message, NotificationType type) {
        broadcastNotification(message, type, Bukkit.getOnlinePlayers());
    }

    /**
     * Send a notification to specific players
     */
    public void broadcastNotification(String message, NotificationType type, Collection<? extends Player> players) {
        Component component = formatMessage(message, type);
        
        for (Player player : players) {
            player.sendMessage(component);
            playSound(player, type);
        }
    }

    /**
     * Send a title notification to all players
     */
    public void broadcastTitle(String title, String subtitle, NotificationType type) {
        broadcastTitle(title, subtitle, type, Bukkit.getOnlinePlayers());
    }

    /**
     * Send a title notification to specific players
     */
    public void broadcastTitle(String title, String subtitle, NotificationType type, Collection<? extends Player> players) {
        Component titleComponent = Component.text(title, getColor(type), TextDecoration.BOLD);
        Component subtitleComponent = Component.text(subtitle, NamedTextColor.GRAY);
        
        Title titleObj = Title.title(
                titleComponent,
                subtitleComponent,
                Title.Times.times(
                        Duration.ofMillis(500),
                        Duration.ofSeconds(3),
                        Duration.ofSeconds(1)
                )
        );

        for (Player player : players) {
            player.showTitle(titleObj);
            playSound(player, type);
        }
    }

    /**
     * Send an action bar notification to all players
     */
    public void broadcastActionBar(String message, NotificationType type) {
        broadcastActionBar(message, type, Bukkit.getOnlinePlayers());
    }

    /**
     * Send an action bar notification to specific players
     */
    public void broadcastActionBar(String message, NotificationType type, Collection<? extends Player> players) {
        Component component = Component.text(message, getColor(type));
        
        for (Player player : players) {
            player.sendActionBar(component);
        }
    }

    /**
     * Send a notification to a single player
     */
    public void sendNotification(Player player, String message, NotificationType type) {
        Component component = formatMessage(message, type);
        player.sendMessage(component);
        playSound(player, type);
    }

    /**
     * Send a title to a single player
     */
    public void sendTitle(Player player, String title, String subtitle, NotificationType type) {
        Component titleComponent = Component.text(title, getColor(type), TextDecoration.BOLD);
        Component subtitleComponent = Component.text(subtitle, NamedTextColor.GRAY);
        
        Title titleObj = Title.title(
                titleComponent,
                subtitleComponent,
                Title.Times.times(
                        Duration.ofMillis(500),
                        Duration.ofSeconds(3),
                        Duration.ofSeconds(1)
                )
        );

        player.showTitle(titleObj);
        playSound(player, type);
    }

    /**
     * Format message with prefix and color based on type
     */
    private Component formatMessage(String message, NotificationType type) {
        Component prefix = Component.text("[" + type.getPrefix() + "] ", getColor(type), TextDecoration.BOLD);
        Component msg = Component.text(message, NamedTextColor.WHITE);
        return Component.text().append(prefix).append(msg).build();
    }

    /**
     * Get color for notification type
     */
    private NamedTextColor getColor(NotificationType type) {
        return switch (type) {
            case INFO -> NamedTextColor.AQUA;
            case SUCCESS -> NamedTextColor.GREEN;
            case WARNING -> NamedTextColor.YELLOW;
            case ERROR -> NamedTextColor.RED;
            case CLAN -> NamedTextColor.GOLD;
            case ECONOMY -> NamedTextColor.YELLOW;
            case EVENT -> NamedTextColor.LIGHT_PURPLE;
        };
    }

    /**
     * Play sound for notification type
     */
    private void playSound(Player player, NotificationType type) {
        Sound sound = switch (type) {
            case INFO -> Sound.BLOCK_NOTE_BLOCK_PLING;
            case SUCCESS -> Sound.ENTITY_PLAYER_LEVELUP;
            case WARNING -> Sound.BLOCK_NOTE_BLOCK_BASS;
            case ERROR -> Sound.ENTITY_VILLAGER_NO;
            case CLAN -> Sound.BLOCK_ANVIL_LAND;
            case ECONOMY -> Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
            case EVENT -> Sound.ENTITY_ENDER_DRAGON_GROWL;
        };

        player.playSound(player.getLocation(), sound, 0.5f, 1.0f);
    }

    /**
     * Notification types
     */
    public enum NotificationType {
        INFO("INFO"),
        SUCCESS("✓"),
        WARNING("⚠"),
        ERROR("✗"),
        CLAN("CLAN"),
        ECONOMY("$"),
        EVENT("EVENT");

        private final String prefix;

        NotificationType(String prefix) {
            this.prefix = prefix;
        }

        public String getPrefix() {
            return prefix;
        }
    }
}
