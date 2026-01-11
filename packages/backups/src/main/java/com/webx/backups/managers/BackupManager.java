package com.webx.backups.managers;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public class BackupManager {
    private final File backupDir;
    private final List<String> backups = new ArrayList<>();
    
    public BackupManager(File backupDir) {
        this.backupDir = backupDir;
    }
    
    public void createBackup(String name) {
        backups.add(name + "_" + LocalDateTime.now());
    }
    
    public List<String> listBackups() {
        return new ArrayList<>(backups);
    }
}
