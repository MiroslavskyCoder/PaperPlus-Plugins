package com.webx.create2.kinematic;

import com.webx.create2.Create2Plugin;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.joml.Vector3i;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages all kinematic networks in the world
 * Handles network creation, merging, splitting
 * Inspired by Create's RotationPropagator
 */
public class KinematicNetworkManager {
    
    private final Create2Plugin plugin;
    private final Map<UUID, KinematicNetwork> networks;
    private final Map<Vector3i, UUID> positionToNetwork;
    
    public KinematicNetworkManager(Create2Plugin plugin) {
        this.plugin = plugin;
        this.networks = new ConcurrentHashMap<>();
        this.positionToNetwork = new ConcurrentHashMap<>();
    }
    
    /**
     * Create a new kinematic node at position
     */
    public void createNode(Vector3i position, KinematicNodeType type) {
        // Check if already exists
        if (positionToNetwork.containsKey(position)) {
            plugin.getLogger().warning("Node already exists at " + position);
            return;
        }
        
        // Create new node
        KinematicNode node = new KinematicNode(position, type);
        
        // Find adjacent networks
        List<KinematicNetwork> adjacentNetworks = findAdjacentNetworks(position, node);
        
        if (adjacentNetworks.isEmpty()) {
            // Create new network
            KinematicNetwork network = new KinematicNetwork();
            network.addComponent(position, node);
            networks.put(network.getId(), network);
            positionToNetwork.put(position, network.getId());
            
            plugin.getLogger().info("Created new network: " + network.getId());
        } else if (adjacentNetworks.size() == 1) {
            // Add to existing network
            KinematicNetwork network = adjacentNetworks.get(0);
            network.addComponent(position, node);
            positionToNetwork.put(position, network.getId());
            
            plugin.getLogger().info("Added node to network: " + network.getId());
        } else {
            // Merge multiple networks
            KinematicNetwork mainNetwork = adjacentNetworks.get(0);
            mainNetwork.addComponent(position, node);
            positionToNetwork.put(position, mainNetwork.getId());
            
            // Merge other networks into main
            for (int i = 1; i < adjacentNetworks.size(); i++) {
                KinematicNetwork other = adjacentNetworks.get(i);
                mainNetwork.merge(other);
                networks.remove(other.getId());
                
                // Update position mappings
                for (Vector3i pos : other.getComponents()) {
                    positionToNetwork.put(pos, mainNetwork.getId());
                }
            }
            
            plugin.getLogger().info("Merged " + adjacentNetworks.size() + " networks into: " + mainNetwork.getId());
        }
    }
    
    /**
     * Remove a node at position
     */
    public void removeNode(Vector3i position) {
        UUID networkId = positionToNetwork.remove(position);
        if (networkId == null) return;
        
        KinematicNetwork network = networks.get(networkId);
        if (network == null) return;
        
        // Remove from network
        network.removeComponent(position);
        
        // Check if network should split
        if (network.getSize() > 1) {
            // Split network
            List<KinematicNetwork> newNetworks = network.split(position);
            
            // Remove old network
            networks.remove(networkId);
            
            // Add new networks
            for (KinematicNetwork newNetwork : newNetworks) {
                networks.put(newNetwork.getId(), newNetwork);
                
                // Update position mappings
                for (Vector3i pos : newNetwork.getComponents()) {
                    positionToNetwork.put(pos, newNetwork.getId());
                }
            }
            
            plugin.getLogger().info("Split network into " + newNetworks.size() + " networks");
        } else {
            // Network is now empty, remove it
            networks.remove(networkId);
            plugin.getLogger().info("Removed empty network: " + networkId);
        }
    }
    
    /**
     * Get node at position
     */
    public KinematicNode getNode(Vector3i position) {
        UUID networkId = positionToNetwork.get(position);
        if (networkId == null) return null;
        
        KinematicNetwork network = networks.get(networkId);
        if (network == null) return null;
        
        return network.getNode(position);
    }
    
    /**
     * Get network containing position
     */
    public KinematicNetwork getNetwork(Vector3i position) {
        UUID networkId = positionToNetwork.get(position);
        if (networkId == null) return null;
        
        return networks.get(networkId);
    }
    
    /**
     * Find adjacent networks that can connect to this node
     */
    private List<KinematicNetwork> findAdjacentNetworks(Vector3i position, KinematicNode newNode) {
        Set<KinematicNetwork> adjacentSet = new HashSet<>();
        
        // Check all 6 neighbors
        Vector3i[] neighbors = {
            new Vector3i(position.x + 1, position.y, position.z),
            new Vector3i(position.x - 1, position.y, position.z),
            new Vector3i(position.x, position.y + 1, position.z),
            new Vector3i(position.x, position.y - 1, position.z),
            new Vector3i(position.x, position.y, position.z + 1),
            new Vector3i(position.x, position.y, position.z - 1)
        };
        
        for (Vector3i neighbor : neighbors) {
            UUID networkId = positionToNetwork.get(neighbor);
            if (networkId != null) {
                KinematicNetwork network = networks.get(networkId);
                if (network != null && network.isValid()) {
                    KinematicNode neighborNode = network.getNode(neighbor);
                    
                    // Check if nodes can connect
                    if (neighborNode != null && newNode.canConnectTo(neighborNode)) {
                        adjacentSet.add(network);
                    }
                }
            }
        }
        
        return new ArrayList<>(adjacentSet);
    }
    
