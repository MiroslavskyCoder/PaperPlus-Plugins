package com.webx.warps.managers;

import com.webx.warps.WarpsPlugin;
import org.bukkit.entity.Player;

public class EconomyManager {
    private final WarpsPlugin plugin;
    private boolean enabled;

    public EconomyManager(WarpsPlugin plugin) {
        this.plugin = plugin;
        this.enabled = plugin.getConfig().getBoolean("economy.enabled", false);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public double getCost() {
        return plugin.getConfig().getDouble("economy.cost-per-warp", 10.0);
    }

    public boolean hasMoney(Player player, double amount) {
        // Placeholder - integrate with Vault or your economy system
        return true;
    }

    public boolean charge(Player player, double amount) {
        if (player.hasPermission("warps.bypass.cost")) {
            return true;
        }

        // Placeholder - integrate with Vault or your economy system
        return true;
    }

    public void refund(Player player, double amount) {
        // Placeholder - integrate with Vault or your economy system
    }
}
