package com.webx.antispam.managers;

import com.webx.antispam.models.SpamViolation;
import org.bukkit.entity.Player;
import java.util.*;

public class SpamManager {
    private final Map<UUID, List<Long>> chatTimestamps = new HashMap<>();
    private final Map<UUID, Integer> violations = new HashMap<>();
    private final int maxMessages;
    private final long timeWindow;
    
    public SpamManager(int maxMessages, long timeWindow) {
        this.maxMessages = maxMessages;
        this.timeWindow = timeWindow;
    }
    
    public boolean isSpamming(UUID uuid) {
        List<Long> timestamps = chatTimestamps.computeIfAbsent(uuid, k -> new ArrayList<>());
        long now = System.currentTimeMillis();
        timestamps.removeIf(ts -> now - ts > timeWindow);
        
        if (timestamps.size() >= maxMessages) {
            return true;
        }
        
        timestamps.add(now);
        return false;
    }
    
    public void addViolation(UUID uuid) {
        violations.put(uuid, violations.getOrDefault(uuid, 0) + 1);
    }
    
    public int getViolationCount(UUID uuid) {
        return violations.getOrDefault(uuid, 0);
    }
}
