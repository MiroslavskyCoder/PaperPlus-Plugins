package com.webx.horrorenginex;

import org.bukkit.*;
import org.bukkit.block.Block;

/**
 * Generates haunted house structures
 */
public class HauntedHouseGenerator {
    
    /**
     * Generate a haunted house at the given location
     */
    public static void generateHouse(World world, Location baseLocation) {
        try {
            int baseX = baseLocation.getBlockX();
            int baseY = baseLocation.getBlockY();
            int baseZ = baseLocation.getBlockZ();
            
            // Random house height (1-6 floors)
            int floors = (int)(Math.random() * 6) + 1;
            int floorHeight = 4; // 4 blocks per floor
            
            // Main structure - dark wood
            for (int floor = 0; floor < floors; floor++) {
                int floorY = baseY + (floor * floorHeight);
                
                // Create 10x10 floor
                for (int x = baseX; x < baseX + 10; x++) {
                    for (int z = baseZ; z < baseZ + 10; z++) {
                        // Walls
                        if (x == baseX || x == baseX + 9 || z == baseZ || z == baseZ + 9) {
                            Block block = world.getBlockAt(x, floorY, z);
                            block.setType(Material.DARK_OAK_LOG);
                            
                            // Occasional broken windows
                            if (Math.random() < 0.1) {
                                block.setType(Material.AIR);
                            }
                        }
                        
                        // Ceiling/floor
                        Block floor_block = world.getBlockAt(x, floorY - 1, z);
                        if (floor_block.getType() == Material.AIR) {
                            floor_block.setType(Material.DARK_OAK_PLANKS);
                        }
                        
                        Block ceiling = world.getBlockAt(x, floorY + floorHeight - 1, z);
                        ceiling.setType(Material.DARK_OAK_PLANKS);
                    }
                }
                
                // Add spiderwebs
                if (Math.random() < 0.3) {
                    int webX = baseX + (int)(Math.random() * 10);
                    int webZ = baseZ + (int)(Math.random() * 10);
                    int webY = floorY + (int)(Math.random() * floorHeight);
                    world.getBlockAt(webX, webY, webZ).setType(Material.COBWEB);
                }
            }
            
            // Add roof
            int roofY = baseY + (floors * floorHeight);
            for (int x = baseX - 1; x <= baseX + 10; x++) {
                for (int z = baseZ - 1; z <= baseZ + 10; z++) {
                    if (Math.abs(x - baseX - 4) + Math.abs(z - baseZ - 4) <= 6) {
                        world.getBlockAt(x, roofY, z).setType(Material.DARK_OAK_STAIRS);
                    }
                }
            }
            
            // Add treasure chest on random floor with notes
            int treasureFloor = (int)(Math.random() * floors);
            int treasureY = baseY + (treasureFloor * floorHeight) + 1;
            int treasureX = baseX + (int)(Math.random() * 10);
            int treasureZ = baseZ + (int)(Math.random() * 10);
            
            Block chestBlock = world.getBlockAt(treasureX, treasureY, treasureZ);
            if (chestBlock.getType() == Material.AIR) {
                chestBlock.setType(Material.CHEST);
                
                // Add items and notes (would need full implementation)
            }
            
        } catch (Exception e) {
            // Silently fail if generation issue occurs
        }
    }
}
