package com.webx.dashboard.config.models;

import java.util.Arrays;
import java.util.List;

public class AutoShutdownConfig {
    public boolean enabled = true;
    public int timeout = 10;
    public List<Integer> warnings = Arrays.asList(5, 3, 1);
    public String shutdownMessage = "§cServer shutting down due to inactivity...";
    
    public AutoShutdownConfig() {}
}
