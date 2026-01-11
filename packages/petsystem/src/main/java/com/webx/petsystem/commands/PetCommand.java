package com.webx.petsystem.commands;

import com.webx.petsystem.PetSystemPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PetCommand implements CommandExecutor {
    private final PetSystemPlugin plugin;
    
    public PetCommand(PetSystemPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        
        Player player = (Player) sender;
        var pets = plugin.getPetManager().getPets(player);
        
        player.sendMessage("§6=== Your Pets ===");
        for (var pet : pets) {
            player.sendMessage("§f" + pet.getName() + " §7(Level " + pet.getLevel() + ")");
        }
        
        return true;
    }
}
