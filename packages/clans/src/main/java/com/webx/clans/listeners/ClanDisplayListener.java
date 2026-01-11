package com.webx.clans.listeners;

import com.webx.clans.ClansPlugin;
import com.webx.clans.models.Clan;
import com.webx.clans.managers.ClanManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Handles clan tag display in chat and tab list
 */
public class ClanDisplayListener implements Listener {
    private final ClansPlugin plugin;
    private final ClanManager clanManager;

    public ClanDisplayListener(ClansPlugin plugin, ClanManager clanManager) {
        this.plugin = plugin;
        this.clanManager = clanManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Clan clan = clanManager.getClanByMember(player.getUniqueId());

        if (clan != null) {
            String format = event.getFormat();
            String clanTag = formatClanTag(clan, player);
            
            // Insert clan tag before player name
            format = format.replace("%1$s", clanTag + "%1$s");
            event.setFormat(format);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        updatePlayerDisplayName(player);
        updatePlayerTabName(player);
    }

    /**
     * Update player's display name with clan tag
     */
    public void updatePlayerDisplayName(Player player) {
        Clan clan = clanManager.getClanByMember(player.getUniqueId());
        
        if (clan != null) {
            Component displayName = Component.text()
                    .append(formatClanTagComponent(clan, player))
                    .append(Component.text(" "))
                    .append(Component.text(player.getName()))
                    .build();
            player.displayName(displayName);
        }
    }

    /**
     * Update player's tab list name with clan tag
     */
    public void updatePlayerTabName(Player player) {
        Clan clan = clanManager.getClanByMember(player.getUniqueId());
        
        if (clan != null) {
            Component tabName = Component.text()
                    .append(formatClanTagComponent(clan, player))
                    .append(Component.text(" "))
                    .append(Component.text(player.getName()))
                    .build();
            player.playerListName(tabName);
        } else {
            player.playerListName(Component.text(player.getName()));
        }
    }

    /**
     * Format clan tag for legacy string format
     */
    private String formatClanTag(Clan clan, Player player) {
        String tag = clan.getTag();
        String rolePrefix = getRolePrefix(clan, player);
        return "§6[" + tag + "]" + rolePrefix + "§r";
    }

    /**
     * Format clan tag as Component
     */
    private Component formatClanTagComponent(Clan clan, Player player) {
        String tag = clan.getTag();
        String rolePrefix = getRolePrefix(clan, player);
        
        return Component.text()
                .append(Component.text("[", NamedTextColor.GOLD, TextDecoration.BOLD))
                .append(Component.text(tag, NamedTextColor.GOLD, TextDecoration.BOLD))
                .append(Component.text("]", NamedTextColor.GOLD, TextDecoration.BOLD))
                .append(Component.text(rolePrefix))
                .build();
    }

    /**
     * Get role prefix based on player's rank in clan
     */
    private String getRolePrefix(Clan clan, Player player) {
        String rank = clan.getMemberRank(player.getUniqueId());
        
        if (rank == null) {
            return "";
        }

        return switch (rank) {
            case "LEADER" -> "§c★§r";
            case "DEPUTY" -> "§e⚡§r";
            default -> "";
        };
    }
}
