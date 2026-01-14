package com.webx.hdphysicssound.engine;

import org.bukkit.Location;

public class ReverbProfile {

    private final boolean enabled;
    private final double roomScale;
    private final double decay;
    private final double wetGain;

    public ReverbProfile(boolean enabled, double roomScale, double decay, double wetGain) {
        this.enabled = enabled;
        this.roomScale = roomScale;
        this.decay = decay;
        this.wetGain = wetGain;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int computeDelayTicks(Location source, Location listener) {
        double distance = source.distance(listener);
        return (int) Math.max(2, distance * roomScale * 2); // simple approximation
    }

    public float computeWetVolume(float dryVolume) {
        return (float) Math.max(0.0, dryVolume * wetGain * decay);
    }
}
