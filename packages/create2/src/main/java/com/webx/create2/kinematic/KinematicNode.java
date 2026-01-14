package com.webx.create2.kinematic;

import org.bukkit.Location;
import org.joml.Vector3i;

/**
 * Represents a single node in a kinematic network
 * Can be a shaft, gear, power source, or consumer
 * Inspired by Create's IRotate interface
 */
public class KinematicNode {
    
    private final Vector3i position;
    private final KinematicNodeType type;
    private double rpm;
    private double stressImpact;
    private double stressCapacity;
    private RotationAxis axis;
    private boolean reversed;
    
    public KinematicNode(Vector3i position, KinematicNodeType type) {
        this.position = position;
        this.type = type;
        this.rpm = 0.0;
        this.stressImpact = type.getDefaultStressImpact();
        this.stressCapacity = type.getDefaultStressCapacity();
        this.axis = RotationAxis.Y;
        this.reversed = false;
    }
    
    /**
     * Update this node's rotation
     */
    public void tick() {
        // Can be overridden for special behaviors
    }
    
    /**
     * Check if this node can connect to another
     */
    public boolean canConnectTo(KinematicNode other) {
        // Must be adjacent
        if (!isAdjacentTo(other)) return false;
        
        // Check axis compatibility
        return isAxisCompatible(other);
    }
    
    /**
     * Check if nodes are adjacent (Manhattan distance = 1)
     */
    private boolean isAdjacentTo(KinematicNode other) {
        int dx = Math.abs(position.x - other.position.x);
        int dy = Math.abs(position.y - other.position.y);
        int dz = Math.abs(position.z - other.position.z);
        return (dx + dy + dz) == 1;
    }
    
    /**
     * Check if rotation axes are compatible
     */
    private boolean isAxisCompatible(KinematicNode other) {
        // Same axis = direct connection
        if (axis == other.axis) return true;
        
        // Gears can connect perpendicular axes
        if (type == KinematicNodeType.COGWHEEL || other.type == KinematicNodeType.COGWHEEL) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Get gear ratio when connecting to another node
     */
    public double getGearRatioTo(KinematicNode other) {
        // Large cogwheel to small cogwheel = 2x speed
        if (type == KinematicNodeType.LARGE_COGWHEEL && other.type == KinematicNodeType.COGWHEEL) {
            return 2.0;
        }
        
        // Small to large = 0.5x speed
        if (type == KinematicNodeType.COGWHEEL && other.type == KinematicNodeType.LARGE_COGWHEEL) {
            return 0.5;
        }
        
        // Default = 1:1 ratio
        return 1.0;
    }
    
    /**
     * Calculate output RPM to another node
     */
    public double getOutputRpm(KinematicNode other) {
        double ratio = getGearRatioTo(other);
        double output = rpm * ratio;
        
        // Apply efficiency loss
        output *= 0.98; // TODO: from config
        
        // Reverse if needed
        if (shouldReverse(other)) {
            output = -output;
        }
        
        return output;
    }
    
    /**
     * Check if rotation should reverse when connecting
     */
    private boolean shouldReverse(KinematicNode other) {
        // Gears reverse rotation
        if (type == KinematicNodeType.COGWHEEL || type == KinematicNodeType.LARGE_COGWHEEL) {
            return true;
        }
        
        // Gearbox reverses on specific faces
        if (type == KinematicNodeType.GEARBOX) {
            return isPerpendicularConnection(other);
        }
        
        return false;
    }
    
    /**
     * Check if connection is perpendicular
     */
    private boolean isPerpendicularConnection(KinematicNode other) {
        return axis != other.axis;
    }
    
    // Getters and setters
    public Vector3i getPosition() {
        return new Vector3i(position);
    }
    
    public KinematicNodeType getType() {
        return type;
    }
    
    public double getRpm() {
        return rpm;
    }
    
    public void setRpm(double rpm) {
        this.rpm = rpm;
    }
    
    public double getStressImpact() {
        return stressImpact;
    }
    
    public void setStressImpact(double stressImpact) {
        this.stressImpact = stressImpact;
    }
    
    public double getStressCapacity() {
        return stressCapacity;
    }
    
    public void setStressCapacity(double stressCapacity) {
        this.stressCapacity = stressCapacity;
    }
    
    public RotationAxis getAxis() {
        return axis;
    }
    
    public void setAxis(RotationAxis axis) {
        this.axis = axis;
    }
    
    public boolean isReversed() {
        return reversed;
    }
    
    public void setReversed(boolean reversed) {
        this.reversed = reversed;
    }
    
    public boolean isPowerSource() {
        return stressCapacity > 0;
    }
    
    public boolean isPowerConsumer() {
        return stressImpact > 0;
    }
    
    @Override
    public String toString() {
        return String.format("%s @ %s [RPM: %.1f, Stress: %.1f/%.1f]", 
            type, position, rpm, stressImpact, stressCapacity);
    }
    
    /**
     * Rotation axis enum
     */
    public enum RotationAxis {
        X, Y, Z
    }
}
