package com.webx.backups.utils;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BackupUtils {
    
    public static String formatBackupName(String world) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        return world + "_" + now.format(formatter);
    }
    
    public static long getBackupSize(File backupDir) {
        return backupDir.length();
    }
}
