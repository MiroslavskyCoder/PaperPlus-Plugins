package com.webx.dance;

public class DanceInfo {
    private final String name;
    private final String displayName;
    private final int duration;
    private final String description;

    public DanceInfo(String name, String displayName, int duration, String description) {
        this.name = name;
        this.displayName = displayName;
        this.duration = duration;
        this.description = description;
    }

    public String getName() { return name; }
    public String getDisplayName() { return displayName; }
    public int getDuration() { return duration; }
    public String getDescription() { return description; }
}
