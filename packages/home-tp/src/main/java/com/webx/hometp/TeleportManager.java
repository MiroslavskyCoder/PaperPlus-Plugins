package com.webx.hometp;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeleportManager {
    private final HomeTpPlugin plugin;
    private final Map<UUID, TeleportTask> pendingTeleports = new HashMap<>();

    public TeleportManager(HomeTpPlugin plugin) {
        this.plugin = plugin;
    }

    public void teleport(Player player, Location destination, String homeName) {
        int delay = plugin.getConfig().getInt("teleport-delay", 3);
        
        if (delay <= 0) {
            player.teleport(destination);
            String msg = plugin.getConfig().getString("messages.teleported", "&aВы телепортированы в дом '{name}'!");
            msg = msg.replace("{name}", homeName);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            return;
        }

        cancelTeleport(player);

        String msg = plugin.getConfig().getString("messages.teleporting", "&eТелепортация к дому '{name}' через {delay} сек...");
        msg = msg.replace("{name}", homeName).replace("{delay}", String.valueOf(delay));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));

        TeleportTask task = new TeleportTask(player, destination, homeName);
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

    public Location getPendingLocation(Player player) {
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
        private final String homeName;

        public TeleportTask(Player player, Location destination, String homeName) {
            this.player = player;
            this.destination = destination;
            this.startLocation = player.getLocation().clone();
            this.homeName = homeName;
        }

        @Override
        public void run() {
            pendingTeleports.remove(player.getUniqueId());
            
            if (!player.isOnline()) return;

            player.teleport(destination);
            String msg = plugin.getConfig().getString("messages.teleported", "&aВы телепортированы в дом '{name}'!");
            msg = msg.replace("{name}", homeName);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
        }
    }
}
