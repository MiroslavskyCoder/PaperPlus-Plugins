package com.webx.create2.kinematic;

/**
 * Types of kinematic nodes in the system
 * Each has different stress impact and capacity
 * Inspired by Create's mechanical components
 */
public enum KinematicNodeType {
    
    // Transmission components (pass rotation)
    SHAFT(1.0, 0.0),
    COGWHEEL(2.0, 0.0),
    LARGE_COGWHEEL(2.0, 0.0),
    GEARBOX(1.5, 0.0),
    
    // Power sources (generate rotation)
    HAND_CRANK(0.0, 0.0),  // No stress (manual)
    WATER_WHEEL(0.0, 512.0),
    WINDMILL(0.0, 1024.0),
    MOTOR(0.0, 99999.0),  // Creative only
    
    // Power consumers (use rotation)
    MECHANICAL_PRESS(8.0, 0.0),
    MECHANICAL_MIXER(12.0, 0.0),
    MILLSTONE(4.0, 0.0),
    DEPLOYER(4.0, 0.0),
    SAW(4.0, 0.0),
    DRILL(4.0, 0.0),

    // Logistics
    BELT(2.0, 0.0),
    FUNNEL(1.0, 0.0),

    // Fluids (driven by rotation for pumps)
    PUMP(8.0, 0.0),
    PIPE(0.5, 0.0),
    TANK(0.0, 0.0);
    
    private final double defaultStressImpact;
    private final double defaultStressCapacity;
    
    KinematicNodeType(double stressImpact, double stressCapacity) {
        this.defaultStressImpact = stressImpact;
        this.defaultStressCapacity = stressCapacity;
    }
    
    public double getDefaultStressImpact() {
        return defaultStressImpact;
    }
    
    public double getDefaultStressCapacity() {
        return defaultStressCapacity;
    }
    
    public boolean isGenerator() {
        return defaultStressCapacity > 0;
    }
    
    public boolean isConsumer() {
        return defaultStressImpact > 0;
    }
    
    public boolean isTransmission() {
        return !isGenerator() && !isConsumer();
    }
}