    /**
     * Update all networks (called every tick)
     */
    public void tick() {
        // Remove invalid networks
        networks.values().removeIf(network -> !network.isValid());
        
        // Update all networks
        for (KinematicNetwork network : networks.values()) {
            network.tick();
        }
    }
    
    /**
     * Save all networks to disk
     */
    public void saveAll() {
        if (!plugin.getConfig().getBoolean("persistence.enabled", true)) return;

        File file = new File(plugin.getDataFolder(), plugin.getConfig().getString("persistence.file", "networks.yml"));
        YamlConfiguration yaml = new YamlConfiguration();

        int idx = 0;
        for (KinematicNetwork network : networks.values()) {
            List<Map<String, Object>> list = new ArrayList<>();
            for (Vector3i pos : network.getComponents()) {
                Map<String, Object> entry = new HashMap<>();
                entry.put("x", pos.x);
                entry.put("y", pos.y);
                entry.put("z", pos.z);
                entry.put("type", network.getNode(pos).getType().name());
                list.add(entry);
            }
            yaml.set("networks." + idx + ".nodes", list);
            idx++;
        }

        try {
            yaml.save(file);
            plugin.getLogger().info("Saved " + networks.size() + " networks to " + file.getName());
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save networks: " + e.getMessage());
        }
    }
    
    /**
     * Load networks from disk
     */
    public void loadAll() {
        if (!plugin.getConfig().getBoolean("persistence.enabled", true)) return;

        File file = new File(plugin.getDataFolder(), plugin.getConfig().getString("persistence.file", "networks.yml"));
        if (!file.exists()) return;

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        networks.clear();
        positionToNetwork.clear();

        if (!yaml.isConfigurationSection("networks")) return;

        for (String key : Objects.requireNonNull(yaml.getConfigurationSection("networks")).getKeys(false)) {
            List<Map<?, ?>> list = yaml.getMapList("networks." + key + ".nodes");
            KinematicNetwork network = new KinematicNetwork();
            for (Map<?, ?> entry : list) {
                int x = entry.get("x") != null ? ((Number) entry.get("x")).intValue() : 0;
                int y = entry.get("y") != null ? ((Number) entry.get("y")).intValue() : 0;
                int z = entry.get("z") != null ? ((Number) entry.get("z")).intValue() : 0;
                String typeStr = (String) entry.get("type");
                KinematicNodeType type = KinematicNodeType.valueOf(typeStr);
                Vector3i pos = new Vector3i(x, y, z);
                KinematicNode node = new KinematicNode(pos, type);
                network.addComponent(pos, node);
                positionToNetwork.put(pos, network.getId());
            }
            networks.put(network.getId(), network);
        }
        plugin.getLogger().info("Loaded " + networks.size() + " networks from " + file.getName());
    }
    
    /**
     * Get all networks
     */
    public Collection<KinematicNetwork> getAllNetworks() {
        return new ArrayList<>(networks.values());
    }
    
    /**
     * Get network count
     */
    public int getNetworkCount() {
        return networks.size();
    }
    
    /**
     * Get total component count
     */
    public int getTotalComponentCount() {
        return positionToNetwork.size();
    }
    
    /**
     * Get network statistics
     */
    public NetworkStats getStats() {
        int totalComponents = positionToNetwork.size();
        int totalNetworks = networks.size();
        double avgSize = totalNetworks > 0 ? (double) totalComponents / totalNetworks : 0;
        
        int largestNetwork = networks.values().stream()
            .mapToInt(KinematicNetwork::getSize)
            .max()
            .orElse(0);
        
        long overstressedNetworks = networks.values().stream()
            .filter(KinematicNetwork::isOverstressed)
            .count();
        
        return new NetworkStats(
            totalNetworks,
            totalComponents,
            avgSize,
            largestNetwork,
            (int) overstressedNetworks
        );
    }
    
    /**
     * Network statistics data class
     */
    public static class NetworkStats {
        public final int totalNetworks;
        public final int totalComponents;
        public final double avgNetworkSize;
        public final int largestNetwork;
        public final int overstressedNetworks;
        
        public NetworkStats(int totalNetworks, int totalComponents, double avgNetworkSize, 
                          int largestNetwork, int overstressedNetworks) {
            this.totalNetworks = totalNetworks;
            this.totalComponents = totalComponents;
            this.avgNetworkSize = avgNetworkSize;
            this.largestNetwork = largestNetwork;
            this.overstressedNetworks = overstressedNetworks;
        }
        
        @Override
        public String toString() {
            return String.format("Networks: %d | Components: %d | Avg Size: %.1f | Largest: %d | Overstressed: %d",
                totalNetworks, totalComponents, avgNetworkSize, largestNetwork, overstressedNetworks);
        }
    }
}
