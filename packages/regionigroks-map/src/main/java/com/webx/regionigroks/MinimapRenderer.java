package com.webx.regionigroks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MapPalette;
import org.bukkit.HeightMap;

public class MinimapRenderer extends MapRenderer {
    private final RegionigroksMapPlugin plugin;

    public MinimapRenderer(RegionigroksMapPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void render(MapView map, MapCanvas canvas, Player player) {
        int width = 128;
        int height = 128;
        int cx = width / 2;
        int cy = height / 2;
        Location playerLoc = player.getLocation();
        World world = playerLoc.getWorld();

        // Sample terrain: 64 blocks in each direction (1 block = 1 pixel)
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int worldX = playerLoc.getBlockX() + (x - cx);
                int worldZ = playerLoc.getBlockZ() + (y - cy);
                // Use world surface heightmap so we only render what is visible on top.
                Block surface = world.getHighestBlockAt(worldX, worldZ, HeightMap.WORLD_SURFACE);
                byte color = getBlockColor(surface.getType());
                canvas.setPixel(x, y, color);
            }
        }

        // Plot other players as white dots
        byte playerDot = MapPalette.matchColor(255, 255, 255);
        for (Player other : world.getPlayers()) {
            if (other.equals(player)) continue;
            Location o = other.getLocation();
            double dx = o.getX() - playerLoc.getX();
            double dz = o.getZ() - playerLoc.getZ();
            if (Math.abs(dx) > 64 || Math.abs(dz) > 64) continue;
            int px = cx + (int) Math.round(dx);
            int py = cy + (int) Math.round(dz);
            if (px >= 0 && px < width && py >= 0 && py < height) {
                canvas.setPixel(px, py, playerDot);
            }
        }

        // Center player indicator
        byte centerDot = MapPalette.matchColor(255, 255, 255);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int px = cx + dx;
                int py = cy + dy;
                if (px >= 0 && px < width && py >= 0 && py < height) {
                    canvas.setPixel(px, py, centerDot);
                }
            }
        }

        // Facing arrow (red)
        double yaw = Math.toRadians(playerLoc.getYaw());
        byte arrow = MapPalette.matchColor(255, 0, 0);
        int length = 12;
        for (int i = 0; i < length; i++) {
            int px = cx + (int) Math.round(Math.cos(yaw) * i);
            int py = cy + (int) Math.round(Math.sin(yaw) * i);
            if (px >= 0 && px < width && py >= 0 && py < height) {
                canvas.setPixel(px, py, arrow);
            }
        }
    }

    private byte getBlockColor(Material mat) {
        // Map block types to colors
        if (mat == Material.GRASS_BLOCK || mat == Material.SHORT_GRASS || mat == Material.TALL_GRASS) {
            return MapPalette.matchColor(34, 139, 34); // Forest green
        } else if (mat == Material.DIRT || mat == Material.COARSE_DIRT || mat == Material.ROOTED_DIRT) {
            return MapPalette.matchColor(139, 90, 43); // Brown
        } else if (mat == Material.FARMLAND || mat == Material.MUD) {
            return MapPalette.matchColor(110, 72, 38); // Dark soil
        } else if (mat == Material.STONE || mat == Material.DEEPSLATE || mat == Material.COBBLESTONE || mat == Material.MOSSY_COBBLESTONE) {
            return MapPalette.matchColor(128, 128, 128); // Gray
        } else if (mat == Material.GRAVEL) {
            return MapPalette.matchColor(169, 169, 169); // Dark gray
        } else if (mat == Material.SAND || mat == Material.RED_SAND) {
            return MapPalette.matchColor(238, 203, 123); // Sand yellow
        } else if (mat.name().contains("SANDSTONE")) {
            return MapPalette.matchColor(230, 214, 160); // Sandstone beige
        } else if (mat == Material.CLAY) {
            return MapPalette.matchColor(180, 200, 209); // Clay gray-blue
        } else if (mat.name().contains("TERRACOTTA")) {
            return MapPalette.matchColor(191, 141, 110); // Terracotta warm
        } else if (mat.name().endsWith("_WOOL")) {
            return MapPalette.matchColor(220, 220, 220); // Neutral wool
        } else if (mat.name().endsWith("_CONCRETE")) {
            return MapPalette.matchColor(180, 180, 180); // Neutral concrete
        } else if (mat == Material.WATER || mat == Material.SEAGRASS || mat == Material.KELP || mat == Material.KELP_PLANT) {
            return MapPalette.matchColor(0, 105, 148); // Water blue
        } else if (mat == Material.LAVA) {
            return MapPalette.matchColor(255, 69, 0); // Orange-red
        } else if (mat == Material.OAK_LOG || mat == Material.BIRCH_LOG || mat == Material.SPRUCE_LOG ||
                   mat == Material.DARK_OAK_LOG || mat == Material.ACACIA_LOG || mat == Material.JUNGLE_LOG ||
                   mat == Material.MANGROVE_LOG || mat == Material.CHERRY_LOG || mat == Material.BAMBOO_BLOCK) {
            return MapPalette.matchColor(101, 67, 33); // Dark brown
        } else if (mat == Material.OAK_PLANKS || mat == Material.BIRCH_PLANKS || mat == Material.SPRUCE_PLANKS ||
                   mat == Material.DARK_OAK_PLANKS || mat == Material.ACACIA_PLANKS || mat == Material.JUNGLE_PLANKS ||
                   mat == Material.MANGROVE_PLANKS || mat == Material.CHERRY_PLANKS || mat == Material.BAMBOO_PLANKS ||
                   mat == Material.BAMBOO_MOSAIC) {
            return MapPalette.matchColor(193, 154, 107); // Light wood
        } else if (mat == Material.OAK_LEAVES || mat == Material.BIRCH_LEAVES || mat == Material.SPRUCE_LEAVES ||
                   mat == Material.DARK_OAK_LEAVES || mat == Material.ACACIA_LEAVES || mat == Material.JUNGLE_LEAVES ||
                   mat == Material.MANGROVE_LEAVES || mat == Material.CHERRY_LEAVES) {
            return MapPalette.matchColor(85, 107, 47); // Dark yellow-green
        } else if (mat == Material.SNOW_BLOCK || mat == Material.SNOW) {
            return MapPalette.matchColor(240, 248, 255); // Alice blue
        } else if (mat == Material.ICE || mat == Material.BLUE_ICE || mat == Material.PACKED_ICE) {
            return MapPalette.matchColor(173, 216, 230); // Light blue
        } else if (mat == Material.DIRT_PATH) {
            return MapPalette.matchColor(210, 180, 100); // Path tan
        } else if (mat == Material.NETHERRACK) {
            return MapPalette.matchColor(153, 51, 51); // Nether red
        } else if (mat == Material.SOUL_SAND || mat == Material.SOUL_SOIL) {
            return MapPalette.matchColor(115, 78, 48); // Soul brown
        } else if (mat == Material.END_STONE) {
            return MapPalette.matchColor(236, 235, 185); // Pale yellow
        } else if (mat == Material.BLACKSTONE || mat == Material.BASALT) {
            return MapPalette.matchColor(54, 57, 63); // Dark basalt
        } else if (mat == Material.AIR || mat == Material.CAVE_AIR || mat == Material.VOID_AIR) {
            return MapPalette.matchColor(135, 206, 235); // Sky blue
        } else {
            // Default for unknown blocks
            return MapPalette.matchColor(100, 100, 100); // Medium gray
        }
    }
}
