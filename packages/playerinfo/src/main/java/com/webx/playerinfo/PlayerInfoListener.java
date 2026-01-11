package com.webx.playerinfo;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class PlayerInfoListener implements Listener {
    
    private final EconomyDataManager economyDataManager;
    
    public PlayerInfoListener(EconomyDataManager economyDataManager) {
        this.economyDataManager = economyDataManager;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.sendMessage(
            Component.text()
                .append(Component.text("💰 PlayerInfo", NamedTextColor.GOLD, TextDecoration.BOLD))
                .append(Component.text(" - Displaying your stats in action bar!", NamedTextColor.GRAY))
                .build()
        );
    }
    
    public void updateAllPlayersInfo() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updatePlayerInfo(player);
        }
    }
    
    private void updatePlayerInfo(Player player) {
        // Get player stats
        String playerName = player.getName();
        int level = player.getLevel();
        double health = Math.round(player.getHealth() * 10.0) / 10.0;
        int maxHealth = (int) player.getMaxHealth();
        int food = player.getFoodLevel();
        
        // Get money from Economy plugin
        double money = economyDataManager.getBalance(player);
        
        // Action Bar - Updated every tick with colorful formatting
        Component actionBar = Component.text()
            .append(Component.text("💰 ", NamedTextColor.YELLOW))
            .append(Component.text(String.format("$%.2f", money), NamedTextColor.GOLD, TextDecoration.BOLD))
            .append(Component.text(" | ", NamedTextColor.DARK_GRAY))
            .append(Component.text("⭐ ", NamedTextColor.YELLOW))
            .append(Component.text("Lv" + level, NamedTextColor.AQUA, TextDecoration.BOLD))
            .append(Component.text(" | ", NamedTextColor.DARK_GRAY))
            .append(Component.text("❤️ ", NamedTextColor.RED))
            .append(Component.text(health + "/" + maxHealth, NamedTextColor.RED))
            .append(Component.text(" | ", NamedTextColor.DARK_GRAY))
            .append(Component.text("🍗 ", NamedTextColor.GOLD))
            .append(Component.text(food + "/20", NamedTextColor.GOLD))
            .build();
        
        player.sendActionBar(actionBar);
    }
}
