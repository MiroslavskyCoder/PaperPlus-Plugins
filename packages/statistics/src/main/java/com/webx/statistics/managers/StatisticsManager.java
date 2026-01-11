package com.webx.statistics.managers;

import com.webx.statistics.models.PlayerStats;
import java.util.*;

public class StatisticsManager {
    private final Map<UUID, PlayerStats> stats = new HashMap<>();
    
    public void recordKill(UUID killer, UUID victim) {
        stats.computeIfAbsent(killer, k -> new PlayerStats(killer)).addKill();
    }
    
    public void recordDeath(UUID victim) {
        stats.computeIfAbsent(victim, k -> new PlayerStats(victim)).addDeath();
    }
    
    public PlayerStats getStats(UUID uuid) {
        return stats.computeIfAbsent(uuid, k -> new PlayerStats(uuid));
    }
}
