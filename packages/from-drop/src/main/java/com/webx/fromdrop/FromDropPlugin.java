package com.webx.fromdrop;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class FromDropPlugin extends JavaPlugin {
    private DropConfig dropConfig;

    @Override
    public void onEnable() {
        dropConfig = new DropConfig();
        dropConfig.load(this);
        getServer().getPluginManager().registerEvents(new DropListener(dropConfig), this);
        getLogger().info("FromDrop 0.1.2 enabled");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("fromdrop.admin")) {
            sender.sendMessage("No permission");
            return true;
        }
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            dropConfig.load(this);
            sender.sendMessage("FromDrop config reloaded.");
            return true;
        }
        sender.sendMessage("Usage: /fromdrop reload");
        return true;
    }
}
