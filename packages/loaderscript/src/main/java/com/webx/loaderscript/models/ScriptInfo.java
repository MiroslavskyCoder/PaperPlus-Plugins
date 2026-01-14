package com.webx.loaderscript.models;

/**
 * Information about a loaded script
 */
public class ScriptInfo {
    private final String name;
    private final String path;
    private final long size;
    private final long lastModified;
    private final long loadedAt;
    private final boolean success;
    private final String error;
    
    public ScriptInfo(String name, String path, long size, long lastModified, 
                     long loadedAt, boolean success, String error) {
        this.name = name;
        this.path = path;
        this.size = size;
        this.lastModified = lastModified;
        this.loadedAt = loadedAt;
        this.success = success;
        this.error = error;
    }
    
    public String getName() {
        return name;
    }
    
    public String getPath() {
        return path;
    }
    
    public long getSize() {
        return size;
    }
    
    public long getLastModified() {
        return lastModified;
    }
    
    public long getLoadedAt() {
        return loadedAt;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public String getError() {
        return error;
    }
}
