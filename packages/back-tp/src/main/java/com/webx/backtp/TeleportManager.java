package com.webx.backtp;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeleportManager {
    private final BackTpPlugin plugin;
    private final Map<UUID, TeleportTask> pendingTeleports = new HashMap<>();

    public TeleportManager(BackTpPlugin plugin) {
        this.plugin = plugin;
    }

    public void teleport(Player player, Location destination, String type) {
        String configPath = type.equals("back") ? "back.teleport-delay" : "tpa.teleport-delay";
        int delay = plugin.getConfig().getInt(configPath, 3);
        
        if (delay <= 0) {
            player.teleport(destination);
            String msgKey = type.equals("back") ? "messages.back.teleported" : "messages.tpa.teleported";
            String msg = plugin.getConfig().getString(msgKey, "&aТелепортация выполнена!");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            return;
        }

        cancelTeleport(player);

        String msgKey = type.equals("back") ? "messages.back.teleporting" : "messages.tpa.teleporting";
        String msg = plugin.getConfig().getString(msgKey, "&eТелепортация через {delay} сек...");
        msg = msg.replace("{delay}", String.valueOf(delay));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));

        TeleportTask task = new TeleportTask(player, destination, type);
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

    public String getTeleportType(Player player) {
        TeleportTask task = pendingTeleports.get(player.getUniqueId());
        return task != null ? task.type : null;
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
        private final String type;

        public TeleportTask(Player player, Location destination, String type) {
            this.player = player;
            this.destination = destination;
            this.type = type;
        }

        @Override
        public void run() {
            pendingTeleports.remove(player.getUniqueId());
            
            if (!player.isOnline()) return;

            player.teleport(destination);
            String msgKey = type.equals("back") ? "messages.back.teleported" : "messages.tpa.teleported";
            String msg = plugin.getConfig().getString(msgKey, "&aТелепортация выполнена!");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
        }
    }
}
