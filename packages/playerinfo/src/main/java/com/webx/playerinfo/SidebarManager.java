package com.webx.playerinfo;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SidebarManager {

    private final Map<UUID, Scoreboard> scoreboards = new HashMap<>();
    private final ScoreboardManager scoreboardManager;
    private final String title;

    public SidebarManager(PlayerInfoPlugin plugin) {
        this.scoreboardManager = Bukkit.getScoreboardManager();
        this.title = ChatColor.YELLOW + "SafeZone";
        if (this.scoreboardManager == null) {
            plugin.getLogger().warning("ScoreboardManager is null; sidebar will be disabled.");
        }
    }

    public void ensureSidebar(Player player) {
        if (scoreboardManager == null) return;
        if (scoreboards.containsKey(player.getUniqueId())) return;

        Scoreboard board = scoreboardManager.getNewScoreboard();
        Objective objective = board.registerNewObjective("playerinfo", "dummy", title);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        // Static lines with team-based suffixes for dynamic values
        int score = 10;
        addLine(board, objective, score--, ChatColor.GOLD + "Игрок:", "name");
        addLine(board, objective, score--, ChatColor.YELLOW + "Уровень:", "level");
        addLine(board, objective, score--, ChatColor.GREEN + "Coins:", "coins");
        addLine(board, objective, score--, ChatColor.DARK_AQUA + "Bank:", "bank");
        addLine(board, objective, score--, ChatColor.BLUE + "Total:", "total");
        addLine(board, objective, score--, ChatColor.AQUA + "Здоровье:", "health");
        addLine(board, objective, score--, ChatColor.BLUE + "Голод:", "food");
        addLine(board, objective, score--, ChatColor.LIGHT_PURPLE + "Пинг:", "ping");
        addLine(board, objective, score--, ChatColor.GRAY + "Онлайн:", "online");
        addLine(board, objective, score, ChatColor.DARK_GRAY + "Мир:", "world");

        player.setScoreboard(board);
        scoreboards.put(player.getUniqueId(), board);
    }

    private void addLine(Scoreboard board, Objective obj, int score, String label, String teamName) {
        Team team = board.registerNewTeam(teamName);
        String entry = ChatColor.values()[score % ChatColor.values().length].toString() + ChatColor.RESET;
        team.addEntry(entry);
        team.prefix(Component.text(label + " "));
        team.suffix(Component.text(ChatColor.WHITE + "..."));
        obj.getScore(entry).setScore(score);
    }

    public void updateSidebar(Player player, SidebarValues values) {
        Scoreboard board = scoreboards.get(player.getUniqueId());
        if (board == null) return;

        setSuffix(board, "name", ChatColor.WHITE + values.playerName);
        setSuffix(board, "level", ChatColor.AQUA + String.valueOf(values.level));
        setSuffix(board, "coins", ChatColor.GOLD + values.coins);
        setSuffix(board, "bank", ChatColor.DARK_AQUA + values.bank);
        setSuffix(board, "total", ChatColor.BLUE + values.total);
        setSuffix(board, "health", ChatColor.RED + values.health);
        setSuffix(board, "food", ChatColor.GREEN + values.food);
        setSuffix(board, "ping", ChatColor.YELLOW + String.valueOf(values.ping) + " ms");
        setSuffix(board, "online", ChatColor.GREEN + String.valueOf(values.online));
        setSuffix(board, "world", ChatColor.WHITE + values.world);
    }

    private void setSuffix(Scoreboard board, String teamName, String value) {
        Team team = board.getTeam(teamName);
        if (team != null) {
            team.suffix(Component.text(value));
        }
    }

    public static class SidebarValues {
        public String playerName;
        public int level;
        public String coins;
        public String bank;
        public String total;
        public String health;
        public String food;
        public int ping;
        public int online;
        public String world;
    }
}
