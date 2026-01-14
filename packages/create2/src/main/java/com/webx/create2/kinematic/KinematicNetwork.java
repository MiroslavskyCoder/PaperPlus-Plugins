package com.webx.create2.kinematic;

import org.bukkit.Location;
import org.joml.Vector3i;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a kinematic network - a connected system of rotating components
 * Inspired by Create's Rotational Network system
 */
public class KinematicNetwork {
    
    private final UUID id;
    private final Set<Vector3i> components;
    private final Map<Vector3i, KinematicNode> nodes;
    private double rpm;
    private double stress;
    private double stressCapacity;
    private boolean valid;
    
    public KinematicNetwork() {
        this.id = UUID.randomUUID();
        this.components = ConcurrentHashMap.newKeySet();
        this.nodes = new ConcurrentHashMap<>();
        this.rpm = 0.0;
        this.stress = 0.0;
        this.stressCapacity = 0.0;
        this.valid = true;
    }
    
    /**
     * Add a component to this network
     */
    public void addComponent(Vector3i position, KinematicNode node) {
        components.add(position);
        nodes.put(position, node);
        recalculateNetwork();
    }
    
    /**
     * Remove a component from this network
     */
    public void removeComponent(Vector3i position) {
        components.remove(position);
        nodes.remove(position);
        
        if (components.isEmpty()) {
            valid = false;
        } else {
            recalculateNetwork();
        }
    }
    
    /**
     * Check if network contains this position
     */
    public boolean contains(Vector3i position) {
        return components.contains(position);
    }
    
    /**
     * Get node at position
     */
    public KinematicNode getNode(Vector3i position) {
        return nodes.get(position);
    }
    
    /**
     * Update network state (called every tick)
     */
    public void tick() {
        if (!valid) return;
        
        // Apply stress decay if overloaded
        if (stress > stressCapacity) {
            double decayRate = 0.1; // TODO: from config
            rpm *= (1.0 - decayRate);
            
            if (rpm < 0.1) {
                rpm = 0.0;
            }
        }
        
        // Update all nodes with current RPM
        for (KinematicNode node : nodes.values()) {
            node.setRpm(rpm);
        }
    }
    
    /**
     * Recalculate network properties (stress, capacity, etc.)
     * Inspired by Create's StressImpact system
     */
    private void recalculateNetwork() {
        double totalStress = 0.0;
        double totalCapacity = 0.0;
        double sourceRpm = 0.0;
        
        for (KinematicNode node : nodes.values()) {
            // Add stress from consumers
            if (node.getStressImpact() > 0) {
                totalStress += node.getStressImpact();
            }
            
            // Add capacity from generators
            if (node.getStressCapacity() > 0) {
                totalCapacity += node.getStressCapacity();
                
                // Use RPM from strongest generator
                if (node.getRpm() > sourceRpm) {
                    sourceRpm = node.getRpm();
                }
            }
        }
        
        this.stress = totalStress;
        this.stressCapacity = totalCapacity;
        
        // Set RPM only if we have capacity
        if (totalCapacity > 0) {
            this.rpm = sourceRpm;
        } else {
            this.rpm = 0.0;
        }
    }
    
    /**
     * Merge another network into this one
     */
    public void merge(KinematicNetwork other) {
        if (other == this) return;
        
        // Transfer all components
        for (Vector3i pos : other.components) {
            KinematicNode node = other.nodes.get(pos);
            if (node != null) {
                addComponent(pos, node);
            }
        }
        
        // Invalidate the other network
        other.valid = false;
        other.components.clear();
        other.nodes.clear();
    }
    
    /**
     * Split network at a position (when component removed)
     */
    public List<KinematicNetwork> split(Vector3i splitPosition) {
        List<KinematicNetwork> newNetworks = new ArrayList<>();
        
        // Find connected components using BFS
        Set<Vector3i> visited = new HashSet<>();
        
        for (Vector3i start : components) {
            if (visited.contains(start)) continue;
            if (start.equals(splitPosition)) continue;
            
            // BFS to find connected group
            Set<Vector3i> group = new HashSet<>();
            Queue<Vector3i> queue = new LinkedList<>();
            queue.add(start);
            
            while (!queue.isEmpty()) {
                Vector3i current = queue.poll();
                if (visited.contains(current)) continue;
                if (current.equals(splitPosition)) continue;
                
                visited.add(current);
                group.add(current);
                
                // Check neighbors (6 directions)
                for (Vector3i neighbor : getNeighbors(current)) {
                    if (components.contains(neighbor) && !visited.contains(neighbor)) {
                        queue.add(neighbor);
                    }
                }
            }
            
            // Create new network for this group
            if (!group.isEmpty()) {
                KinematicNetwork newNetwork = new KinematicNetwork();
                for (Vector3i pos : group) {
                    KinematicNode node = nodes.get(pos);
                    if (node != null) {
                        newNetwork.addComponent(pos, node);
                    }
                }
                newNetworks.add(newNetwork);
            }
        }
        
        // Invalidate this network
        this.valid = false;
        
        return newNetworks;
    }
    
    /**
     * Get neighboring positions (6 directions)
     */
    private List<Vector3i> getNeighbors(Vector3i pos) {
        List<Vector3i> neighbors = new ArrayList<>();
        neighbors.add(new Vector3i(pos.x + 1, pos.y, pos.z));
        neighbors.add(new Vector3i(pos.x - 1, pos.y, pos.z));
        neighbors.add(new Vector3i(pos.x, pos.y + 1, pos.z));
        neighbors.add(new Vector3i(pos.x, pos.y - 1, pos.z));
        neighbors.add(new Vector3i(pos.x, pos.y, pos.z + 1));
        neighbors.add(new Vector3i(pos.x, pos.y, pos.z - 1));
        return neighbors;
    }
    
    // Getters
    public UUID getId() {
        return id;
    }
    
    public Set<Vector3i> getComponents() {
        return new HashSet<>(components);
    }
    
    public int getSize() {
        return components.size();
    }
    
    public double getRpm() {
        return rpm;
    }
    
    public void setRpm(double rpm) {
        this.rpm = Math.max(0, rpm);
    }
    
    public double getStress() {
        return stress;
    }
    
    public double getStressCapacity() {
        return stressCapacity;
    }
    
    public boolean isValid() {
        return valid;
    }
    
    public boolean isOverstressed() {
        return stress > stressCapacity;
    }
    
    public double getStressPercentage() {
        if (stressCapacity <= 0) return 0.0;
        return (stress / stressCapacity) * 100.0;
    }
    
    @Override
    public String toString() {
        return String.format("Network{id=%s, size=%d, rpm=%.1f, stress=%.1f/%.1f (%.1f%%)}", 
            id.toString().substring(0, 8), 
            components.size(), 
            rpm, 
            stress, 
            stressCapacity, 
            getStressPercentage()
        );
    }
}
