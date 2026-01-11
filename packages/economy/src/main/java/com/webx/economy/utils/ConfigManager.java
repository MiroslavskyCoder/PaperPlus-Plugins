package com.webx.economy.utils;

import com.webx.economy.EconomyPlugin;
import org.bukkit.ChatColor;

public class ConfigManager {
    private final EconomyPlugin plugin;

    public ConfigManager(EconomyPlugin plugin) {
        this.plugin = plugin;
    }

    public String getCurrencySymbol() {
        return plugin.getConfig().getString("currency.symbol", "$");
    }

    public String getCurrencyName() {
        return plugin.getConfig().getString("currency.name", "Coin");
    }

    public String getCurrencyPlural() {
        return plugin.getConfig().getString("currency.plural", "Coins");
    }

    public String formatBalance(double balance) {
        return getCurrencySymbol() + String.format("%.2f", balance);
    }

    public void reload() {
        plugin.reloadConfig();
    }
}
