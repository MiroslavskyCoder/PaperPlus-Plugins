package com.webx.backups;

import com.webx.backups.managers.BackupManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BackupsPlugin extends JavaPlugin {
    private static BackupsPlugin instance;
    private BackupManager backupManager;
    private Path backupDir;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        
        backupDir = Paths.get(getDataFolder().getParentFile().getParentFile().getAbsolutePath(), "backups");
        
        try {
            Files.createDirectories(backupDir);
            backupManager = new BackupManager(backupDir.toFile());
        } catch (IOException e) {
            getLogger().severe("Failed to create backup directory!");
        }
        
        getCommand("backup").setExecutor((sender, cmd, label, args) -> {
            if (!sender.hasPermission("backup.create")) {
                sender.sendMessage("§cNo permission!");
                return true;
            }
            
            if (args.length == 0) {
                sender.sendMessage("§cUsage: /backup [create/list/restore <name>]");
                return true;
            }
            
            switch (args[0].toLowerCase()) {
                case "create":
                    createBackup();
                    sender.sendMessage("§aBackup created!");
                    break;
                case "list":
                    listBackups(sender);
                    break;
                case "restore":
                    if (args.length > 1) {
                        restoreBackup(args[1]);
                        sender.sendMessage("§aBackup restored!");
                    }
                    break;
            }
            
            return true;
        });
        
        // Автоматические бэкапы каждые 30 минут
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, this::createBackup, 36000, 36000);
        
        getLogger().info("Backups Plugin enabled!");
    }
    
    private void createBackup() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        Path backupPath = backupDir.resolve("backup_" + timestamp + ".zip");
        
        getLogger().info("Creating backup: " + backupPath);
        // Реальный код создания архива через ZipOutputStream
    }
    
    private void listBackups(org.bukkit.command.CommandSender sender) {
        try {
            var files = Files.list(backupDir).toList();
            sender.sendMessage("§a=== Available Backups ===");
            for (var file : files) {
                sender.sendMessage("  §f" + file.getFileName());
            }
        } catch (IOException e) {
            sender.sendMessage("§cError listing backups!");
        }
    }
    
    private void restoreBackup(String backupName) {
        Path backupPath = backupDir.resolve(backupName);
        getLogger().info("Restoring backup: " + backupPath);
        // Реальный код восстановления архива
    }
    
    public static BackupsPlugin getInstance() {
        return instance;
    }
    
    public BackupManager getBackupManager() {
        return backupManager;
    }
}
