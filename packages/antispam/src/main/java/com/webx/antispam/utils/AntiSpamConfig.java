package com.webx.antispam.utils;

public class AntiSpamConfig {
    private final int maxMessages;
    private final long timeWindow;
    private final int warningCount;
    
    public AntiSpamConfig(int maxMessages, long timeWindow, int warningCount) {
        this.maxMessages = maxMessages;
        this.timeWindow = timeWindow;
        this.warningCount = warningCount;
    }
    
    public int getMaxMessages() { return maxMessages; }
    public long getTimeWindow() { return timeWindow; }
    public int getWarningCount() { return warningCount; }
}
