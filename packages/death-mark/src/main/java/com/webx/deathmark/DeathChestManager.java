package com.webx.deathmark;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DeathChestManager {
    private final JavaPlugin plugin;
    private final Map<Location, UUID> chests = new HashMap<>();
    private final Map<Location, Integer> expirationTasks = new HashMap<>();

    public DeathChestManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerChest(Location loc, UUID owner) {
        chests.put(loc, owner);
    }

    public void unregisterChest(Location loc) {
        chests.remove(loc);
        Integer taskId = expirationTasks.remove(loc);
        if (taskId != null) {
            plugin.getServer().getScheduler().cancelTask(taskId);
        }
    }

    public boolean isDeathChest(Location loc) {
        return chests.containsKey(loc);
    }

    public UUID getOwner(Location loc) {
        return chests.get(loc);
    }

    public void scheduleExpiration(Location loc, UUID ownerId, int seconds) {
        int taskId = new BukkitRunnable() {
            @Override
            public void run() {
                expireChest(loc, ownerId, seconds);
            }
        }.runTaskLater(plugin, seconds * 20L).getTaskId();
        
        expirationTasks.put(loc, taskId);
    }

    private void expireChest(Location loc, UUID ownerId, int seconds) {
        Block block = loc.getBlock();
        if (block.getType() == Material.CHEST) {
            // Drop items on ground
            if (block.getState() instanceof org.bukkit.block.Chest chest) {
                for (org.bukkit.inventory.ItemStack item : chest.getInventory().getContents()) {
                    if (item != null && item.getType() != Material.AIR) {
                        loc.getWorld().dropItemNaturally(loc, item);
                    }
                }
            }
            block.setType(Material.AIR);
        }
        
        unregisterChest(loc);
        
        // Notify player if online
        Player player = plugin.getServer().getPlayer(ownerId);
        if (player != null && player.isOnline()) {
            String msg = plugin.getConfig().getString("messages.chest-expired", 
                "&7Ваш сундук со смертью исчез через {time} секунд");
            msg = msg.replace("{time}", String.valueOf(seconds));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
        }
    }

    public void cleanup() {
        for (Integer taskId : expirationTasks.values()) {
            plugin.getServer().getScheduler().cancelTask(taskId);
        }
        chests.clear();
        expirationTasks.clear();
    }
}
