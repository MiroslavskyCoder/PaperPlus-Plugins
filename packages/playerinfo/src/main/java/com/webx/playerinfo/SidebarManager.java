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
        int score = 15;
        addLine(board, objective, score--, ChatColor.GOLD + "Игрок:", "name");
        addLine(board, objective, score--, ChatColor.YELLOW + "Уровень:", "level");
        addLine(board, objective, score--, ChatColor.GREEN + "Coins:", "coins");
        addLine(board, objective, score--, ChatColor.DARK_AQUA + "Bank:", "bank");
        addLine(board, objective, score--, ChatColor.BLUE + "Total:", "total");
        addLine(board, objective, score--, ChatColor.DARK_PURPLE + "Clan:", "clan");
        addLine(board, objective, score--, ChatColor.AQUA + "Rank:", "rank");
        addLine(board, objective, score--, ChatColor.GREEN + "Skill:", "skill");
        addLine(board, objective, score--, ChatColor.LIGHT_PURPLE + "Quest:", "quest");
        addLine(board, objective, score--, ChatColor.GOLD + "Job:", "job");
        addLine(board, objective, score--, ChatColor.DARK_GREEN + "Market:", "market");
        addLine(board, objective, score--, ChatColor.DARK_AQUA + "Marketpl.:", "marketplace");
        addLine(board, objective, score, ChatColor.RED + "Feed:", "feed");

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
        setSuffix(board, "clan", ChatColor.LIGHT_PURPLE + values.clan);
        setSuffix(board, "rank", ChatColor.AQUA + values.rank);
        setSuffix(board, "skill", ChatColor.GREEN + values.skill);
        setSuffix(board, "quest", ChatColor.LIGHT_PURPLE + values.quest);
        setSuffix(board, "job", ChatColor.GOLD + values.job);
        setSuffix(board, "market", ChatColor.DARK_GREEN + values.market);
        setSuffix(board, "marketplace", ChatColor.DARK_AQUA + values.marketplace);
        setSuffix(board, "feed", ChatColor.RED + values.feed);
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
        public String clan;
        public String rank;
        public String skill;
        public String quest;
        public String job;
        public String market;
        public String marketplace;
        public String feed;
    }
}
