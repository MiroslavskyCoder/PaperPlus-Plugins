package com.webx.pvpbase;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MatchCommand implements CommandExecutor {
    private final PvPBasePlugin plugin;

    public MatchCommand(PvPBasePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }
        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(ChatColor.YELLOW + "Usage: /match create <mode> | join <matchId> | list | leave");
            return true;
        }

        String sub = args[0].toLowerCase();
        switch (sub) {
            case "create":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /match create <SKYWARS|BEDWARS|DUELS|SIEGE>");
                    return true;
                }
                String modeName = args[1].toUpperCase();
                try {
                    GameMode mode = GameMode.valueOf(modeName);
                    World world = player.getWorld();
                    Match match = plugin.getMatchManager().createMatch(mode, world, world.getName());
                    player.sendMessage(ChatColor.GREEN + "Match created: " + mode.getDisplayName() + " (" + match.getId() + ")");
                } catch (IllegalArgumentException e) {
                    player.sendMessage(ChatColor.RED + "Unknown game mode: " + modeName);
                }
                return true;

            case "join":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /match join <matchId>");
                    return true;
                }
                try {
                    java.util.UUID matchId = java.util.UUID.fromString(args[1]);
                    Match match = plugin.getMatchManager().getMatch(matchId);
                    if (match == null) {
                        player.sendMessage(ChatColor.RED + "Match not found.");
                        return true;
                    }
                    // For now, just confirm join; would need class selection in full impl
                    player.sendMessage(ChatColor.GREEN + "Joined match: " + match.getGameMode().getDisplayName());
                } catch (IllegalArgumentException e) {
                    player.sendMessage(ChatColor.RED + "Invalid match ID.");
                }
                return true;

            case "list":
                java.util.List<Match> matches = plugin.getMatchManager().getActiveMatches();
                if (matches.isEmpty()) {
                    player.sendMessage(ChatColor.YELLOW + "No active matches.");
                } else {
                    player.sendMessage(ChatColor.GOLD + "Active Matches:");
                    for (Match m : matches) {
                        player.sendMessage(ChatColor.GRAY + "  " + m.getId() + " - " + m.getGameMode().getDisplayName() + 
                            " (" + m.getTotalPlayers() + " players, " + m.getState() + ")");
                    }
                }
                return true;

            case "leave":
                Match currentMatch = plugin.getMatchManager().getMatchByPlayer(player.getUniqueId());
                if (currentMatch == null) {
                    player.sendMessage(ChatColor.RED + "You are not in a match.");
                    return true;
                }
                Team team = currentMatch.getPlayerTeam(player.getUniqueId());
                if (team != null) {
                    team.removeMember(player.getUniqueId());
                    player.sendMessage(ChatColor.GREEN + "Left the match.");
                }
                return true;

            default:
                player.sendMessage(ChatColor.RED + "Unknown subcommand: " + sub);
                return true;
        }
    }
}
