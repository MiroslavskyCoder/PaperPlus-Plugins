package com.webx.backtp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TpaManager {
    private final BackTpPlugin plugin;
    private final TeleportManager teleportManager;
    private final Map<UUID, TpaRequest> requests = new HashMap<>();

    public TpaManager(BackTpPlugin plugin, TeleportManager teleportManager) {
        this.plugin = plugin;
        this.teleportManager = teleportManager;
    }

    public void sendRequest(Player sender, Player target, boolean isHere) {
        TpaRequest request = new TpaRequest(sender.getUniqueId(), target.getUniqueId(), isHere);
        requests.put(target.getUniqueId(), request);

        String msgKey = isHere ? "messages.tpa.sent-here" : "messages.tpa.sent";
        String msg = plugin.getConfig().getString(msgKey, "&aЗапрос отправлен!");
        msg = msg.replace("{player}", target.getName());
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));

        msgKey = isHere ? "messages.tpa.received-here" : "messages.tpa.received";
        msg = plugin.getConfig().getString(msgKey, "&aПолучен запрос!");
        msg = msg.replace("{player}", sender.getName());
        target.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));

        // Schedule request expiration
        int timeout = plugin.getConfig().getInt("tpa.request-timeout", 60);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (requests.remove(target.getUniqueId()) != null) {
                String expMsg = plugin.getConfig().getString("messages.tpa.expired", "&cЗапрос истек!");
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', expMsg));
                target.sendMessage(ChatColor.translateAlternateColorCodes('&', expMsg));
            }
        }, timeout * 20L);
    }

    public void acceptRequest(Player accepter) {
        TpaRequest request = requests.remove(accepter.getUniqueId());
        if (request == null) return;

        Player sender = Bukkit.getPlayer(request.senderId);
        if (sender == null || !sender.isOnline()) {
            String msg = plugin.getConfig().getString("messages.tpa.player-not-found", "&cИгрок не найден!");
            accepter.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            return;
        }

        String msg = plugin.getConfig().getString("messages.tpa.accepted", "&aВы приняли запрос!");
        accepter.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));

        msg = plugin.getConfig().getString("messages.tpa.accepted-sender", "&e{player} &aпринял запрос!");
        msg = msg.replace("{player}", accepter.getName());
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));

        // Teleport
        Location destination;
        Player toTeleport;
        
        if (request.isHere) {
            // tpahere: accepter teleports to sender
            destination = sender.getLocation();
            toTeleport = accepter;
        } else {
            // tpa: sender teleports to accepter
            destination = accepter.getLocation();
            toTeleport = sender;
        }

        teleportManager.teleport(toTeleport, destination, "tpa");
    }

    public void denyRequest(Player denier) {
        TpaRequest request = requests.remove(denier.getUniqueId());
        if (request == null) return;

        Player sender = Bukkit.getPlayer(request.senderId);
        
        String msg = plugin.getConfig().getString("messages.tpa.denied", "&cВы отклонили запрос!");
        denier.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));

        if (sender != null && sender.isOnline()) {
            msg = plugin.getConfig().getString("messages.tpa.denied-sender", "&e{player} &cотклонил запрос!");
            msg = msg.replace("{player}", denier.getName());
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
        }
    }

    public boolean hasRequest(Player player) {
        return requests.containsKey(player.getUniqueId());
    }

    public void cancelAll() {
        requests.clear();
    }

    private static class TpaRequest {
        UUID senderId;
        UUID targetId;
        boolean isHere;

        TpaRequest(UUID senderId, UUID targetId, boolean isHere) {
            this.senderId = senderId;
            this.targetId = targetId;
            this.isHere = isHere;
        }
    }
}
