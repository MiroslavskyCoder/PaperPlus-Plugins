package com.webx.abomination;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class AbominationPlugin extends JavaPlugin {
    private AbominationManager manager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        manager = new AbominationManager(this);
        getServer().getPluginManager().registerEvents(new AbominationListener(this), this);
        manager.startNaturalSpawns();
        getLogger().info("Abomination 0.1.0 enabled");
    }

    @Override
    public void onDisable() {
        if (manager != null) {
            manager.cleanup();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("abomination.admin")) {
            sender.sendMessage("No permission");
            return true;
        }
        String sub = args.length > 0 ? args[0].toLowerCase() : "status";
        switch (sub) {
            case "spawn":
                Player target = null;
                if (args.length > 1) {
                    target = getServer().getPlayerExact(args[1]);
                }
                if (target == null && sender instanceof Player) {
                    target = (Player) sender;
                }
                if (target == null) {
                    sender.sendMessage("Player not found");
                    return true;
                }
                manager.spawnAbominationNear(target.getLocation());
                sender.sendMessage("Spawned near " + target.getName());
                return true;
            case "killall":
                int killed = manager.removeAll();
                sender.sendMessage("Removed " + killed + " abominations.");
                return true;
            case "status":
            default:
                sender.sendMessage("Active abominations: " + manager.getActiveCount());
                return true;
        }
    }

    public AbominationManager getManager() {
        return manager;
    }
}
