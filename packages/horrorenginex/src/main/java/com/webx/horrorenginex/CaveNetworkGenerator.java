package com.webx.horrorenginex;

import org.bukkit.*;

/**
 * Generates cave tunnel systems
 */
public class CaveNetworkGenerator {
    
    /**
     * Generate a network of cave tunnels (3x3 passages)
     */
    public static void generateTunnelNetwork(World world, Location startLocation) {
        try {
            int startX = startLocation.getBlockX();
            int startY = startLocation.getBlockY();
            int startZ = startLocation.getBlockZ();
            
            // Generate main tunnel (3x3 cross-section)
            // Length: 100-600 blocks
            int tunnelLength = (int)(Math.random() * 500) + 100;
            
            // Choose direction (X or Z)
            boolean xDirection = Math.random() < 0.5;
            
            for (int i = 0; i < tunnelLength; i++) {
                int x, y, z;
                
                if (xDirection) {
                    x = startX + i;
                    z = startZ;
                } else {
                    x = startX;
                    z = startZ + i;
                }
                
                // Carve out 3x3 tunnel
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = 0; dy < 3; dy++) {
                        for (int dz = -1; dz <= 1; dz++) {
                            Block block = world.getBlockAt(x + dx, startY + dy, z + dz);
                            
                            // Only carve stone and similar blocks
                            if (isCarveableBlock(block.getType())) {
                                block.setType(Material.CAVE_AIR);
                                
                                // Occasional stalactites/stalagmites
                                if (dy == 2 && Math.random() < 0.1) {
                                    block.setType(Material.POINTED_DRIPSTONE);
                                } else if (dy == 0 && Math.random() < 0.1) {
                                    block.setType(Material.POINTED_DRIPSTONE);
                                }
                            }
                        }
                    }
                }
                
                // Random branches (10% chance every block)
                if (Math.random() < 0.1) {
                    int branchLength = (int)(Math.random() * 50) + 20;
                    generateBranch(world, startX + (xDirection ? i : 0), 
                                  startY, startZ + (xDirection ? 0 : i), branchLength);
                }
            }
            
            // Add cave lava pool at the end
            int poolX = xDirection ? startX + tunnelLength - 10 : startX;
            int poolZ = xDirection ? startZ : startZ + tunnelLength - 10;
            generateLavaPool(world, poolX, startY, poolZ);
            
        } catch (Exception e) {
            // Silently fail
        }
    }
    
    /**
     * Generate a branch off the main tunnel
     */
    private static void generateBranch(World world, int baseX, int baseY, int baseZ, int length) {
        boolean branchX = Math.random() < 0.5;
        
        for (int i = 0; i < length; i++) {
            int x = baseX + (branchX ? i : (int)(Math.random() * 3 - 1));
            int z = baseZ + (branchX ? (int)(Math.random() * 3 - 1) : i);
            
            // 2x2 branch
            for (int dx = 0; dx < 2; dx++) {
                for (int dy = 0; dy < 2; dy++) {
                    for (int dz = 0; dz < 2; dz++) {
                        Block block = world.getBlockAt(x + dx, baseY + dy, z + dz);
                        if (isCarveableBlock(block.getType())) {
                            block.setType(Material.CAVE_AIR);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Generate a lava pool (small cave lake effect)
     */
    private static void generateLavaPool(World world, int centerX, int centerY, int centerZ) {
        int poolRadius = (int)(Math.random() * 5) + 3;
        
        for (int x = centerX - poolRadius; x <= centerX + poolRadius; x++) {
            for (int z = centerZ - poolRadius; z <= centerZ + poolRadius; z++) {
                if (Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(z - centerZ, 2)) <= poolRadius) {
                    world.getBlockAt(x, centerY, z).setType(Material.LAVA);
                }
            }
        }
    }
    
    /**
     * Check if a block can be carved (stone variants)
     */
    private static boolean isCarveableBlock(Material type) {
        return type == Material.STONE || type == Material.DEEPSLATE ||
               type == Material.GRANITE || type == Material.DIORITE ||
               type == Material.ANDESITE || type == Material.TUFF ||
               type == Material.DIRT || type == Material.GRAVEL ||
               type == Material.CALCITE || type == Material.BLACKSTONE;
    }
}
