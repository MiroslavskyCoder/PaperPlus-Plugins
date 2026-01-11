package com.webx.economy.managers;

import com.webx.economy.EconomyPlugin;
import com.webx.economy.models.Account;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class BalTopManager {
    private final EconomyPlugin plugin;
    private List<Map.Entry<UUID, Double>> topBalances;
    private BukkitRunnable updateTask;

    public BalTopManager(EconomyPlugin plugin) {
        this.plugin = plugin;
        this.topBalances = new ArrayList<>();
    }

    public void startUpdateTask() {
        long interval = plugin.getConfig().getLong("baltop.update-interval", 300) * 20L;

        updateTask = new BukkitRunnable() {
            @Override
            public void run() {
                updateTopBalances();
            }
        };

        updateTask.runTaskTimerAsynchronously(plugin, 20L, interval);
        updateTopBalances();
    }

    public void stopUpdateTask() {
        if (updateTask != null) {
            updateTask.cancel();
        }
    }

    private void updateTopBalances() {
        int limit = plugin.getConfig().getInt("baltop.display-amount", 10);
        List<Account> topAccounts = plugin.getAccountManager().getTopAccounts(limit);

        topBalances = topAccounts.stream()
                .map(account -> (Map.Entry<UUID, Double>) new AbstractMap.SimpleEntry<>(account.getOwner(), account.getTotalBalance()))
                .collect(Collectors.toList());
    }

    public List<Map.Entry<UUID, Double>> getTopBalances() {
        return Collections.unmodifiableList(topBalances);
    }

    public int getRank(UUID uuid) {
        for (int i = 0; i < topBalances.size(); i++) {
            if (topBalances.get(i).getKey().equals(uuid)) {
                return i + 1;
            }
        }
        return -1;
    }
}
