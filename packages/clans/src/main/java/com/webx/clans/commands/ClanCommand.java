package com.webx.clans.commands;

import com.webx.clans.ClansPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClanCommand implements CommandExecutor {
    private final ClansPlugin plugin;

    public ClanCommand(ClansPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§cUsage: /clan <create|disband|invite|kick|rank|info|list|territory>");
            return true;
        }

        String action = args[0].toLowerCase();

        switch (action) {
            case "create" -> {
                if (args.length < 2) {
                    player.sendMessage("§cUsage: /clan create <name>");
                    return true;
                }
                String name = args[1];
                var clan = plugin.getClanManager().createClan(name, player.getUniqueId());
                if (clan != null) {
                    player.sendMessage("§aCreated clan: §6" + name);
                } else {
                    player.sendMessage("§cClan already exists!");
                }
            }
            case "info" -> {
                var clan = plugin.getClanManager().getClanByMember(player.getUniqueId());
                if (clan == null) {
                    player.sendMessage("§cYou are not in a clan!");
                    return true;
                }
                player.sendMessage("§6§lClan: " + clan.getName());
                player.sendMessage("§7Members: §f" + clan.getMemberCount());
                player.sendMessage("§7Level: §f" + clan.getLevel());
            }
            case "list" -> {
                player.sendMessage("§6§lClans:");
                plugin.getClanManager().getAllClans().forEach(clan -> {
                    player.sendMessage("§e  - §6" + clan.getName() + " §7(Level " + clan.getLevel() + ", " + clan.getMemberCount() + " members)");
                });
            }
            default -> player.sendMessage("§cUsage: /clan <create|disband|invite|kick|rank|info|list|territory>");
        }

        return true;
    }
}
