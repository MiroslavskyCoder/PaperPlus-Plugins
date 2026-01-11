package com.webx.playerinfo;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.ScoreboardManager;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
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
        addLine(board, objective, 8, ChatColor.GOLD + "Игрок:", "name");
        addLine(board, objective, 7, ChatColor.YELLOW + "Уровень:", "level");
        addLine(board, objective, 6, ChatColor.GREEN + "Баланс:", "money");
        addLine(board, objective, 5, ChatColor.AQUA + "Здоровье:", "health");
        addLine(board, objective, 4, ChatColor.BLUE + "Голод:", "food");
        addLine(board, objective, 3, ChatColor.LIGHT_PURPLE + "Пинг:", "ping");
        addLine(board, objective, 2, ChatColor.GRAY + "Онлайн:", "online");
        addLine(board, objective, 1, ChatColor.DARK_GRAY + "Мир:", "world");

        player.setScoreboard(board);
        scoreboards.put(player.getUniqueId(), board);
    }

    private void addLine(Scoreboard board, Objective obj, int score, String label, String teamName) {
        Team team = board.registerNewTeam(teamName);
        String entry = ChatColor.values()[score % ChatColor.values().length].toString() + ChatColor.RESET;
        team.addEntry(entry);
        team.prefix(label + " ");
        team.suffix(ChatColor.WHITE + "...");
        obj.getScore(entry).setScore(score);
    }

    public void updateSidebar(Player player, SidebarValues values) {
        Scoreboard board = scoreboards.get(player.getUniqueId());
        if (board == null) return;

        setSuffix(board, "name", ChatColor.WHITE + values.playerName);
        setSuffix(board, "level", ChatColor.AQUA + String.valueOf(values.level));
        setSuffix(board, "money", ChatColor.GOLD + values.money);
        setSuffix(board, "health", ChatColor.RED + values.health);
        setSuffix(board, "food", ChatColor.GREEN + values.food);
        setSuffix(board, "ping", ChatColor.YELLOW + values.ping + " ms");
        setSuffix(board, "online", ChatColor.GREEN + String.valueOf(values.online));
        setSuffix(board, "world", ChatColor.WHITE + values.world);
    }

    private void setSuffix(Scoreboard board, String teamName, String value) {
        Team team = board.getTeam(teamName);
        if (team != null) {
            team.suffix(value);
        }
    }

    public static class SidebarValues {
        public String playerName;
        public int level;
        public String money;
        public String health;
        public String food;
        public int ping;
        public int online;
        public String world;
    }
}
