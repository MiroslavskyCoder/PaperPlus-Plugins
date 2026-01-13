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
    private BukkitTask rainTask;
    private BukkitTask animalWatcherTask;
    private static final int STRUCTURE_SEARCH_RADIUS = 100;
    private static final int GENERATION_CHECK_INTERVAL = 40; // 2 seconds
    
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
        
        // Start structure generation around players
        startStructureGeneration();
        
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
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
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
                    // 5% chance to generate a house
                    if (Math.random() < 0.05) {
                        HauntedHouseGenerator.generateHouse(world, checkLoc);
                        generatedStructures.add(checkLoc);
                    }
                }
            }
        }
    }
    
    /**
     * Try to generate a cave tunnel system
     */
    private void tryGenerateCaveTunnel(World world, Location playerLoc) {
        for (int dx = -STRUCTURE_SEARCH_RADIUS; dx < STRUCTURE_SEARCH_RADIUS; dx += 60) {
            for (int dz = -STRUCTURE_SEARCH_RADIUS; dz < STRUCTURE_SEARCH_RADIUS; dz += 60) {
                Location checkLoc = playerLoc.clone().add(dx, 0, dz);
                checkLoc.setY(30); // Underground
                
                if (!generatedStructures.contains(checkLoc)) {
                    // 3% chance to generate tunnel system
                    if (Math.random() < 0.03) {
                        CaveNetworkGenerator.generateTunnelNetwork(world, checkLoc);
                        generatedStructures.add(checkLoc);
                    }
                }
            }
        }
    }
    
    /**
     * Try to generate large block formations
     */
    private void tryGenerateLargeBlocks(World world, Location playerLoc) {
        for (int dx = -STRUCTURE_SEARCH_RADIUS; dx < STRUCTURE_SEARCH_RADIUS; dx += 80) {
            for (int dz = -STRUCTURE_SEARCH_RADIUS; dz < STRUCTURE_SEARCH_RADIUS; dz += 80) {
                Location checkLoc = playerLoc.clone().add(dx, 25, dz);
                
                if (!generatedStructures.contains(checkLoc)) {
                    // 8% chance to generate large blocks
                    if (Math.random() < 0.08) {
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
                    // 2% chance to generate secret lab
                    if (Math.random() < 0.02) {
                        SecretLabGenerator.generateLaboratory(world, checkLoc);
                        generatedStructures.add(checkLoc);
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
                    // 4% chance to generate sawmill
                    if (Math.random() < 0.04) {
                        SawmillGenerator.generateSawmill(world, checkLoc);
                        generatedStructures.add(checkLoc);
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
        
        // Find all entities within 20 blocks
        playerLoc.getWorld().getNearbyEntities(playerLoc, 20, 20, 20).forEach(entity -> {
            if (entity instanceof org.bukkit.entity.Pig ||
                entity instanceof org.bukkit.entity.Cow ||
                entity instanceof org.bukkit.entity.Sheep) {
                
                // Make animal look at player
                if (entity instanceof org.bukkit.entity.LivingEntity) {
                    org.bukkit.entity.LivingEntity livingEntity = (org.bukkit.entity.LivingEntity) entity;
                    
                    // Rotate head toward player
                    Location entityLoc = entity.getLocation();
                    Location playerEyeLoc = player.getEyeLocation();
                    
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
}
