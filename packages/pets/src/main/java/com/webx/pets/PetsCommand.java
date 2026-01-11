package com.webx.pets;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PetsCommand implements CommandExecutor {
    private PetsManager manager;
    
    public PetsCommand(PetsManager manager) {
        this.manager = manager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        
        if (args.length == 0) {
            sender.sendMessage("§6Your Pets:");
            manager.getPets(player).forEach(pet -> 
                sender.sendMessage("§b" + pet.getName() + " §7(§f" + pet.getType() + "§7) - Level " + pet.getLevel())
            );
            return true;
        }
        
        if (args[0].equalsIgnoreCase("adopt")) {
            manager.addPet(player, new PetsManager.Pet(args.length > 1 ? args[1] : "Fluffy", "dog"));
        }
        
        return true;
    }
}
