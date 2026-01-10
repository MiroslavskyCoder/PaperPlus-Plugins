package com.webx.speedrun.managers;

import java.time.LocalDateTime;
import java.util.*;

public class SpeedRunManager {
    private final Map<String, LocalDateTime> runTimes = new HashMap<>();
    
    public void recordRunTime(String playerName) {
        runTimes.put(playerName, LocalDateTime.now());
    }
    
    public LocalDateTime getRunTime(String playerName) {
        return runTimes.get(playerName);
    }
}
