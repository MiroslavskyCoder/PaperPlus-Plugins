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

import java.awt.Color;

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
                Block block = world.getBlockAt(worldX, playerLoc.getBlockY(), worldZ);
                byte color = getBlockColor(block.getType());
                canvas.setPixel(x, y, color);
            }
        }

        // Plot other players as white dots
        byte playerDot = MapPalette.matchColor(new Color(255, 255, 255));
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
        byte centerDot = MapPalette.matchColor(new Color(255, 255, 255));
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
        byte arrow = MapPalette.matchColor(new Color(255, 0, 0));
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
        if (mat == Material.GRASS_BLOCK || mat == Material.SHORT_GRASS) {
            return MapPalette.matchColor(new Color(34, 139, 34)); // Forest green
        } else if (mat == Material.DIRT || mat == Material.COARSE_DIRT) {
            return MapPalette.matchColor(new Color(139, 90, 43)); // Brown
        } else if (mat == Material.STONE || mat == Material.DEEPSLATE) {
            return MapPalette.matchColor(new Color(128, 128, 128)); // Gray
        } else if (mat == Material.WATER || mat == Material.SEAGRASS || mat == Material.KELP) {
            return MapPalette.matchColor(new Color(0, 0, 255)); // Blue
        } else if (mat == Material.LAVA) {
            return MapPalette.matchColor(new Color(255, 69, 0)); // Orange-red
        } else if (mat == Material.OAK_LOG || mat == Material.BIRCH_LOG || mat == Material.SPRUCE_LOG || 
                   mat == Material.DARK_OAK_LOG || mat == Material.ACACIA_LOG || mat == Material.JUNGLE_LOG) {
            return MapPalette.matchColor(new Color(101, 67, 33)); // Dark brown
        } else if (mat == Material.OAK_LEAVES || mat == Material.BIRCH_LEAVES || mat == Material.SPRUCE_LEAVES || 
                   mat == Material.DARK_OAK_LEAVES || mat == Material.ACACIA_LEAVES || mat == Material.JUNGLE_LEAVES) {
            return MapPalette.matchColor(new Color(85, 107, 47)); // Dark yellow-green
        } else if (mat == Material.SAND || mat == Material.RED_SAND) {
            return MapPalette.matchColor(new Color(238, 203, 123)); // Sand yellow
        } else if (mat == Material.SNOW_BLOCK || mat == Material.SNOW) {
            return MapPalette.matchColor(new Color(240, 248, 255)); // Alice blue
        } else if (mat == Material.ICE || mat == Material.BLUE_ICE) {
            return MapPalette.matchColor(new Color(173, 216, 230)); // Light blue
        } else if (mat == Material.GRAVEL) {
            return MapPalette.matchColor(new Color(169, 169, 169)); // Dark gray
        } else if (mat == Material.AIR) {
            return MapPalette.matchColor(new Color(135, 206, 235)); // Sky blue
        } else {
            // Default for unknown blocks
            return MapPalette.matchColor(new Color(100, 100, 100)); // Medium gray
        }
    }
}
