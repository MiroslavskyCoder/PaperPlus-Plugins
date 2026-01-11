package com.webx.achievements;

public class Achievement {
    private String id;
    private String displayName;
    private String description;
    
    public Achievement(String id, String displayName, String description) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
}
