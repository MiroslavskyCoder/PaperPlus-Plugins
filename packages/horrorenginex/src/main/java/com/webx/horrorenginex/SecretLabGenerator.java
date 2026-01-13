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
            
            // Clear entire area and create perimeter walls
            clearAndCreateWalls(world, baseX, baseY, baseZ, totalWidth, totalDepth, height);
            
            // Create main entrance from surface
            createEntrance(world, baseX + totalWidth / 2, baseY, baseLocation.getBlockY(), baseZ + 2);
            
            // Create central corridor (main hallway)
            createCentralCorridor(world, baseX, baseY, baseZ, totalWidth, totalDepth);
            
            // Generate director's office (1 room)
            createDirectorOffice(world, baseX + 5, baseY, baseZ + 5);
            
            // Generate scientist rooms (2-6 rooms)
            int scientistRooms = 2 + random.nextInt(5); // 2-6 rooms
            for (int i = 0; i < scientistRooms; i++) {
                int roomX = baseX + 5 + (i % 3) * 12;
                int roomZ = baseZ + 20 + (i / 3) * 12;
                createScientistRoom(world, roomX, baseY, roomZ);
            }
            
            // Generate 6 bathrooms
            for (int i = 0; i < 6; i++) {
                int roomX = baseX + 40 + (i % 2) * 8;
                int roomZ = baseZ + 5 + (i / 2) * 8;
                createBathroom(world, roomX, baseY, roomZ);
            }
            
            // Generate 2 storage rooms
            createStorageRoom(world, baseX + 5, baseY, baseZ + 45);
            createStorageRoom(world, baseX + 20, baseY, baseZ + 45);
            
            // Generate 4 research rooms
            for (int i = 0; i < 4; i++) {
                int roomX = baseX + 25 + (i % 2) * 15;
                int roomZ = baseZ + 5 + (i / 2) * 15;
                createResearchRoom(world, roomX, baseY, roomZ);
            }
            
            // Generate 2 cafeterias with kitchens
            createCafeteria(world, baseX + 5, baseY, baseZ + 30, true);
            createCafeteria(world, baseX + 35, baseY, baseZ + 30, true);
            
            // Generate 3 library rooms
            for (int i = 0; i < 3; i++) {
                int roomX = baseX + 40 + (i * 8);
                int roomZ = baseZ + 25;
                createLibrary(world, roomX, baseY, roomZ);
            }
            
            // Generate 1 rest room (rare)
            if (random.nextDouble() < 0.7) { // 70% chance
                createRestRoom(world, baseX + 45, baseY, baseZ + 40);
            }
            
            // Add lighting throughout
            addLighting(world, baseX, baseY, baseZ, totalWidth, totalDepth, height);
            
        } catch (Exception e) {
            // Silently fail
        }
    }
    
    private static void clearAndCreateWalls(World world, int baseX, int baseY, int baseZ, 
                                           int width, int depth, int height) {
        // Clear space and create walls
        for (int x = baseX; x < baseX + width; x++) {
            for (int z = baseZ; z < baseZ + depth; z++) {
                for (int y = baseY; y < baseY + height; y++) {
                    Block block = world.getBlockAt(x, y, z);
                    
                    // Perimeter walls
                    if (x == baseX || x == baseX + width - 1 || 
                        z == baseZ || z == baseZ + depth - 1) {
                        block.setType(Material.STONE_BRICKS);
                    } else {
                        block.setType(Material.AIR);
                    }
                }
                
                // Floor
                world.getBlockAt(x, baseY - 1, z).setType(Material.POLISHED_ANDESITE);
                
                // Ceiling
                world.getBlockAt(x, baseY + height, z).setType(Material.GRAY_CONCRETE);
            }
        }
    }
    
    private static void createEntrance(World world, int x, int baseY, int surfaceY, int z) {
        // Staircase from surface
        for (int y = baseY; y < surfaceY; y++) {
            world.getBlockAt(x, y, z).setType(Material.AIR);
            world.getBlockAt(x + 1, y, z).setType(Material.AIR);
            world.getBlockAt(x, y, z + 1).setType(Material.STONE_BRICK_STAIRS);
        }
    }
    
    private static void createCentralCorridor(World world, int baseX, int baseY, int baseZ,
                                             int width, int depth) {
        // Main horizontal corridor
        for (int x = baseX + 3; x < baseX + width - 3; x++) {
            for (int z = baseZ + depth / 2 - 2; z < baseZ + depth / 2 + 2; z++) {
                world.getBlockAt(x, baseY, z).setType(Material.POLISHED_ANDESITE);
            }
        }
        
        // Vertical corridors
        for (int z = baseZ + 3; z < baseZ + depth - 3; z++) {
            for (int x = baseX + width / 2 - 2; x < baseX + width / 2 + 2; x++) {
                world.getBlockAt(x, baseY, z).setType(Material.POLISHED_ANDESITE);
            }
        }
    }
    
    private static void createDirectorOffice(World world, int baseX, int baseY, int baseZ) {
        // Room 10x10
        createRoom(world, baseX, baseY, baseZ, 10, 10, Material.DARK_OAK_PLANKS);
        
        // Director's desk
        world.getBlockAt(baseX + 5, baseY + 1, baseZ + 5).setType(Material.CRAFTING_TABLE);
        world.getBlockAt(baseX + 5, baseY + 1, baseZ + 6).setType(Material.CRAFTING_TABLE);
        
        // Chair
        world.getBlockAt(baseX + 5, baseY + 1, baseZ + 4).setType(Material.OAK_STAIRS);
        
        // Bookshelves
        for (int x = baseX + 1; x < baseX + 9; x++) {
            world.getBlockAt(x, baseY + 1, baseZ + 1).setType(Material.BOOKSHELF);
        }
        
        // Safe (chest)
        world.getBlockAt(baseX + 8, baseY + 1, baseZ + 8).setType(Material.CHEST);
        
        // Painting effect (item frame placeholder)
        world.getBlockAt(baseX + 5, baseY + 2, baseZ + 9).setType(Material.GOLD_BLOCK);
    }
    
    private static void createScientistRoom(World world, int baseX, int baseY, int baseZ) {
        // Room 8x8
        createRoom(world, baseX, baseY, baseZ, 8, 8, Material.STONE_BRICKS);
        
        // Workstation
        world.getBlockAt(baseX + 2, baseY + 1, baseZ + 2).setType(Material.CRAFTING_TABLE);
        world.getBlockAt(baseX + 2, baseY + 1, baseZ + 5).setType(Material.BREWING_STAND);
        
        // Storage chest
        world.getBlockAt(baseX + 6, baseY + 1, baseZ + 2).setType(Material.CHEST);
        
        // Lab equipment
        world.getBlockAt(baseX + 4, baseY + 1, baseZ + 4).setType(Material.ENCHANTING_TABLE);
    }
    
    private static void createBathroom(World world, int baseX, int baseY, int baseZ) {
        // Room 5x5
        createRoom(world, baseX, baseY, baseZ, 5, 5, Material.QUARTZ_BLOCK);
        
        // Toilet (cauldron)
        world.getBlockAt(baseX + 2, baseY + 1, baseZ + 2).setType(Material.CAULDRON);
        
        // Sink (cauldron with water)
        world.getBlockAt(baseX + 3, baseY + 1, baseZ + 1).setType(Material.CAULDRON);
    }
    
    private static void createStorageRoom(World world, int baseX, int baseY, int baseZ) {
        // Large room 12x12
        createRoom(world, baseX, baseY, baseZ, 12, 12, Material.STONE_BRICKS);
        
        // Many chests (storage)
        for (int x = baseX + 2; x < baseX + 10; x += 2) {
            for (int z = baseZ + 2; z < baseZ + 10; z += 2) {
                world.getBlockAt(x, baseY + 1, z).setType(Material.CHEST);
            }
        }
        
        // Barrels
        world.getBlockAt(baseX + 3, baseY + 1, baseZ + 3).setType(Material.BARREL);
        world.getBlockAt(baseX + 7, baseY + 1, baseZ + 7).setType(Material.BARREL);
    }
    
    private static void createResearchRoom(World world, int baseX, int baseY, int baseZ) {
        // Room 10x10
        createRoom(world, baseX, baseY, baseZ, 10, 10, Material.IRON_BLOCK);
        
        // Central experiment table
        world.getBlockAt(baseX + 5, baseY + 1, baseZ + 5).setType(Material.ENCHANTING_TABLE);
        
        // Brewing stands around
        world.getBlockAt(baseX + 3, baseY + 1, baseZ + 3).setType(Material.BREWING_STAND);
        world.getBlockAt(baseX + 7, baseY + 1, baseZ + 3).setType(Material.BREWING_STAND);
        world.getBlockAt(baseX + 3, baseY + 1, baseZ + 7).setType(Material.BREWING_STAND);
        world.getBlockAt(baseX + 7, baseY + 1, baseZ + 7).setType(Material.BREWING_STAND);
        
        // Cauldrons
        world.getBlockAt(baseX + 5, baseY + 1, baseZ + 3).setType(Material.CAULDRON);
        world.getBlockAt(baseX + 5, baseY + 1, baseZ + 7).setType(Material.CAULDRON);
        
        // Research notes (chests)
        world.getBlockAt(baseX + 2, baseY + 1, baseZ + 2).setType(Material.CHEST);
        world.getBlockAt(baseX + 8, baseY + 1, baseZ + 8).setType(Material.CHEST);
    }
    
    private static void createCafeteria(World world, int baseX, int baseY, int baseZ, boolean withKitchen) {
        // Dining area 15x12
        createRoom(world, baseX, baseY, baseZ, 15, 12, Material.OAK_PLANKS);
        
        // Tables (crafting tables)
        for (int x = baseX + 2; x < baseX + 13; x += 4) {
            for (int z = baseZ + 2; z < baseZ + 10; z += 3) {
                world.getBlockAt(x, baseY + 1, z).setType(Material.CRAFTING_TABLE);
                
                // Chairs around table
                world.getBlockAt(x - 1, baseY + 1, z).setType(Material.OAK_STAIRS);
                world.getBlockAt(x + 1, baseY + 1, z).setType(Material.OAK_STAIRS);
            }
        }
        
        if (withKitchen) {
            // Kitchen area
            int kitchenX = baseX + 13;
            world.getBlockAt(kitchenX, baseY + 1, baseZ + 2).setType(Material.FURNACE);
            world.getBlockAt(kitchenX, baseY + 1, baseZ + 4).setType(Material.FURNACE);
            world.getBlockAt(kitchenX, baseY + 1, baseZ + 6).setType(Material.CHEST);
            world.getBlockAt(kitchenX, baseY + 1, baseZ + 8).setType(Material.CAULDRON);
        }
    }
    
    private static void createLibrary(World world, int baseX, int baseY, int baseZ) {
        // Room 7x10
        createRoom(world, baseX, baseY, baseZ, 7, 10, Material.DARK_OAK_PLANKS);
        
        // Bookshelves everywhere
        for (int x = baseX + 1; x < baseX + 6; x++) {
            world.getBlockAt(x, baseY + 1, baseZ + 1).setType(Material.BOOKSHELF);
            world.getBlockAt(x, baseY + 1, baseZ + 8).setType(Material.BOOKSHELF);
            world.getBlockAt(x, baseY + 2, baseZ + 1).setType(Material.BOOKSHELF);
            world.getBlockAt(x, baseY + 2, baseZ + 8).setType(Material.BOOKSHELF);
        }
        
        // Reading table
        world.getBlockAt(baseX + 3, baseY + 1, baseZ + 5).setType(Material.CRAFTING_TABLE);
        world.getBlockAt(baseX + 3, baseY + 1, baseZ + 4).setType(Material.OAK_STAIRS);
    }
    
    private static void createRestRoom(World world, int baseX, int baseY, int baseZ) {
        // Luxury room 12x12
        createRoom(world, baseX, baseY, baseZ, 12, 12, Material.GOLD_BLOCK);
        
        // Sofas (stairs)
        world.getBlockAt(baseX + 3, baseY + 1, baseZ + 3).setType(Material.QUARTZ_STAIRS);
        world.getBlockAt(baseX + 8, baseY + 1, baseZ + 3).setType(Material.QUARTZ_STAIRS);
        world.getBlockAt(baseX + 3, baseY + 1, baseZ + 8).setType(Material.QUARTZ_STAIRS);
        world.getBlockAt(baseX + 8, baseY + 1, baseZ + 8).setType(Material.QUARTZ_STAIRS);
        
        // Coffee table
        world.getBlockAt(baseX + 6, baseY + 1, baseZ + 6).setType(Material.CRAFTING_TABLE);
        
        // Plants (dead bush placeholder)
        world.getBlockAt(baseX + 2, baseY + 1, baseZ + 2).setType(Material.DEAD_BUSH);
        world.getBlockAt(baseX + 9, baseY + 1, baseZ + 9).setType(Material.DEAD_BUSH);
        
        // Jukebox for music
        world.getBlockAt(baseX + 10, baseY + 1, baseZ + 2).setType(Material.JUKEBOX);
    }
    
    private static void createRoom(World world, int baseX, int baseY, int baseZ, 
                                   int width, int depth, Material wallMaterial) {
        // Create room walls
        for (int x = baseX; x < baseX + width; x++) {
            for (int z = baseZ; z < baseZ + depth; z++) {
                for (int y = baseY; y < baseY + 4; y++) {
                    Block block = world.getBlockAt(x, y, z);
                    
                    // Walls
                    if (x == baseX || x == baseX + width - 1 || 
                        z == baseZ || z == baseZ + depth - 1) {
                        if (y > baseY) {
                            block.setType(wallMaterial);
                        }
                    } else {
                        block.setType(Material.AIR);
                    }
                }
            }
        }
        
        // Door
        world.getBlockAt(baseX + width / 2, baseY + 1, baseZ).setType(Material.AIR);
        world.getBlockAt(baseX + width / 2, baseY + 2, baseZ).setType(Material.AIR);
    }
    
    private static void addLighting(World world, int baseX, int baseY, int baseZ,
                                    int width, int depth, int height) {
        // Add redstone lamps every 8 blocks
        for (int x = baseX + 4; x < baseX + width - 4; x += 8) {
            for (int z = baseZ + 4; z < baseZ + depth - 4; z += 8) {
                world.getBlockAt(x, baseY + height - 2, z).setType(Material.REDSTONE_LAMP);
                world.getBlockAt(x, baseY + height - 3, z).setType(Material.REDSTONE_BLOCK);
            }
        }
    }
}
