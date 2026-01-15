package com.webx.playerinfo;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.util.UUID;

public class EconomyDataManager {

    private final JavaPlugin plugin;
    private Plugin economyPlugin;
    private Method getAccountManagerMethod;
    private Method getAccountMethod;
    private Method getBalanceMethod;
    private Method getBankBalanceMethod;
    private Method getTotalBalanceMethod;
    private boolean loggedMissingEconomy;
    private boolean loggedReflectionError;

    public EconomyDataManager(JavaPlugin plugin) {
        this.plugin = plugin;
        attemptHook();
    }

    public CoinSnapshot getCoinInfo(Player player) {
        if (!ensureHook()) {
            return CoinSnapshot.unavailable();
        }

        try {
            Object accountManager = getAccountManagerMethod.invoke(economyPlugin);
            if (accountManager == null) {
                return CoinSnapshot.unavailable();
            }

            Object account = getAccountMethod.invoke(accountManager, player.getUniqueId());
            if (account == null) {
                return CoinSnapshot.unavailable();
            }

            double wallet = ((Number) getBalanceMethod.invoke(account)).doubleValue();
            double bank = ((Number) getBankBalanceMethod.invoke(account)).doubleValue();
            double total = ((Number) getTotalBalanceMethod.invoke(account)).doubleValue();
            return new CoinSnapshot(wallet, bank, total, true);
        } catch (Exception e) {
            if (!loggedReflectionError) {
                plugin.getLogger().warning("Failed to read coins from Economy plugin: " + e.getMessage());
                loggedReflectionError = true;
            }
            return CoinSnapshot.unavailable();
        }
    }

    public void refreshAccounts() {
        ensureHook();
    }

    private boolean ensureHook() {
        if (economyPlugin != null && economyPlugin.isEnabled()) {
            return true;
        }
        return attemptHook();
    }

    private boolean attemptHook() {
        Plugin found = Bukkit.getPluginManager().getPlugin("Economy");
        if (found == null || !found.isEnabled()) {
            if (!loggedMissingEconomy) {
                plugin.getLogger().warning("Economy plugin not found; coins will appear as N/A until it loads.");
                loggedMissingEconomy = true;
            }
            economyPlugin = null;
            return false;
        }

        try {
            Class<?> economyClass = Class.forName("com.webx.economy.EconomyPlugin");
            Class<?> accountManagerClass = Class.forName("com.webx.economy.managers.AccountManager");
            Class<?> accountClass = Class.forName("com.webx.economy.models.Account");

            if (!economyClass.isInstance(found)) {
                plugin.getLogger().warning("Economy plugin detected but incompatible class; coins display disabled.");
                economyPlugin = null;
                return false;
            }

            getAccountManagerMethod = economyClass.getMethod("getAccountManager");
            getAccountMethod = accountManagerClass.getMethod("getAccount", UUID.class);
            getBalanceMethod = accountClass.getMethod("getBalance");
            getBankBalanceMethod = accountClass.getMethod("getBankBalance");
            getTotalBalanceMethod = accountClass.getMethod("getTotalBalance");

            economyPlugin = found;
            loggedMissingEconomy = false;
            loggedReflectionError = false;
            plugin.getLogger().info("Hooked into Economy plugin for PlayerInfo coins display.");
            return true;
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to hook into Economy plugin: " + e.getMessage());
            economyPlugin = null;
            return false;
        }
    }

    public record CoinSnapshot(double wallet, double bank, double total, boolean available) {
        public static CoinSnapshot unavailable() {
            return new CoinSnapshot(0.0, 0.0, 0.0, false);
        }
    }
}
