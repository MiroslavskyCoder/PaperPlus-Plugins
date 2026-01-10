package com.webx.mounts;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MountsCommand implements CommandExecutor {
    private MountsManager manager;
    
    public MountsCommand(MountsManager manager) {
        this.manager = manager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        
        if (args.length == 0) {
            String current = manager.getMount(player);
            player.sendMessage("§6Current mount: §f" + current);
            return true;
        }
        
        if (args[0].equalsIgnoreCase("set")) {
            manager.setMount(player, args.length > 1 ? args[1] : "horse");
        }
        
        if (args[0].equalsIgnoreCase("summon")) {
            manager.summonMount(player);
        }
        
        return true;
    }
}
