package com.webx.feed;

import com.webx.feed.managers.FeedManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class FeedPlugin extends JavaPlugin implements CommandExecutor {
    private static FeedPlugin instance;
    private FeedManager feedManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        feedManager = new FeedManager(getConfig().getInt("hunger-restore", 20));
        getCommand("feed").setExecutor(this);
        getCommand("heal").setExecutor(this);
        getLogger().info("Feed Plugin enabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (command.getName().equalsIgnoreCase("feed")) {
            return handleFeedCommand(player, args);
        } else if (command.getName().equalsIgnoreCase("heal")) {
            return handleHealCommand(player, args);
        }
        
        return false;
    }
    
    private boolean handleFeedCommand(Player player, String[] args) {
        Player target = player;
        
        if (args.length > 0) {
            if (!player.hasPermission("feed.others")) {
                player.sendMessage("§cYou don't have permission to feed others!");
                return true;
            }
            target = getServer().getPlayer(args[0]);
            if (target == null) {
                player.sendMessage("§cPlayer not found!");
                return true;
            }
        }
        
        target.setFoodLevel(20);
        target.setSaturation(20);
        
        if (target != player) {
            player.sendMessage("§a" + target.getName() + " has been fed!");
            target.sendMessage("§aYou were fed by " + player.getName());
        } else {
            player.sendMessage("§aYou have been fed!");
        }
        
        return true;
    }
    
    private boolean handleHealCommand(Player player, String[] args) {
        Player target = player;
        
        if (args.length > 0) {
            if (!player.hasPermission("feed.heal.others")) {
                player.sendMessage("§cYou don't have permission to heal others!");
                return true;
            }
            target = getServer().getPlayer(args[0]);
            if (target == null) {
                player.sendMessage("§cPlayer not found!");
                return true;
            }
        }
        
        target.setHealth(target.getMaxHealth());
        target.setFireTicks(0);
        
        if (target != player) {
            player.sendMessage("§a" + target.getName() + " has been healed!");
            target.sendMessage("§aYou were healed by " + player.getName());
        } else {
            player.sendMessage("§aYou have been healed!");
        }
        
        return true;
    }
    
    public static FeedPlugin getInstance() {
        return instance;
    }
    
    public FeedManager getFeedManager() {
        return feedManager;
    }
}
