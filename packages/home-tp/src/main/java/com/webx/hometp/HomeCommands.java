package com.webx.hometp;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

public class HomeCommands implements CommandExecutor {
    private final HomeTpPlugin plugin;
    private final HomeManager homeManager;
    private final TeleportManager teleportManager;

    public HomeCommands(HomeTpPlugin plugin, HomeManager homeManager, TeleportManager teleportManager) {
        this.plugin = plugin;
        this.homeManager = homeManager;
        this.teleportManager = teleportManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Только игроки могут использовать эту команду!");
            return true;
        }

        switch (command.getName().toLowerCase()) {
            case "sethome" -> {
                if (args.length != 1) {
                    player.sendMessage(ChatColor.RED + "Использование: /sethome <название>");
                    return true;
                }
                String homeName = args[0];
                if (homeManager.setHome(player, homeName)) {
                    String msg = plugin.getConfig().getString("messages.home-set", "&aДом '{name}' установлен!");
                    msg = msg.replace("{name}", homeName);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                } else {
                    int maxHomes = plugin.getConfig().getInt("max-homes", 5);
                    String msg = plugin.getConfig().getString("messages.max-homes-reached", "&cВы достигли лимита домов ({max})!");
                    msg = msg.replace("{max}", String.valueOf(maxHomes));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                }
                return true;
            }
            case "delhome" -> {
                if (args.length != 1) {
                    player.sendMessage(ChatColor.RED + "Использование: /delhome <название>");
                    return true;
                }
                String homeName = args[0];
                if (homeManager.deleteHome(player, homeName)) {
                    String msg = plugin.getConfig().getString("messages.home-deleted", "&cДом '{name}' удален!");
                    msg = msg.replace("{name}", homeName);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                } else {
                    String msg = plugin.getConfig().getString("messages.home-not-found", "&cДом '{name}' не найден!");
                    msg = msg.replace("{name}", homeName);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                }
                return true;
            }
            case "home" -> {
                if (args.length != 1) {
                    player.sendMessage(ChatColor.RED + "Использование: /home <название>");
                    return true;
                }
                String homeName = args[0];
                Location home = homeManager.getHome(player, homeName);
                if (home != null) {
                    teleportManager.teleport(player, home, homeName);
                } else {
                    String msg = plugin.getConfig().getString("messages.home-not-found", "&cДом '{name}' не найден!");
                    msg = msg.replace("{name}", homeName);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                }
                return true;
            }
            case "homes" -> {
                Set<String> homes = homeManager.getHomes(player);
                if (homes.isEmpty()) {
                    String msg = plugin.getConfig().getString("messages.no-homes", "&cУ вас нет установленных домов!");
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                } else {
                    String homesList = String.join(", ", homes);
                    String msg = plugin.getConfig().getString("messages.homes-list", "&aВаши дома: &e{homes}");
                    msg = msg.replace("{homes}", homesList);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                }
                return true;
            }
        }
        return false;
    }
}
