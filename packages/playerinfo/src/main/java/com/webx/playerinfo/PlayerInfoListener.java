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
    private final SidebarManager sidebarManager;
    
    public PlayerInfoListener(EconomyDataManager economyDataManager, SidebarManager sidebarManager) {
        this.economyDataManager = economyDataManager;
        this.sidebarManager = sidebarManager;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        sidebarManager.ensureSidebar(player);
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
        int ping = player.getPing();
        int online = Bukkit.getOnlinePlayers().size();
        String world = player.getWorld().getName();
        
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

        // Sidebar update
        sidebarManager.ensureSidebar(player);
        SidebarManager.SidebarValues values = new SidebarManager.SidebarValues();
        values.playerName = playerName;
        values.level = level;
        values.money = String.format("$%.2f", money);
        values.health = health + "/" + maxHealth;
        values.food = food + "/20";
        values.ping = ping;
        values.online = online;
        values.world = world;
        sidebarManager.updateSidebar(player, values);
    }
}
