package com.webx.horrorenginex;

import org.bukkit.*;
import org.bukkit.block.Block;

/**
 * Generates sawmill structures
 */
public class SawmillGenerator {
    
    /**
     * Generate a sawmill building
     */
    public static void generateSawmill(World world, Location baseLocation) {
        try {
            int baseX = baseLocation.getBlockX();
            int baseY = baseLocation.getBlockY();
            int baseZ = baseLocation.getBlockZ();
            
            // Sawmill dimensions (12x8x10)
            int width = 12;
            int height = 8;
            int depth = 10;
            
            // Main structure - spruce wood
            for (int x = baseX; x < baseX + width; x++) {
                for (int z = baseZ; z < baseZ + depth; z++) {
                    for (int y = baseY; y < baseY + height; y++) {
                        Block block = world.getBlockAt(x, y, z);
                        
                        // Walls (spruce planks)
                        if (x == baseX || x == baseX + width - 1 || 
                            z == baseZ || z == baseZ + depth - 1) {
                            block.setType(Material.SPRUCE_PLANKS);
                            
                            // Windows (occasional air blocks)
                            if (y > baseY + 1 && y < baseY + height - 2 && Math.random() < 0.2) {
                                block.setType(Material.AIR);
                            }
                        }
                    }
                    
                    // Floor (oak planks)
                    world.getBlockAt(x, baseY - 1, z).setType(Material.OAK_PLANKS);
                    
                    // Roof (spruce slabs)
                    world.getBlockAt(x, baseY + height, z).setType(Material.SPRUCE_SLAB);
                }
            }
            
            // Add entrance door
            int doorX = baseX + width / 2;
            int doorZ = baseZ;
            world.getBlockAt(doorX, baseY, doorZ).setType(Material.AIR);
            world.getBlockAt(doorX, baseY + 1, doorZ).setType(Material.AIR);
            
            // Add sawmill equipment inside
            int interiorY = baseY + 1;
            
            // Large saw blade (fence and iron bars)
            int sawX = baseX + width / 2;
            int sawZ = baseZ + depth / 2;
            
            // Vertical saw blade
            for (int y = interiorY; y < interiorY + 3; y++) {
                world.getBlockAt(sawX, y, sawZ).setType(Material.IRON_BARS);
                world.getBlockAt(sawX + 1, y, sawZ).setType(Material.IRON_BARS);
            }
            
            // Saw base (stonecutter for effect)
            world.getBlockAt(sawX, interiorY - 1, sawZ).setType(Material.STONECUTTER);
            world.getBlockAt(sawX + 1, interiorY - 1, sawZ).setType(Material.STONECUTTER);
            
            // Log storage piles
            for (int x = baseX + 2; x < baseX + 5; x++) {
                for (int z = baseZ + 2; z < baseZ + 4; z++) {
                    world.getBlockAt(x, interiorY, z).setType(Material.OAK_LOG);
                    if (Math.random() < 0.5) {
                        world.getBlockAt(x, interiorY + 1, z).setType(Material.SPRUCE_LOG);
                    }
                }
            }
            
            // More log piles in another corner
            for (int x = baseX + width - 5; x < baseX + width - 2; x++) {
                for (int z = baseZ + depth - 4; z < baseZ + depth - 2; z++) {
                    world.getBlockAt(x, interiorY, z).setType(Material.BIRCH_LOG);
                    if (Math.random() < 0.5) {
                        world.getBlockAt(x, interiorY + 1, z).setType(Material.DARK_OAK_LOG);
                    }
                }
            }
            
            // Workbenches
            world.getBlockAt(baseX + 3, interiorY, baseZ + 7).setType(Material.CRAFTING_TABLE);
            world.getBlockAt(baseX + width - 4, interiorY, baseZ + 7).setType(Material.CRAFTING_TABLE);
            
            // Tool storage chests
            world.getBlockAt(baseX + 2, interiorY, baseZ + depth - 3).setType(Material.CHEST);
            world.getBlockAt(baseX + width - 3, interiorY, baseZ + depth - 3).setType(Material.CHEST);
            
            // Sawdust piles (coarse dirt)
            for (int i = 0; i < 8; i++) {
                int dustX = baseX + 2 + (int)(Math.random() * (width - 4));
                int dustZ = baseZ + 2 + (int)(Math.random() * (depth - 4));
                world.getBlockAt(dustX, interiorY - 1, dustZ).setType(Material.COARSE_DIRT);
            }
            
            // Add abandoned tools (iron blocks as machinery)
            world.getBlockAt(baseX + 7, interiorY, baseZ + 3).setType(Material.IRON_BLOCK);
            world.getBlockAt(baseX + 7, interiorY + 1, baseZ + 3).setType(Material.ANVIL);
            
            // Cobwebs (abandoned feel)
            for (int i = 0; i < 5; i++) {
                int webX = baseX + 1 + (int)(Math.random() * (width - 2));
                int webY = interiorY + (int)(Math.random() * 3);
                int webZ = baseZ + 1 + (int)(Math.random() * (depth - 2));
                world.getBlockAt(webX, webY, webZ).setType(Material.COBWEB);
            }
            
            // Exterior log pile
            for (int x = baseX - 2; x < baseX; x++) {
                for (int z = baseZ + 2; z < baseZ + 6; z++) {
                    world.getBlockAt(x, baseY, z).setType(Material.OAK_LOG);
                    if (Math.random() < 0.3) {
                        world.getBlockAt(x, baseY + 1, z).setType(Material.SPRUCE_LOG);
                    }
                }
            }
            
            // Add fence around the building
            for (int x = baseX - 1; x < baseX + width + 1; x++) {
                world.getBlockAt(x, baseY, baseZ - 1).setType(Material.OAK_FENCE);
                world.getBlockAt(x, baseY, baseZ + depth).setType(Material.OAK_FENCE);
            }
            for (int z = baseZ; z < baseZ + depth; z++) {
                world.getBlockAt(baseX - 1, baseY, z).setType(Material.OAK_FENCE);
                world.getBlockAt(baseX + width, baseY, z).setType(Material.OAK_FENCE);
            }
            
        } catch (Exception e) {
            // Silently fail
        }
    }
}
