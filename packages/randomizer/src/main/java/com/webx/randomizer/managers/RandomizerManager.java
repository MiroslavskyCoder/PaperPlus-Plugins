package com.webx.randomizer.managers;

import com.webx.randomizer.RandomizerPlugin;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Manager for randomizer functionality
 */
public class RandomizerManager {
    private final RandomizerPlugin plugin;
    private final Random random = new Random();
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    
    // Wood types and sticks only
    private static final Material[] RANDOM_ITEMS = {
        Material.OAK_LOG,
        Material.SPRUCE_LOG,
        Material.BIRCH_LOG,
        Material.JUNGLE_LOG,
        Material.ACACIA_LOG,
        Material.DARK_OAK_LOG,
        Material.MANGROVE_LOG,
        Material.CHERRY_LOG,
        Material.OAK_PLANKS,
        Material.SPRUCE_PLANKS,
        Material.BIRCH_PLANKS,
        Material.JUNGLE_PLANKS,
        Material.ACACIA_PLANKS,
        Material.DARK_OAK_PLANKS,
        Material.MANGROVE_PLANKS,
        Material.CHERRY_PLANKS,
        Material.STICK
    };
    
    private static final long COOLDOWN_TIME = 3 * 60 * 1000; // 3 minutes in milliseconds
    
    public RandomizerManager(RandomizerPlugin plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Get random number between 1 and 1000
     */
    public int getRandomNumber() {
        return random.nextInt(1000) + 1;
    }
    
    /**
     * Get random wood or stick item
     */
    public Material getRandomItem() {
        return RANDOM_ITEMS[random.nextInt(RANDOM_ITEMS.length)];
    }
    
    /**
     * Give random item to player if cooldown passed
     */
    public boolean giveRandomItem(Player player) {
        if (!checkCooldown(player)) {
            long timeLeft = getRemainingCooldown(player);
            long secondsLeft = timeLeft / 1000;
            player.sendMessage("§cВы должны подождать еще " + secondsLeft + " секунд!");
            return false;
        }
        
        Material randomMaterial = getRandomItem();
        int amount = random.nextInt(16) + 1; // 1-16 items
        ItemStack item = new ItemStack(randomMaterial, amount);
        player.getInventory().addItem(item);
        
        player.sendMessage("§aВы получили §f" + amount + "x " + getItemName(randomMaterial) + "§a!");
        
        setCooldown(player);
        return true;
    }
    
    /**
     * Teleport player to random safe surface location
     */
    public boolean randomTeleport(Player player) {
        World world = player.getWorld();
        Location playerLoc = player.getLocation();
        
        // Try to find safe location within 1000 blocks
        for (int i = 0; i < 20; i++) {
            int x = playerLoc.getBlockX() + random.nextInt(2000) - 1000;
            int z = playerLoc.getBlockZ() + random.nextInt(2000) - 1000;
            
            Location safeLoc = findSafeSurfaceLocation(world, x, z);
            if (safeLoc != null) {
                player.teleport(safeLoc);
                player.sendMessage("§aВы телепортированы на случайное место!");
                player.sendMessage("§7Координаты: §fX: " + safeLoc.getBlockX() + 
                                 " Y: " + safeLoc.getBlockY() + 
                                 " Z: " + safeLoc.getBlockZ());
                return true;
            }
        }
        
        player.sendMessage("§cНе удалось найти безопасное место для телепортации!");
        return false;
    }
    
    /**
     * Find safe surface location without monsters nearby
     */
    private Location findSafeSurfaceLocation(World world, int x, int z) {
        // Find highest solid block
        int y = world.getHighestBlockYAt(x, z);
        
        // Make sure it's a solid surface
        Block groundBlock = world.getBlockAt(x, y - 1, z);
        Block feetBlock = world.getBlockAt(x, y, z);
        Block headBlock = world.getBlockAt(x, y + 1, z);
        
        // Check if it's safe to stand
        if (!groundBlock.getType().isSolid()) return null;
        if (feetBlock.getType().isSolid()) return null;
        if (headBlock.getType().isSolid()) return null;
        
        // Check if it's not in water or lava
        if (feetBlock.isLiquid() || headBlock.isLiquid()) return null;
        
        Location loc = new Location(world, x + 0.5, y, z + 0.5);
        
        // Check for monsters nearby (10 block radius)
        if (hasMonstersNearby(loc, 10)) return null;
        
        // Set proper direction
        loc.setYaw(random.nextFloat() * 360);
        loc.setPitch(0);
        
        return loc;
    }
    
    /**
     * Check if there are monsters nearby
     */
    private boolean hasMonstersNearby(Location location, double radius) {
        Collection<Entity> entities = location.getWorld().getNearbyEntities(
            location, radius, radius, radius
        );
        
        for (Entity entity : entities) {
            if (entity instanceof Monster) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check if player cooldown expired
     */
    public boolean checkCooldown(Player player) {
        if (!cooldowns.containsKey(player.getUniqueId())) {
            return true;
        }
        
        long lastUsed = cooldowns.get(player.getUniqueId());
        long currentTime = System.currentTimeMillis();
        
        return (currentTime - lastUsed) >= COOLDOWN_TIME;
    }
    
    /**
     * Set cooldown for player
     */
    public void setCooldown(Player player) {
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
    }
    
    /**
     * Get remaining cooldown time in milliseconds
     */
    public long getRemainingCooldown(Player player) {
        if (!cooldowns.containsKey(player.getUniqueId())) {
            return 0;
        }
        
        long lastUsed = cooldowns.get(player.getUniqueId());
        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - lastUsed;
        
        if (elapsed >= COOLDOWN_TIME) {
            return 0;
        }
        
        return COOLDOWN_TIME - elapsed;
    }
    
    /**
     * Get readable item name
     */
    private String getItemName(Material material) {
        return material.name().toLowerCase().replace("_", " ");
    }
}
