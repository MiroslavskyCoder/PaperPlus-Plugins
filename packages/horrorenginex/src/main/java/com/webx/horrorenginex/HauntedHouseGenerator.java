package com.webx.horrorenginex;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

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
            
            // Random size: small (12x12) or large (20x20)
            boolean isLarge = Math.random() < 0.4; // 40% chance for large house
            int width = isLarge ? 20 : 12;
            int depth = isLarge ? 20 : 12;
            
            // Random floors (1-6 as per requirements)
            int floors = (int)(Math.random() * 6) + 1;
            int floorHeight = 4; // 4 blocks per floor
            
            // Build each floor
            for (int floor = 0; floor < floors; floor++) {
                int floorY = baseY + (floor * floorHeight);
                
                // Build walls
                for (int x = baseX; x < baseX + width; x++) {
                    for (int z = baseZ; z < baseZ + depth; z++) {
                        // Floor
                        world.getBlockAt(x, floorY, z).setType(Material.DARK_OAK_PLANKS);
                        
                        // Ceiling
                        world.getBlockAt(x, floorY + 3, z).setType(Material.DARK_OAK_PLANKS);
                        
                        // Walls
                        if (x == baseX || x == baseX + width - 1 || 
                            z == baseZ || z == baseZ + depth - 1) {
                            for (int y = floorY + 1; y <= floorY + 3; y++) {
                                world.getBlockAt(x, y, z).setType(Material.DARK_OAK_LOG);
                            }
                        } else {
                            // Clear interior
                            for (int y = floorY + 1; y < floorY + 3; y++) {
                                world.getBlockAt(x, y, z).setType(Material.AIR);
                            }
                        }
                    }
                }
                
                // Add windows
                for (int side = 0; side < 4; side++) {
                    int windowCount = isLarge ? 4 : 2;
                    for (int i = 0; i < windowCount; i++) {
                        int windowX, windowZ;
                        
                        if (side == 0) { // North wall
                            windowX = baseX + 3 + i * (width / windowCount);
                            windowZ = baseZ;
                        } else if (side == 1) { // South wall
                            windowX = baseX + 3 + i * (width / windowCount);
                            windowZ = baseZ + depth - 1;
                        } else if (side == 2) { // West wall
                            windowX = baseX;
                            windowZ = baseZ + 3 + i * (depth / windowCount);
                        } else { // East wall
                            windowX = baseX + width - 1;
                            windowZ = baseZ + 3 + i * (depth / windowCount);
                        }
                        
                        // Random broken window (20% chance)
                        if (Math.random() < 0.2) {
                            world.getBlockAt(windowX, floorY + 2, windowZ).setType(Material.AIR);
                        } else {
                            world.getBlockAt(windowX, floorY + 2, windowZ).setType(Material.GLASS_PANE);
                        }
                    }
                }
                
                // Add cobwebs (more in large houses)
                int cobwebCount = isLarge ? 8 : 5;
                for (int i = 0; i < cobwebCount; i++) {
                    int webX = baseX + 2 + (int)(Math.random() * (width - 4));
                    int webZ = baseZ + 2 + (int)(Math.random() * (depth - 4));
                    int webY = floorY + 1 + (int)(Math.random() * 2);
                    
                    if (Math.random() < 0.3) {
                        world.getBlockAt(webX, webY, webZ).setType(Material.COBWEB);
                    }
                }
                
                // Add furniture in large houses
                if (isLarge && floor < floors - 1) {
                    // Tables
                    world.getBlockAt(baseX + 5, floorY + 1, baseZ + 5).setType(Material.CRAFTING_TABLE);
                    world.getBlockAt(baseX + width - 6, floorY + 1, baseZ + depth - 6).setType(Material.CRAFTING_TABLE);
                    
                    // Chairs (stairs)
                    world.getBlockAt(baseX + 5, floorY + 1, baseZ + 4).setType(Material.OAK_STAIRS);
                    world.getBlockAt(baseX + width - 6, floorY + 1, baseZ + depth - 5).setType(Material.OAK_STAIRS);
                }
                
                // Add treasure chest on random floor
                if (floor == (int)(Math.random() * floors)) {
                    int chestX = baseX + 2 + (int)(Math.random() * (width - 4));
                    int chestZ = baseZ + 2 + (int)(Math.random() * (depth - 4));
                    Block chestBlock = world.getBlockAt(chestX, floorY + 1, chestZ);
                    chestBlock.setType(Material.CHEST);
                    addNotesToChest(chestBlock);
                }
            }
            
            // Add roof
            int roofY = baseY + (floors * floorHeight);
            for (int x = baseX; x <= baseX + width; x++) {
                for (int z = baseZ; z <= baseZ + depth; z++) {
                    world.getBlockAt(x, roofY, z).setType(Material.DARK_OAK_PLANKS);
                }
            }
            
            // Add stone cross above the house
            generateStoneCross(world, baseX + width / 2, roofY + 2, baseZ + depth / 2);
            
        } catch (Exception e) {
            // Silently fail if generation issue occurs
        }
    }
    
    /**
     * Generate a stone cross above the house
     * Gothic/cemetery style cross made of stone bricks
     */
    private static void generateStoneCross(World world, int centerX, int baseY, int centerZ) {
        try {
            // Cross dimensions
            int height = 8 + (int)(Math.random() * 4); // 8-12 blocks tall
            int armWidth = 1;
            int armLength = 3;
            
            // Vertical beam
            for (int y = baseY; y < baseY + height; y++) {
                world.getBlockAt(centerX, y, centerZ).setType(Material.STONE_BRICKS);
            }
            
            // Horizontal beam (shorter, positioned higher)
            int beamY = baseY + height / 3;
            for (int x = centerX - armLength; x <= centerX + armLength; x++) {
                for (int z = centerZ - armWidth; z <= centerZ + armWidth; z++) {
                    world.getBlockAt(x, beamY, z).setType(Material.STONE_BRICKS);
                }
            }
            
            // Top point/cross top
            world.getBlockAt(centerX, baseY + height, centerZ).setType(Material.STONE_BRICK_STAIRS);
            
            // Base of cross (wider support)
            for (int x = centerX - 2; x <= centerX + 2; x++) {
                for (int z = centerZ - 2; z <= centerZ + 2; z++) {
                    if (world.getBlockAt(x, baseY - 1, z).getType() == Material.AIR) {
                        world.getBlockAt(x, baseY - 1, z).setType(Material.STONE_BRICKS);
                    }
                }
            }
            
            // Add moss for aged effect (30% chance)
            if (Math.random() < 0.3) {
                // Add some mossy stone bricks
                for (int y = baseY; y < baseY + height; y += 2) {
                    if (Math.random() < 0.5) {
                        world.getBlockAt(centerX, y, centerZ).setType(Material.MOSSY_STONE_BRICKS);
                    }
                }
            }
            
            // Add creeping vines occasionally
            if (Math.random() < 0.2) {
                // Vines on the sides
                for (int y = baseY; y < baseY + height; y += 3) {
                    world.getBlockAt(centerX - 1, y, centerZ).setType(Material.TWISTING_VINES);
                    world.getBlockAt(centerX + 1, y, centerZ).setType(Material.TWISTING_VINES);
                }
            }
            
        } catch (Exception e) {
            // Silently fail
        }
    }
    
    /**
     * Add horror-themed notes (written books) to chest
     */
    private static void addNotesToChest(Block chestBlock) {
        try {
            if (!(chestBlock.getState() instanceof Chest)) {
                return;
            }
            
            Chest chest = (Chest) chestBlock.getState();
            
            // Create written book with horror story
            ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
            BookMeta meta = (BookMeta) book.getItemMeta();
            
            // Random note selection
            String[] titles = {
                "Последняя запись",
                "Дневник неизвестного",
                "Предупреждение",
                "Не читай это",
                "Записи исследователя"
            };
            
            String[][] pages = {
                {
                    "§0День 1:\n§0Я нашёл это место случайно. Здесь так тихо... слишком тихо.",
                    "§0День 3:\n§0Животные ведут себя странно. Они просто... смотрят на меня.",
                    "§0День 7:\n§0Я больше не могу спать. Что-то не так с этим местом.\n\n§0Уходи отсюда."
                },
                {
                    "§0Если ты читаешь это...\n\n§0Беги.\n\n§0Не оставайся здесь после наступления темноты.",
                    "§0Они наблюдают.\n§0Всегда наблюдают.\n\n§0Не смотри в окна по ночам."
                },
                {
                    "§0Лаборатория под землёй.\n§0Туннели соединяют всё.\n\n§0Не спускайся туда.",
                    "§0Я видел их эксперименты.\n§0Это было ошибкой.\n\n§0§lБЕГИ!"
                },
                {
                    "§0Дождь не прекращается.\n§0Уже 40 дней.\n\n§0Это не естественно.",
                    "§0Животные... они говорили со мной.\n\n§0Я схожу с ума?"
                },
                {
                    "§0ВНИМАНИЕ:\n§0Не входить в подземные комплексы.\n\n§0Опасность заражения.",
                    "§0Эвакуация отменена.\n§0Никто не пришёл.\n\n§0Мы одни."
                }
            };
            
            int noteIndex = (int)(Math.random() * titles.length);
            meta.setTitle(titles[noteIndex]);
            meta.setAuthor("???");
            
            for (String page : pages[noteIndex]) {
                meta.addPage(page);
            }
            
            book.setItemMeta(meta);
            
            // Add book to random slot
            int slot = (int)(Math.random() * 27);
            chest.getInventory().setItem(slot, book);
            
            // Sometimes add additional creepy items
            if (Math.random() < 0.3) {
                chest.getInventory().addItem(new ItemStack(Material.BONE, 1 + (int)(Math.random() * 5)));
            }
            if (Math.random() < 0.2) {
                chest.getInventory().addItem(new ItemStack(Material.SPIDER_EYE, 1 + (int)(Math.random() * 3)));
            }
            
            chest.update();
            
        } catch (Exception e) {
            // Silently fail
        }
    }
}
