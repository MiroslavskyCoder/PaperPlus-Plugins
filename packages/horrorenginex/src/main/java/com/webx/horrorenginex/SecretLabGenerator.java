package com.webx.horrorenginex;

import org.bukkit.*;
import org.bukkit.block.Block;
import java.util.Random;

/**
 * Generates secret underground laboratories with multiple rooms
 */
public class SecretLabGenerator {
    
    private static final Random random = new Random();
    
    /**
     * Generate a large multi-room laboratory underground
     */
    public static void generateLaboratory(World world, Location baseLocation) {
        try {
            int baseX = baseLocation.getBlockX();
            int baseY = baseLocation.getBlockY() - 15; // 15 blocks underground
            int baseZ = baseLocation.getBlockZ();
            
            // Large laboratory complex (60x60x12)
            int totalWidth = 60;
            int totalDepth = 60;
            int height = 12;
            
            // Clear space and create walls
            for (int x = baseX; x < baseX + width; x++) {
                for (int z = baseZ; z < baseZ + depth; z++) {
                    for (int y = baseY; y < baseY + height; y++) {
                        Block block = world.getBlockAt(x, y, z);
                        
                        // Walls (iron blocks and concrete)
                        if (x == baseX || x == baseX + width - 1 || 
                            z == baseZ || z == baseZ + depth - 1) {
                            block.setType(Material.IRON_BLOCK);
                        } else {
                            // Clear interior
                            block.setType(Material.AIR);
                        }
                    }
                    
                    // Floor (smooth stone)
                    world.getBlockAt(x, baseY - 1, z).setType(Material.SMOOTH_STONE);
                    
                    // Ceiling (concrete)
                    world.getBlockAt(x, baseY + height, z).setType(Material.GRAY_CONCRETE);
                }
            }
            
            // Add entrance staircase from surface
            int entranceX = baseX + width / 2;
            int entranceZ = baseZ;
            for (int y = baseY; y < baseLocation.getBlockY(); y++) {
                world.getBlockAt(entranceX, y, entranceZ).setType(Material.AIR);
                world.getBlockAt(entranceX + 1, y, entranceZ).setType(Material.AIR);
                
                // Stairs
                if (y < baseLocation.getBlockY() - 1) {
                    world.getBlockAt(entranceX, y, entranceZ + 1).setType(Material.STONE_BRICK_STAIRS);
                }
            }
            
            // Add lab equipment (brewing stands, cauldrons, etc.)
            int equipY = baseY + 1;
            
            // Brewing stands in corners
            world.getBlockAt(baseX + 2, equipY, baseZ + 2).setType(Material.BREWING_STAND);
            world.getBlockAt(baseX + width - 3, equipY, baseZ + 2).setType(Material.BREWING_STAND);
            world.getBlockAt(baseX + 2, equipY, baseZ + depth - 3).setType(Material.BREWING_STAND);
            world.getBlockAt(baseX + width - 3, equipY, baseZ + depth - 3).setType(Material.BREWING_STAND);
            
            // Cauldrons
            world.getBlockAt(baseX + 5, equipY, baseZ + 5).setType(Material.CAULDRON);
            world.getBlockAt(baseX + 10, equipY, baseZ + 5).setType(Material.CAULDRON);
            world.getBlockAt(baseX + 5, equipY, baseZ + 10).setType(Material.CAULDRON);
            
            // Enchanting table
            world.getBlockAt(baseX + width / 2, equipY, baseZ + depth / 2).setType(Material.ENCHANTING_TABLE);
            
            // Bookshelves around center
            for (int i = 0; i < 8; i++) {
                int angle = i * 45;
                int offsetX = (int)(Math.cos(Math.toRadians(angle)) * 3);
                int offsetZ = (int)(Math.sin(Math.toRadians(angle)) * 3);
                world.getBlockAt(baseX + width/2 + offsetX, equipY, baseZ + depth/2 + offsetZ)
                    .setType(Material.BOOKSHELF);
            }
            
            // Add chests with "research notes"
            world.getBlockAt(baseX + 3, equipY, baseZ + 7).setType(Material.CHEST);
            world.getBlockAt(baseX + width - 4, equipY, baseZ + 7).setType(Material.CHEST);
            
            // Add redstone lamps for lighting
            for (int x = baseX + 3; x < baseX + width - 3; x += 4) {
                for (int z = baseZ + 3; z < baseZ + depth - 3; z += 4) {
                    world.getBlockAt(x, baseY + height - 2, z).setType(Material.REDSTONE_LAMP);
                    world.getBlockAt(x, baseY + height - 3, z).setType(Material.REDSTONE_BLOCK);
                }
            }
            
            // Add suspicious blocks (dispensers, observers)
            world.getBlockAt(baseX + 7, equipY + 2, baseZ + 2).setType(Material.DISPENSER);
            world.getBlockAt(baseX + 8, equipY + 2, baseZ + 2).setType(Material.OBSERVER);
            
            // Secret chamber with loot
            int secretX = baseX + width - 5;
            int secretZ = baseZ + depth - 5;
            for (int x = secretX; x < secretX + 3; x++) {
                for (int z = secretZ; z < secretZ + 3; z++) {
                    world.getBlockAt(x, equipY, z).setType(Material.NETHERITE_BLOCK);
                }
            }
            world.getBlockAt(secretX + 1, equipY + 1, secretZ + 1).setType(Material.CHEST);
            
        } catch (Exception e) {
            // Silently fail
        }
    }
}
