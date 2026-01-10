package com.webx.backups.commands;

import com.webx.backups.BackupsPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BackupCommand implements CommandExecutor {
    private final BackupsPlugin plugin;
    
    public BackupCommand(BackupsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("backup.admin")) {
            sender.sendMessage("§cNo permission!");
            return true;
        }
        
        sender.sendMessage("§aBackup process started...");
        plugin.getBackupManager().createBackup("world");
        sender.sendMessage("§aBackup completed!");
        
        return true;
    }
}
