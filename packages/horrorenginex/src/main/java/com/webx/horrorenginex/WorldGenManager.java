package com.webx.horrorenginex;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

/**
 * Manages horror world generation and environmental effects
 * Generates haunted structures, caves, and constant rain
 */
public class WorldGenManager implements Listener {
    
    private final HorrorEngineXPlugin plugin;
    private final Set<Location> generatedStructures = new HashSet<>();
    private final Map<Location, StructureType> structureLocations = new HashMap<>();
    private BukkitTask rainTask;
    private BukkitTask animalWatcherTask;
    private BukkitTask playerGenerationTask;
    private boolean preGenerationComplete = false;
    private BukkitTask preGenerationTask;
    private static final int STRUCTURE_SEARCH_RADIUS = 150;
    private static final int GENERATION_CHECK_INTERVAL = 20; // 1 second
    
    /**
     * Types of structures for tunnel connections
     */
    private enum StructureType {
        HAUNTED_HOUSE,
        SECRET_LAB,
        SAWMILL
    }
    
    public WorldGenManager(HorrorEngineXPlugin plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Start world generation systems
     */
    public void startWorldGeneration() {
        // Start constant rain system
        if (plugin.getConfigManager().getConfig().getBoolean("world-gen.constant-rain", true)) {
            startConstantRain();
        }
        
        // Pre-generate structures on startup (in async with main thread blocks)
        // Delay gives server time to fully initialize
        Bukkit.getScheduler().runTaskAsynchronously(plugin, this::preGenerateStructures);
        
        // Start structure generation around players (if enabled in config)
        if (plugin.getConfigManager().getConfig().getBoolean("world-gen.continuous-generation", false)) {
            startStructureGeneration();
        }
        
        // Start animal watcher behavior
        if (plugin.getConfigManager().getConfig().getBoolean("world-gen.animal-watchers", true)) {
            startAnimalWatchers();
        }
        
        plugin.getLogger().info("World generation system enabled");
    }
    
    /**
     * Stop world generation systems
     */
    public void stopWorldGeneration() {
        if (rainTask != null) {
            rainTask.cancel();
            rainTask = null;
        }
        
        if (animalWatcherTask != null) {
            animalWatcherTask.cancel();
            animalWatcherTask = null;
        }
        
        if (playerGenerationTask != null) {
            playerGenerationTask.cancel();
            playerGenerationTask = null;
        }
        
        if (preGenerationTask != null) {
            preGenerationTask.cancel();
            preGenerationTask = null;
        }
    }
    
    /**
     * Start constant rain in all worlds
     */
    private void startConstantRain() {
        rainTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (World world : Bukkit.getWorlds()) {
                if (plugin.getConfigManager().getConfig().getStringList("world-gen.rain-worlds")
                    .contains(world.getName())) {
                    // Set weather to rain for 24 hours (1728000 ticks)
                    world.setWeatherDuration(1728000);
                    world.setThundering(false);
                    world.setStorm(true);
                }
            }
        }, 100, 1200); // Check every 60 seconds
    }
    
    /**
     * Start generating structures around players
     */
    private void startStructureGeneration() {
        playerGenerationTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (plugin.isBypassed(player)) continue;
                
                World world = player.getWorld();
                Location playerLoc = player.getLocation();
                
                // Check for haunted houses
                if (plugin.getConfigManager().getConfig().getBoolean("world-gen.haunted-houses", true)) {
                    tryGenerateHauntedHouse(world, playerLoc);
                }
                
                // Check for caves
                if (plugin.getConfigManager().getConfig().getBoolean("world-gen.cave-tunnels", true)) {
                    tryGenerateCaveTunnel(world, playerLoc);
                }
                
                // Check for large block formations
                if (plugin.getConfigManager().getConfig().getBoolean("world-gen.large-blocks", true)) {
                    tryGenerateLargeBlocks(world, playerLoc);
                }
                
                // Check for secret laboratories
                if (plugin.getConfigManager().getConfig().getBoolean("world-gen.secret-labs", true)) {
                    tryGenerateSecretLab(world, playerLoc);
                }
                
                // Check for sawmills
                if (plugin.getConfigManager().getConfig().getBoolean("world-gen.sawmills", true)) {
                    tryGenerateSawmill(world, playerLoc);
                }
            }
        }, GENERATION_CHECK_INTERVAL, GENERATION_CHECK_INTERVAL);
    }
    
    /**
     * Start animal watcher behavior system
     */
    private void startAnimalWatchers() {
        animalWatcherTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (plugin.isBypassed(player)) continue;
                
                // Find nearby animals and make them stare
                checkNearbyAnimals(player);
            }
        }, 60, 20); // Check every second
    }
    
    /**
     * Try to generate a haunted house near the player
     */
    private void tryGenerateHauntedHouse(World world, Location playerLoc) {
        // Generate around the player at ground level
        for (int dx = -STRUCTURE_SEARCH_RADIUS; dx < STRUCTURE_SEARCH_RADIUS; dx += 50) {
            for (int dz = -STRUCTURE_SEARCH_RADIUS; dz < STRUCTURE_SEARCH_RADIUS; dz += 50) {
                Location checkLoc = playerLoc.clone().add(dx, 0, dz);
                checkLoc = getGroundLevel(checkLoc);
                
                if (checkLoc != null && !generatedStructures.contains(checkLoc)) {
                    // 20% chance to generate a house (increased from 5%)
                    if (Math.random() < 0.20) {
                        HauntedHouseGenerator.generateHouse(world, checkLoc);
                        generatedStructures.add(checkLoc);
                        structureLocations.put(checkLoc, StructureType.HAUNTED_HOUSE);
                        
                        // Try to connect to nearby structures with tunnel
                        connectToNearbyStructure(world, checkLoc);
                    }
                }
            }
        }
    }
    
    /**
     * Try to generate a cave tunnel system (now connects structures)
     * This method is no longer used for random generation
     * Tunnels are created via connectToNearbyStructure() instead
     */
    private void tryGenerateCaveTunnel(World world, Location playerLoc) {
        // Disabled: Cave tunnels now only connect structures
        // See connectToNearbyStructure() method
    }
    
    /**
     * Try to generate large block formations
     */
    private void tryGenerateLargeBlocks(World world, Location playerLoc) {
        for (int dx = -STRUCTURE_SEARCH_RADIUS; dx < STRUCTURE_SEARCH_RADIUS; dx += 80) {
            for (int dz = -STRUCTURE_SEARCH_RADIUS; dz < STRUCTURE_SEARCH_RADIUS; dz += 80) {
                Location checkLoc = playerLoc.clone().add(dx, 25, dz);
                
                if (!generatedStructures.contains(checkLoc)) {
                    // 25% chance to generate large blocks (increased from 8%)
                    if (Math.random() < 0.25) {
                        generateLargeBlocks(world, checkLoc);
                        generatedStructures.add(checkLoc);
                    }
                }
            }
        }
    }
    
    /**
     * Try to generate a secret laboratory
     */
    private void tryGenerateSecretLab(World world, Location playerLoc) {
        for (int dx = -STRUCTURE_SEARCH_RADIUS; dx < STRUCTURE_SEARCH_RADIUS; dx += 70) {
            for (int dz = -STRUCTURE_SEARCH_RADIUS; dz < STRUCTURE_SEARCH_RADIUS; dz += 70) {
                Location checkLoc = playerLoc.clone().add(dx, 0, dz);
                checkLoc = getGroundLevel(checkLoc);
                
                if (checkLoc != null && !generatedStructures.contains(checkLoc)) {
                    // 10% chance to generate secret lab (increased from 2%)
                    if (Math.random() < 0.10) {
                        SecretLabGenerator.generateLaboratory(world, checkLoc);
                        generatedStructures.add(checkLoc);
                        structureLocations.put(checkLoc, StructureType.SECRET_LAB);
                        
                        // Try to connect to nearby structures with tunnel
                        connectToNearbyStructure(world, checkLoc);
                    }
                }
            }
        }
    }
    
    /**
     * Try to generate a sawmill
     */
    private void tryGenerateSawmill(World world, Location playerLoc) {
        for (int dx = -STRUCTURE_SEARCH_RADIUS; dx < STRUCTURE_SEARCH_RADIUS; dx += 65) {
            for (int dz = -STRUCTURE_SEARCH_RADIUS; dz < STRUCTURE_SEARCH_RADIUS; dz += 65) {
                Location checkLoc = playerLoc.clone().add(dx, 0, dz);
                checkLoc = getGroundLevel(checkLoc);
                
                if (checkLoc != null && !generatedStructures.contains(checkLoc)) {
                    // 15% chance to generate sawmill (increased from 4%)
                    if (Math.random() < 0.15) {
                        SawmillGenerator.generateSawmill(world, checkLoc);
                        generatedStructures.add(checkLoc);
                        structureLocations.put(checkLoc, StructureType.SAWMILL);
                        
                        // Try to connect to nearby structures with tunnel
                        connectToNearbyStructure(world, checkLoc);
                    }
                }
            }
        }
    }
    
    /**
     * Generate random large block formations
     */
    private void generateLargeBlocks(World world, Location loc) {
        Material[] materials = {
            Material.BLACKSTONE,
            Material.DEEPSLATE,
            Material.OBSIDIAN,
            Material.BEDROCK
        };
        
        Material blockType = materials[(int)(Math.random() * materials.length)];
        int radius = (int)(Math.random() * 3) + 1; // 1-3 blocks radius
        
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                Location blockLoc = loc.clone().add(dx, 0, dz);
                if (blockLoc.getBlock().getType() == Material.AIR || 
                    blockLoc.getBlock().getType().toString().contains("CAVE")) {
                    blockLoc.getBlock().setType(blockType);
                }
            }
        }
    }
    
    /**
     * Check nearby animals and make them stare at player
     */
    private void checkNearbyAnimals(Player player) {
        Location playerLoc = player.getLocation();
        
        // Find all entities within 3-5 blocks (as per requirements)
        int radius = 3 + (int)(Math.random() * 3); // Random 3-5 block radius
        playerLoc.getWorld().getNearbyEntities(playerLoc, radius, radius, radius).forEach(entity -> {
            if (entity instanceof org.bukkit.entity.Pig ||
                entity instanceof org.bukkit.entity.Cow ||
                entity instanceof org.bukkit.entity.Sheep) {
                
                // Make animal look at player
                if (entity instanceof org.bukkit.entity.LivingEntity) {
                    org.bukkit.entity.LivingEntity livingEntity = (org.bukkit.entity.LivingEntity) entity;
                    
                    // Rotate head toward player
                    Location entityLoc = entity.getLocation();
                    Location playerEyeLoc = player.getEyeLocation();
                    
                    // Play creepy stare sound effect (ambient cave sound)
                    player.playSound(player.getLocation(), Sound.AMBIENT_CAVE, 0.3f, 0.5f);
                    
                    // Calculate rotation to look at player
                    double dx = playerEyeLoc.getX() - entityLoc.getX();
                    double dy = playerEyeLoc.getY() - (entityLoc.getY() + 1);
                    double dz = playerEyeLoc.getZ() - entityLoc.getZ();
                    
                    double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
                    
                    float yaw = (float) -Math.toDegrees(Math.atan2(dx, dz));
                    float pitch = (float) -Math.toDegrees(Math.atan2(dy, distance));
                    
                    // Set rotation
                    livingEntity.getLocation().setYaw(yaw);
                    livingEntity.getLocation().setPitch(pitch);
                }
            }
        });
    }
    
    /**
     * Connect new structure to nearest existing structure with tunnel
     * This creates long 3x3 tunnels that can reach up to 600 blocks
     */
    private void connectToNearbyStructure(World world, Location newStructure) {
        // Find nearest structure within range
        Location nearestStructure = null;
        double nearestDistance = Double.MAX_VALUE;
        
        for (Location existingLoc : structureLocations.keySet()) {
            if (existingLoc.equals(newStructure)) continue;
            if (!existingLoc.getWorld().equals(world)) continue;
            
            double distance = existingLoc.distance(newStructure);
            
            // Only connect if distance is between 100 and 600 blocks (as per requirements)
            if (distance >= 100 && distance <= 600 && distance < nearestDistance) {
                nearestDistance = distance;
                nearestStructure = existingLoc;
            }
        }
        
        // If found nearby structure, create tunnel connecting them
        if (nearestStructure != null) {
            plugin.getLogger().info("Connecting structures with tunnel: " + 
                newStructure.getBlockX() + "," + newStructure.getBlockZ() + " -> " +
                nearestStructure.getBlockX() + "," + nearestStructure.getBlockZ() +
                " (distance: " + (int)nearestDistance + " blocks)");
            
            // Create 3x3 tunnel underground (y=30)
            createConnectingTunnel(world, newStructure, nearestStructure);
        }
    }
    
    /**
     * Create a 3x3 tunnel connecting two structures
     * Optimized to avoid excessive block updates
     */
    private void createConnectingTunnel(World world, Location start, Location end) {
        // Set tunnel depth
        int tunnelY = 30;
        
        // Get start and end points
        int startX = start.getBlockX();
        int startZ = start.getBlockZ();
        int endX = end.getBlockX();
        int endZ = end.getBlockZ();
        
        // Calculate direction
        int dx = endX - startX;
        int dz = endZ - startZ;
        double distance = Math.sqrt(dx * dx + dz * dz);
        
        // Limit tunnel generation to every 5 blocks to reduce workload
        // Normalize direction
        double dirX = dx / distance;
        double dirZ = dz / distance;
        
        // Create tunnel blocks - optimized version
        for (double step = 0; step < distance; step += 5.0) { // Every 5 blocks instead of 1
            int x = (int)(startX + dirX * step);
            int z = (int)(startZ + dirZ * step);
            
            // Create 3x3 cross-section - SIMPLIFIED
            for (int offsetX = -1; offsetX <= 1; offsetX++) {
                for (int offsetY = -1; offsetY <= 1; offsetY++) {
                    for (int offsetZ = -1; offsetZ <= 1; offsetZ++) {
                        Location blockLoc = new Location(world, x + offsetX, tunnelY + offsetY, z + offsetZ);
                        Material currentType = blockLoc.getBlock().getType();
                        
                        // Only carve through natural blocks (limited set)
                        if (currentType == Material.STONE || currentType == Material.DEEPSLATE) {
                            blockLoc.getBlock().setType(Material.AIR, false); // Don't update neighbors
                        }
                    }
                }
            }
        }
        
        // Create entrance from structure to tunnel
        createTunnelEntrance(world, start, tunnelY);
        createTunnelEntrance(world, end, tunnelY);
    }
    
    /**
     * Create entrance from structure down to tunnel
     */
    private void createTunnelEntrance(World world, Location structureLoc, int tunnelY) {
        int x = structureLoc.getBlockX();
        int z = structureLoc.getBlockZ();
        int startY = structureLoc.getBlockY();
        
        // Create 3x3 vertical shaft down to tunnel
        for (int y = startY; y >= tunnelY; y--) {
            for (int offsetX = -1; offsetX <= 1; offsetX++) {
                for (int offsetZ = -1; offsetZ <= 1; offsetZ++) {
                    Location blockLoc = new Location(world, x + offsetX, y, z + offsetZ);
                    blockLoc.getBlock().setType(Material.AIR);
                }
            }
            
            // Add ladder on one wall
            Location ladderLoc = new Location(world, x - 1, y, z);
            ladderLoc.getBlock().setType(Material.LADDER);
        }
    }
    
    /**
     * Get ground level for a location
     */
    private Location getGroundLevel(Location loc) {
        World world = loc.getWorld();
        int y = world.getHighestBlockYAt(loc) - 1;
        
        if (y > 0) {
            Location groundLoc = loc.clone();
            groundLoc.setY(y);
            return groundLoc;
        }
        return null;
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // Optional: trigger horror events when players find structures
        if (plugin.isBypassed(event.getPlayer())) return;
        
        // Could add triggers here when players enter generated structures
    }
    
    /**
     * Pre-generate structures on server startup across all worlds
     * Runs asynchronously to avoid blocking the main thread
     */
    private void preGenerateStructures() {
        try {
            plugin.getLogger().info("Starting initial world pre-generation (async)...");
            
            for (World world : Bukkit.getWorlds()) {
                // Generate initial structures in a 500x500 area around spawn
                Location spawn = world.getSpawnLocation();
                
                plugin.getLogger().info("Pre-generating structures in " + world.getName());
                
                // Generate haunted houses in a grid pattern - SIMPLIFIED (no tunnels yet)
                if (plugin.getConfigManager().getConfig().getBoolean("world-gen.haunted-houses", true)) {
                    for (int x = -250; x < 250; x += 100) {
                        for (int z = -250; z < 250; z += 100) {
                            try {
                                Location genLoc = spawn.clone().add(x, 0, z);
                                
                                // Don't call getGroundLevel - it's sync and loads chunks
                                // Use pre-calculated Y from spawn
                                genLoc.setY(spawn.getBlockY());
                                
                                if (!generatedStructures.contains(genLoc)) {
                                    if (Math.random() < 0.4) { // 40% chance
                                        // Run building generation in main thread
                                        Bukkit.getScheduler().runTask(plugin, () -> {
                                            HauntedHouseGenerator.generateHouse(world, genLoc);
                                        });
                                        generatedStructures.add(genLoc);
                                        structureLocations.put(genLoc, StructureType.HAUNTED_HOUSE);
                                    }
                                }
                                
                                // Small delay to prevent main thread saturation
                                Thread.sleep(5);
                            } catch (Exception e) {
                                // Ignore individual structure generation errors
                            }
                        }
                    }
                }
                
                // Generate secret labs - SIMPLIFIED
                if (plugin.getConfigManager().getConfig().getBoolean("world-gen.secret-labs", true)) {
                    for (int x = -300; x < 300; x += 200) {
                        for (int z = -300; z < 300; z += 200) {
                            try {
                                Location genLoc = spawn.clone().add(x, 0, z);
                                genLoc.setY(spawn.getBlockY());
                                
                                if (!generatedStructures.contains(genLoc)) {
                                    if (Math.random() < 0.25) { // 25% chance
                                        Bukkit.getScheduler().runTask(plugin, () -> {
                                            SecretLabGenerator.generateLaboratory(world, genLoc);
                                        });
                                        generatedStructures.add(genLoc);
                                        structureLocations.put(genLoc, StructureType.SECRET_LAB);
                                    }
                                }
                                
                                Thread.sleep(5);
                            } catch (Exception e) {
                                // Ignore
                            }
                        }
                    }
                }
                
                // Generate sawmills - SIMPLIFIED
                if (plugin.getConfigManager().getConfig().getBoolean("world-gen.sawmills", true)) {
                    for (int x = -280; x < 280; x += 150) {
                        for (int z = -280; z < 280; z += 150) {
                            try {
                                Location genLoc = spawn.clone().add(x, 0, z);
                                genLoc.setY(spawn.getBlockY());
                                
                                if (!generatedStructures.contains(genLoc)) {
                                    if (Math.random() < 0.35) { // 35% chance
                                        Bukkit.getScheduler().runTask(plugin, () -> {
                                            SawmillGenerator.generateSawmill(world, genLoc);
                                        });
                                        generatedStructures.add(genLoc);
                                        structureLocations.put(genLoc, StructureType.SAWMILL);
                                    }
                                }
                                
                                Thread.sleep(5);
                            } catch (Exception e) {
                                // Ignore
                            }
                        }
                    }
                }
                
                // Generate large block formations
                if (plugin.getConfigManager().getConfig().getBoolean("world-gen.large-blocks", true)) {
                    for (int x = -300; x < 300; x += 120) {
                        for (int z = -300; z < 300; z += 120) {
                            try {
                                Location genLoc = spawn.clone().add(x, 25, z);
                                
                                if (!generatedStructures.contains(genLoc)) {
                                    if (Math.random() < 0.3) { // 30% chance
                                        Bukkit.getScheduler().runTask(plugin, () -> {
                                            generateLargeBlocks(world, genLoc);
                                        });
                                        generatedStructures.add(genLoc);
                                    }
                                }
                                
                                Thread.sleep(5);
                            } catch (Exception e) {
                                // Ignore
                            }
                        }
                    }
                }
            }
            
            plugin.getLogger().info("Pre-generation complete! Generated " + generatedStructures.size() + " structures");
            preGenerationComplete = true;
            plugin.getLogger().info("World generation finished. Map is fully populated with structures.");
        } catch (Exception e) {
            plugin.getLogger().warning("Error during pre-generation: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

