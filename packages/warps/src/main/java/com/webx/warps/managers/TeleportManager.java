package com.webx.warps.managers;

import com.webx.warps.WarpsPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeleportManager {
    private final WarpsPlugin plugin;
    private final Map<UUID, TeleportTask> pendingTeleports;

    public TeleportManager(WarpsPlugin plugin) {
        this.plugin = plugin;
        this.pendingTeleports = new HashMap<>();
    }

    public void teleport(Player player, Location destination, String warpName) {
        int delay = plugin.getConfig().getInt("teleport.delay", 3);

        if (delay <= 0 || player.hasPermission("warps.bypass.delay")) {
            player.teleport(destination);
            plugin.getMessageManager().send(player, "teleported", 
                Map.of("name", warpName));
            return;
        }

        cancelTeleport(player);

        plugin.getMessageManager().send(player, "teleporting",
                Map.of("name", warpName, "delay", String.valueOf(delay)));

        TeleportTask task = new TeleportTask(player, destination, warpName);
        task.runTaskLater(plugin, delay * 20L);
        pendingTeleports.put(player.getUniqueId(), task);
    }

    public void cancelTeleport(Player player) {
        TeleportTask task = pendingTeleports.remove(player.getUniqueId());
        if (task != null) {
            task.cancel();
        }
    }

    public boolean hasPendingTeleport(Player player) {
        return pendingTeleports.containsKey(player.getUniqueId());
    }

    public Location getStartLocation(Player player) {
        TeleportTask task = pendingTeleports.get(player.getUniqueId());
        return task != null ? task.startLocation : null;
    }

    public void cancelAll() {
        for (TeleportTask task : pendingTeleports.values()) {
            task.cancel();
        }
        pendingTeleports.clear();
    }

    private class TeleportTask extends BukkitRunnable {
        private final Player player;
        private final Location destination;
        private final Location startLocation;
        private final String warpName;

        public TeleportTask(Player player, Location destination, String warpName) {
            this.player = player;
            this.destination = destination;
            this.startLocation = player.getLocation().clone();
            this.warpName = warpName;
        }

        @Override
        public void run() {
            pendingTeleports.remove(player.getUniqueId());

            if (!player.isOnline()) return;

            player.teleport(destination);
            plugin.getMessageManager().send(player, "teleported",
                    Map.of("name", warpName));
        }
    }
}
