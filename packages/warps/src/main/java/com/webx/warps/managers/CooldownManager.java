package com.webx.warps.managers;

import com.webx.warps.WarpsPlugin;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {
    private final WarpsPlugin plugin;
    private final Map<UUID, Long> cooldowns;

    public CooldownManager(WarpsPlugin plugin) {
        this.plugin = plugin;
        this.cooldowns = new HashMap<>();
    }

    public boolean hasCooldown(Player player) {
        if (player.hasPermission("warps.bypass.cooldown")) {
            return false;
        }

        Long endTime = cooldowns.get(player.getUniqueId());
        if (endTime == null) {
            return false;
        }

        return System.currentTimeMillis() < endTime;
    }

    public long getRemainingCooldown(Player player) {
        Long endTime = cooldowns.get(player.getUniqueId());
        if (endTime == null) {
            return 0;
        }

        long remaining = endTime - System.currentTimeMillis();
        return Math.max(0, remaining / 1000);
    }

    public void setCooldown(Player player) {
        if (player.hasPermission("warps.bypass.cooldown")) {
            return;
        }

        int cooldownSeconds = plugin.getConfig().getInt("teleport.cooldown", 30);
        long endTime = System.currentTimeMillis() + (cooldownSeconds * 1000L);
        cooldowns.put(player.getUniqueId(), endTime);
    }

    public void removeCooldown(Player player) {
        cooldowns.remove(player.getUniqueId());
    }

    public void clearAll() {
        cooldowns.clear();
    }
}
