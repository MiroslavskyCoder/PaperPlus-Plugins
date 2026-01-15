package com.webx.shop.services;

import com.webx.shop.ShopPlugin;
import com.webx.shop.managers.ShopManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Watches the shared shop.json file so edits performed via WebX Panel
 * become available in-game without restarting the server.
 */
public class ShopSyncService implements Runnable {

    private final ShopPlugin plugin;
    private final ShopManager shopManager;
    private final Path shopConfigPath;
    private final long intervalTicks;
    private final boolean notifyAdmins;

    private volatile long lastModified;
    private volatile long lastSize;
    private int taskId = -1;

    public ShopSyncService(ShopPlugin plugin, ShopManager shopManager) {
        this.plugin = plugin;
        this.shopManager = shopManager;
        this.shopConfigPath = plugin.getDataFolder().toPath().resolve("shop.json");
        this.intervalTicks = Math.max(20L, plugin.getConfig().getLong("sync.interval-ticks", 200L));
        this.notifyAdmins = plugin.getConfig().getBoolean("sync.notify-admins", true);
        captureCurrentFileState();
    }

    public void start() {
        if (!plugin.getConfig().getBoolean("sync.enabled", true)) {
            plugin.getLogger().info("Shop sync watcher disabled via config.");
            return;
        }

        if (!Files.exists(shopConfigPath)) {
            plugin.getLogger().warning("Shop config not found for sync: " + shopConfigPath);
            return;
        }

        stop();
        taskId = Bukkit.getScheduler()
                .runTaskTimerAsynchronously(plugin, this, intervalTicks, intervalTicks)
                .getTaskId();
        plugin.getLogger().info("Watching shop.json for WebX Panel edits every " + intervalTicks + " ticks.");
    }

    public void stop() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
    }

    @Override
    public void run() {
        try {
            long modified = Files.getLastModifiedTime(shopConfigPath).toMillis();
            long size = Files.size(shopConfigPath);
            if (modified != lastModified || size != lastSize) {
                lastModified = modified;
                lastSize = size;
                int count = shopManager.reloadShopItems();
                plugin.getLogger().info("Shop items synced from disk (" + count + " entries).");
                if (notifyAdmins) {
                    notifyAdmins(count);
                }
            }
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to watch shop.json: " + e.getMessage());
        }
    }

    private void captureCurrentFileState() {
        if (!Files.exists(shopConfigPath)) {
            this.lastModified = 0L;
            this.lastSize = 0L;
            return;
        }
        try {
            this.lastModified = Files.getLastModifiedTime(shopConfigPath).toMillis();
            this.lastSize = Files.size(shopConfigPath);
        } catch (IOException e) {
            this.lastModified = 0L;
            this.lastSize = 0L;
        }
    }

    private void notifyAdmins(int count) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("shop.admin")) {
                    player.sendMessage("§aShop items updated (" + count + ") via WebX Panel.");
                }
            }
        });
    }
}
