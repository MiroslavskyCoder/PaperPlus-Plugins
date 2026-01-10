package com.webx.statistics.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class LeaderboardManager {
    
    public static void displayLeaderboard(Player player) {
        player.sendMessage("§6=== Top Players (K/D Ratio) ===");
        player.sendMessage("§f1. Player1: §c10.5");
        player.sendMessage("§f2. Player2: §c9.2");
        player.sendMessage("§f3. Player3: §c8.8");
    }
}
