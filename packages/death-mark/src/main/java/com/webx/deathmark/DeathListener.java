package com.webx.deathmark;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class DeathListener implements Listener {
    private final JavaPlugin plugin;
    private final DeathChestManager manager;

    public DeathListener(JavaPlugin plugin, DeathChestManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!plugin.getConfig().getBoolean("enabled", true)) return;

        Player player = event.getEntity();
        Location deathLoc = player.getLocation();
        
        // Collect all items
        List<ItemStack> items = new ArrayList<>(event.getDrops());
        if (items.isEmpty()) return;

        // Clear drops - we'll store them in chest
        event.getDrops().clear();
        event.setKeepInventory(false);

        // Find safe location for chest
        Location chestLoc = findSafeLocation(deathLoc);
        if (chestLoc == null) {
            plugin.getLogger().warning("Could not find safe location for death chest at " + deathLoc);
            // Drop items normally
            event.getDrops().addAll(items);
            return;
        }

        // Create chest
        Block block = chestLoc.getBlock();
        block.setType(Material.CHEST);
        
        if (block.getState() instanceof Chest chest) {
            Inventory inv = chest.getInventory();
            
            // Fill chest with items
            for (ItemStack item : items) {
                if (item != null && item.getType() != Material.AIR) {
                    inv.addItem(item);
                }
            }

            // Register chest
            manager.registerChest(chestLoc, player.getUniqueId());

            // Notify player
            if (plugin.getConfig().getBoolean("notify-player", true)) {
                String msg = plugin.getConfig().getString("messages.chest-created", 
                    "&aВаши вещи сохранены в сундуке на &e{x}, {y}, {z}");
                msg = msg.replace("{x}", String.valueOf(chestLoc.getBlockX()))
                         .replace("{y}", String.valueOf(chestLoc.getBlockY()))
                         .replace("{z}", String.valueOf(chestLoc.getBlockZ()));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            }

            // Schedule expiration if configured
            int expireSeconds = plugin.getConfig().getInt("expire-seconds", 600);
            if (expireSeconds > 0) {
                manager.scheduleExpiration(chestLoc, player.getUniqueId(), expireSeconds);
            }
        }
    }

    @EventHandler
    public void onChestOpen(InventoryOpenEvent event) {
        if (!plugin.getConfig().getBoolean("protected", true)) return;
        if (!(event.getPlayer() instanceof Player player)) return;
        if (!(event.getInventory().getHolder() instanceof Chest chest)) return;

        Location loc = chest.getLocation();
        if (!manager.isDeathChest(loc)) return;

        java.util.UUID owner = manager.getOwner(loc);
        if (owner == null) return;

        if (!player.getUniqueId().equals(owner)) {
            event.setCancelled(true);
            String msg = plugin.getConfig().getString("messages.chest-protected", 
                "&cЭтот сундук принадлежит другому игроку");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
        } else {
            String msg = plugin.getConfig().getString("messages.chest-opened", 
                "&aВы открыли свой сундук со смертью");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
        }
    }

    @EventHandler
    public void onChestBreak(BlockBreakEvent event) {
        if (!plugin.getConfig().getBoolean("protected", true)) return;
        
        Location loc = event.getBlock().getLocation();
        if (!manager.isDeathChest(loc)) return;

        Player player = event.getPlayer();
        java.util.UUID owner = manager.getOwner(loc);
        
        if (owner != null && !player.getUniqueId().equals(owner)) {
            event.setCancelled(true);
            String msg = plugin.getConfig().getString("messages.chest-protected", 
                "&cЭтот сундук принадлежит другому игроку");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
        } else {
            // Allow breaking, unregister chest
            manager.unregisterChest(loc);
        }
    }

    private Location findSafeLocation(Location start) {
        // Try exact location first
        if (isSafe(start)) return start;
        
        // Try one block up
        Location up = start.clone().add(0, 1, 0);
        if (isSafe(up)) return up;
        
        // Try surrounding blocks
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (x == 0 && z == 0) continue;
                Location nearby = start.clone().add(x, 0, z);
                if (isSafe(nearby)) return nearby;
            }
        }
        
        return null;
    }

    private boolean isSafe(Location loc) {
        Block block = loc.getBlock();
        return block.getType() == Material.AIR || 
               block.getType().isAir() || 
               block.isReplaceable();
    }
}
