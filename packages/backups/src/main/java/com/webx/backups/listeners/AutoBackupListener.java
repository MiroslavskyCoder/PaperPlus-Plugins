package com.webx.backups.listeners;

import com.webx.backups.BackupsPlugin;
import org.bukkit.event.Listener;

public class AutoBackupListener implements Listener {
    private final BackupsPlugin plugin;
    
    public AutoBackupListener(BackupsPlugin plugin) {
        this.plugin = plugin;
    }
}
