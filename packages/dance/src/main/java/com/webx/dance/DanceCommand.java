package com.webx.dance;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DanceCommand implements CommandExecutor {
    private final DanceManager manager;

    public DanceCommand(DanceManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can dance");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0 || args[0].equalsIgnoreCase("list")) {
            listDances(player);
            return true;
        }

        String danceName = args[0];
        manager.performDance(player, danceName);
        return true;
    }

    private void listDances(Player player) {
        player.sendMessage(ChatColor.AQUA + "=== Available Dances ===");
        for (DanceInfo dance : manager.getAllDances()) {
            player.sendMessage(ChatColor.YELLOW + "/dance " + dance.getName() + ChatColor.RESET + " - " + dance.getDescription());
        }
    }
}
