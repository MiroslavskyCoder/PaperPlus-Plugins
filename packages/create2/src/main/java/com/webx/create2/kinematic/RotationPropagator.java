package com.webx.create2.kinematic;

import com.webx.create2.Create2Plugin;
import org.joml.Vector3i;

import java.util.*;

/**
 * Handles propagation of rotation through kinematic networks
 * Calculates RPM and stress distribution
 * Inspired by Create's RotationPropagator
 */
public class RotationPropagator {
    
    private final Create2Plugin plugin;
    
    public RotationPropagator(Create2Plugin plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Propagate rotation from a source node through the network
     * Uses breadth-first search to spread rotation
     */
    public void propagateRotation(KinematicNetwork network, Vector3i sourcePos, double rpm) {
        if (network == null || !network.isValid()) return;
        
        KinematicNode source = network.getNode(sourcePos);
        if (source == null) return;
        
        // Set source RPM
        source.setRpm(rpm);
        
        // Track visited nodes to avoid loops
        Set<Vector3i> visited = new HashSet<>();
        Queue<PropagationStep> queue = new LinkedList<>();
        
        // Start from source
        queue.add(new PropagationStep(sourcePos, rpm, false));
        
        while (!queue.isEmpty()) {
            PropagationStep step = queue.poll();
            
            if (visited.contains(step.position)) continue;
            visited.add(step.position);
            
            KinematicNode current = network.getNode(step.position);
            if (current == null) continue;
            
            // Apply RPM (with reversal if needed)
            double currentRpm = step.reversed ? -step.rpm : step.rpm;
            current.setRpm(currentRpm);
            
            // Propagate to neighbors
            for (Vector3i neighbor : getNeighbors(step.position)) {
                if (visited.contains(neighbor)) continue;
                
                KinematicNode neighborNode = network.getNode(neighbor);
                if (neighborNode == null) continue;
                
                // Check if can connect
                if (!current.canConnectTo(neighborNode)) continue;
                
                // Calculate output RPM
                double outputRpm = current.getOutputRpm(neighborNode);
                boolean reversed = current.isReversed();
                
                // Add to queue
                queue.add(new PropagationStep(neighbor, Math.abs(outputRpm), reversed));
            }
        }
    }
    
    /**
     * Calculate stress distribution in network
     * Returns map of position -> stress value
     */
    public Map<Vector3i, Double> calculateStressDistribution(KinematicNetwork network) {
        Map<Vector3i, Double> stressMap = new HashMap<>();
        
        if (network == null || !network.isValid()) return stressMap;
        
        // Get all components
        for (Vector3i pos : network.getComponents()) {
            KinematicNode node = network.getNode(pos);
            if (node != null) {
                double stress = node.getStressImpact();
                stressMap.put(pos, stress);
            }
        }
        
        return stressMap;
    }
    
    /**
     * Find optimal power source for network
     * Returns position of best generator
     */
    public Vector3i findOptimalPowerSource(KinematicNetwork network) {
        if (network == null || !network.isValid()) return null;
        
        Vector3i bestSource = null;
        double bestCapacity = 0;
        
        for (Vector3i pos : network.getComponents()) {
            KinematicNode node = network.getNode(pos);
            if (node != null && node.isPowerSource()) {
                if (node.getStressCapacity() > bestCapacity) {
                    bestCapacity = node.getStressCapacity();
                    bestSource = pos;
                }
            }
        }
        
        return bestSource;
    }
    
    /**
     * Validate network integrity
     * Checks for disconnected components
     */
    public boolean validateNetwork(KinematicNetwork network) {
        if (network == null || !network.isValid()) return false;
        
        Set<Vector3i> components = network.getComponents();
        if (components.isEmpty()) return false;
        
        // BFS to check all components are connected
        Set<Vector3i> visited = new HashSet<>();
        Queue<Vector3i> queue = new LinkedList<>();
        
        Vector3i start = components.iterator().next();
        queue.add(start);
        
        while (!queue.isEmpty()) {
            Vector3i current = queue.poll();
            if (visited.contains(current)) continue;
            visited.add(current);
            
            for (Vector3i neighbor : getNeighbors(current)) {
                if (components.contains(neighbor) && !visited.contains(neighbor)) {
                    queue.add(neighbor);
                }
            }
        }
        
        // All components should be visited
        return visited.size() == components.size();
    }
    
    /**
     * Calculate maximum stress capacity path
     * Used for optimization
     */
    public List<Vector3i> findMaxCapacityPath(KinematicNetwork network, Vector3i from, Vector3i to) {
        if (network == null || from == null || to == null) return Collections.emptyList();
        
        // Dijkstra's algorithm with stress capacity as weight
        Map<Vector3i, Double> capacity = new HashMap<>();
        Map<Vector3i, Vector3i> previous = new HashMap<>();
        PriorityQueue<Vector3i> queue = new PriorityQueue<>(
            Comparator.comparingDouble(pos -> -capacity.getOrDefault(pos, 0.0))
        );
        
        capacity.put(from, Double.MAX_VALUE);
        queue.add(from);
        
        while (!queue.isEmpty()) {
            Vector3i current = queue.poll();
            
            if (current.equals(to)) break;
            
            for (Vector3i neighbor : getNeighbors(current)) {
                KinematicNode node = network.getNode(neighbor);
                if (node == null) continue;
                
                double newCapacity = Math.min(
                    capacity.get(current),
                    node.getStressCapacity()
                );
                
                if (newCapacity > capacity.getOrDefault(neighbor, 0.0)) {
                    capacity.put(neighbor, newCapacity);
                    previous.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }
        
        // Reconstruct path
        List<Vector3i> path = new ArrayList<>();
        Vector3i current = to;
        while (current != null) {
            path.add(0, current);
            current = previous.get(current);
        }
        
        return path;
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
    
    /**
     * Helper class for BFS propagation
     */
    private static class PropagationStep {
        final Vector3i position;
        final double rpm;
        final boolean reversed;
        
        PropagationStep(Vector3i position, double rpm, boolean reversed) {
            this.position = position;
            this.rpm = rpm;
            this.reversed = reversed;
        }
    }
}
