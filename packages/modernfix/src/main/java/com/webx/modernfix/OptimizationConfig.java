package com.webx.modernfix;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * Configuration manager for ModernFix optimizations
 */
public class OptimizationConfig {
    private final FileConfiguration config;

    public OptimizationConfig(FileConfiguration config) {
        this.config = config;
    }

    // Entity optimization settings
    public boolean isEntityOptimization() {
        return config.getBoolean("optimizations.entity.enabled", true);
    }

    public int getMaxItemLifetime() {
        return config.getInt("optimizations.entity.max-item-lifetime", 6000); // 5 minutes
    }

    public boolean isReduceEntityAI() {
        return config.getBoolean("optimizations.entity.reduce-ai", true);
    }

    // Chunk optimization settings
    public boolean isChunkOptimization() {
        return config.getBoolean("optimizations.chunk.enabled", true);
    }

    public boolean isReduceViewDistance() {
        return config.getBoolean("optimizations.chunk.reduce-view-distance", false);
    }

    public int getMaxViewDistance() {
        return config.getInt("optimizations.chunk.max-view-distance", 10);
    }

    public boolean isReduceSimulationDistance() {
        return config.getBoolean("optimizations.chunk.reduce-simulation-distance", false);
    }

    public int getMaxSimulationDistance() {
        return config.getInt("optimizations.chunk.max-simulation-distance", 8);
    }

    // Memory optimization settings
    public boolean isMemoryOptimization() {
        return config.getBoolean("optimizations.memory.enabled", true);
    }

    public double getMemoryThreshold() {
        return config.getDouble("optimizations.memory.gc-threshold", 85.0);
    }

    // Tick optimization settings
    public boolean isTickOptimization() {
        return config.getBoolean("optimizations.tick.enabled", true);
    }

    // Performance monitoring
    public boolean isPerformanceMonitoring() {
        return config.getBoolean("monitoring.enabled", true);
    }

    public int getMonitoringInterval() {
        return config.getInt("monitoring.interval", 300); // 15 seconds
    }

    // Debug settings
    public boolean isDebugMode() {
        return config.getBoolean("debug", false);
    }
}
