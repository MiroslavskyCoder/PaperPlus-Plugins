package com.webx.customenchants.commands;

import com.webx.customenchants.CustomEnchantsPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class EnchantCommand implements CommandExecutor {
    private final CustomEnchantsPlugin plugin;
    
    public EnchantCommand(CustomEnchantsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("§6=== Custom Enchants ===");
        for (var enchant : plugin.getEnchantManager().getAllEnchants()) {
            sender.sendMessage("§f" + enchant.getName() + " §7(Max Level: " + enchant.getMaxLevel() + ")");
        }
        return true;
    }
}
