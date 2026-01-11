package com.webx.backtp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpaCommands implements CommandExecutor {
    private final BackTpPlugin plugin;
    private final TpaManager tpaManager;

    public TpaCommands(BackTpPlugin plugin, TpaManager tpaManager) {
        this.plugin = plugin;
        this.tpaManager = tpaManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Только игроки могут использовать эту команду!");
            return true;
        }

        if (!plugin.getConfig().getBoolean("tpa.enabled", true)) {
            player.sendMessage(ChatColor.RED + "TPA система отключена!");
            return true;
        }

        switch (command.getName().toLowerCase()) {
            case "tpa", "tpahere" -> {
                if (args.length != 1) {
                    player.sendMessage(ChatColor.RED + "Использование: /" + command.getName() + " <игрок>");
                    return true;
                }
                
                Player target = Bukkit.getPlayer(args[0]);
                if (target == null || !target.isOnline()) {
                    String msg = plugin.getConfig().getString("messages.tpa.player-not-found", 
                        "&cИгрок не найден!");
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                    return true;
                }
                
                if (target.equals(player)) {
                    String msg = plugin.getConfig().getString("messages.tpa.cant-tp-self", 
                        "&cВы не можете отправить запрос самому себе!");
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                    return true;
                }
                
                boolean isHere = command.getName().equalsIgnoreCase("tpahere");
                tpaManager.sendRequest(player, target, isHere);
                return true;
            }
            case "tpaccept" -> {
                if (!tpaManager.hasRequest(player)) {
                    String msg = plugin.getConfig().getString("messages.tpa.no-pending", 
                        "&cУ вас нет ожидающих запросов!");
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                    return true;
                }
                tpaManager.acceptRequest(player);
                return true;
            }
            case "tpdeny" -> {
                if (!tpaManager.hasRequest(player)) {
                    String msg = plugin.getConfig().getString("messages.tpa.no-pending", 
                        "&cУ вас нет ожидающих запросов!");
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                    return true;
                }
                tpaManager.denyRequest(player);
                return true;
            }
        }
        
        return false;
    }
}
