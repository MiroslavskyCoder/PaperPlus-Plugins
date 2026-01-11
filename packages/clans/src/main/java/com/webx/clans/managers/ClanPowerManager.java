package com.webx.clans.managers;

import com.webx.clans.ClansPlugin;
import com.webx.clans.models.Clan;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

/**
 * Manages clan power calculations based on member economy balances
 */
public class ClanPowerManager {
    private final ClansPlugin plugin;
    private final ClanManager clanManager;

    public ClanPowerManager(ClansPlugin plugin, ClanManager clanManager) {
        this.plugin = plugin;
        this.clanManager = clanManager;
    }

    /**
     * Start periodic power update task (every 5 minutes)
     */
    public void startUpdateTask() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            updateAllClanPowers();
        }, 20L * 60, 20L * 60 * 5); // Run every 5 minutes
    }

    /**
     * Update power for all clans
     */
    public void updateAllClanPowers() {
        Object accountManager = getAccountManager();
        if (accountManager == null) {
            plugin.getLogger().warning("Economy plugin not found, cannot update clan powers");
            return;
        }

        int updated = 0;
        for (Clan clan : clanManager.getAllClans()) {
            double power = calculateClanPower(clan, accountManager);
            clan.setPower(power);
            updated++;
        }

        plugin.getLogger().info("Updated power for " + updated + " clans");
    }

    /**
     * Update power for a specific clan
     */
    public void updateClanPower(Clan clan) {
        Object accountManager = getAccountManager();
        if (accountManager == null) {
            return;
        }

        double power = calculateClanPower(clan, accountManager);
        clan.setPower(power);
    }

    /**
     * Calculate clan power as sum of all member coin balances
     */
    private double calculateClanPower(Clan clan, Object accountManager) {
        double totalPower = 0;
        
        for (UUID memberUuid : clan.getMembers()) {
            double balance = getPlayerBalance(accountManager, memberUuid);
            totalPower += balance;
        }

        return totalPower;
    }

    /**
     * Get player balance using reflection
     */
    private double getPlayerBalance(Object accountManager, UUID uuid) {
        try {
            java.lang.reflect.Method method = accountManager.getClass().getMethod("getBalance", UUID.class);
            Object result = method.invoke(accountManager, uuid);
            return result instanceof Number ? ((Number) result).doubleValue() : 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }

    /**
     * Get AccountManager from Economy plugin using reflection
     */
    private Object getAccountManager() {
        Plugin economyPlugin = Bukkit.getPluginManager().getPlugin("Economy");
        if (economyPlugin == null || !economyPlugin.isEnabled()) {
            return null;
        }

        try {
            Class<?> economyClass = economyPlugin.getClass();
            java.lang.reflect.Method method = economyClass.getMethod("getAccountManager");
            return method.invoke(economyPlugin);
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to get AccountManager: " + e.getMessage());
            return null;
        }
    }
}
