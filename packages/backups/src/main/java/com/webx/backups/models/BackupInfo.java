package com.webx.backups.models;

import java.time.LocalDateTime;

public class BackupInfo {
    private final String name;
    private final long size;
    private final LocalDateTime createdAt;
    
    public BackupInfo(String name, long size) {
        this.name = name;
        this.size = size;
        this.createdAt = LocalDateTime.now();
    }
    
    public String getName() { return name; }
    public long getSize() { return size; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getSizeFormat() { return formatBytes(size); }
    
    private String formatBytes(long bytes) {
        if (bytes <= 0) return "0 B";
        final String[] units = new String[] {"B", "KB", "MB", "GB"};
        int digitGroups = (int) (Math.log10(bytes) / Math.log10(1024));
        return String.format("%.1f %s", bytes / Math.pow(1024, digitGroups), units[digitGroups]);
    }
}
