package com.webx.hdphysicssound.config;

import org.bukkit.configuration.file.FileConfiguration;

public class PhysicsConfig {

    private final double maxDistance;
    private final double airAbsorptionPerMeter;
    private final int occlusionSteps;
    private final double occlusionPenalty;
    private final double minOcclusionVolume;
    private final double minAudibleVolume;

    private final boolean reverbEnabled;
    private final double reverbRoomScale;
    private final double reverbDecay;
    private final double reverbWetGain;

    private final boolean asyncEnabled;
    private final int maxPlayersPerTick;

    private final boolean debugEnabled;
    private final boolean logTraces;
    private final boolean visualizeOcclusion;

    public PhysicsConfig(double maxDistance, double airAbsorptionPerMeter, int occlusionSteps, double occlusionPenalty,
                         double minOcclusionVolume, double minAudibleVolume, boolean reverbEnabled, double reverbRoomScale,
                         double reverbDecay, double reverbWetGain, boolean asyncEnabled, int maxPlayersPerTick,
                         boolean debugEnabled, boolean logTraces, boolean visualizeOcclusion) {
        this.maxDistance = maxDistance;
        this.airAbsorptionPerMeter = airAbsorptionPerMeter;
        this.occlusionSteps = occlusionSteps;
        this.occlusionPenalty = occlusionPenalty;
        this.minOcclusionVolume = minOcclusionVolume;
        this.minAudibleVolume = minAudibleVolume;
        this.reverbEnabled = reverbEnabled;
        this.reverbRoomScale = reverbRoomScale;
        this.reverbDecay = reverbDecay;
        this.reverbWetGain = reverbWetGain;
        this.asyncEnabled = asyncEnabled;
        this.maxPlayersPerTick = maxPlayersPerTick;
        this.debugEnabled = debugEnabled;
        this.logTraces = logTraces;
        this.visualizeOcclusion = visualizeOcclusion;
    }

    public static PhysicsConfig fromConfig(FileConfiguration cfg) {
        double maxDistance = cfg.getDouble("physics.max-distance", 48.0);
        double airAbsorption = cfg.getDouble("physics.air-absorption-per-meter", 0.02);
        int occlusionSteps = cfg.getInt("physics.occlusion-steps", 6);
        double occlusionPenalty = cfg.getDouble("physics.occlusion-penalty", 0.12);
        double minOcclusionVolume = cfg.getDouble("physics.min-occlusion-volume", 0.15);
        double minAudibleVolume = cfg.getDouble("physics.min-audible-volume", 0.05);

        boolean reverbEnabled = cfg.getBoolean("reverb.enabled", true);
        double reverbRoomScale = cfg.getDouble("reverb.room-scale", 0.65);
        double reverbDecay = cfg.getDouble("reverb.decay", 0.45);
        double reverbWetGain = cfg.getDouble("reverb.wet-gain", 0.25);

        boolean asyncEnabled = cfg.getBoolean("performance.async-enabled", true);
        int maxPlayersPerTick = cfg.getInt("performance.max-players-per-tick", 80);

        boolean debugEnabled = cfg.getBoolean("debug.enabled", false);
        boolean logTraces = cfg.getBoolean("debug.log-traces", false);
        boolean visualizeOcclusion = cfg.getBoolean("debug.visualize-occlusion", false);

        return new PhysicsConfig(maxDistance, airAbsorption, occlusionSteps, occlusionPenalty, minOcclusionVolume,
            minAudibleVolume, reverbEnabled, reverbRoomScale, reverbDecay, reverbWetGain, asyncEnabled,
            maxPlayersPerTick, debugEnabled, logTraces, visualizeOcclusion);
    }

    public double getMaxDistance() {
        return maxDistance;
    }

    public double getAirAbsorptionPerMeter() {
        return airAbsorptionPerMeter;
    }

    public int getOcclusionSteps() {
        return occlusionSteps;
    }

    public double getOcclusionPenalty() {
        return occlusionPenalty;
    }

    public double getMinOcclusionVolume() {
        return minOcclusionVolume;
    }

    public double getMinAudibleVolume() {
        return minAudibleVolume;
    }

    public boolean isReverbEnabled() {
        return reverbEnabled;
    }

    public double getReverbRoomScale() {
        return reverbRoomScale;
    }

    public double getReverbDecay() {
        return reverbDecay;
    }

    public double getReverbWetGain() {
        return reverbWetGain;
    }

    public boolean isAsyncEnabled() {
        return asyncEnabled;
    }

    public int getMaxPlayersPerTick() {
        return maxPlayersPerTick;
    }

    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    public boolean isLogTraces() {
        return logTraces;
    }

    public boolean isVisualizeOcclusion() {
        return visualizeOcclusion;
    }
}
