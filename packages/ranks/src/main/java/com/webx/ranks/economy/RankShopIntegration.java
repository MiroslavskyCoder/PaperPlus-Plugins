package com.webx.ranks.economy;

import com.webx.ranks.managers.RankManager;
import com.webx.ranks.managers.PlayerRankManager;
import com.webx.ranks.models.Rank;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Integration with Economy plugin for rank purchases
 */
public class RankShopIntegration {
    private final RankManager rankManager;
    private final PlayerRankManager playerRankManager;
    private Plugin economyPlugin;
    private Object economyManager;

    public RankShopIntegration(RankManager rankManager, PlayerRankManager playerRankManager) {
        this.rankManager = rankManager;
        this.playerRankManager = playerRankManager;
        initializeEconomyPlugin();
    }

    private void initializeEconomyPlugin() {
        try {
            economyPlugin = Bukkit.getPluginManager().getPlugin("Economy");
            if (economyPlugin != null && economyPlugin.isEnabled()) {
                Method getEconomyManagerMethod = economyPlugin.getClass().getMethod("getEconomyManager");
                economyManager = getEconomyManagerMethod.invoke(economyPlugin);
                System.out.println("✅ RankShopIntegration initialized with Economy plugin");
            }
        } catch (Exception e) {
            System.err.println("Failed to initialize economy integration: " + e.getMessage());
        }
    }

    public boolean isAvailable() {
        return economyPlugin != null && economyPlugin.isEnabled() && economyManager != null;
    }

    /**
     * Handle rank purchase from economy system
     */
    public boolean purchaseRank(Player player, String rankId) {
        if (!isAvailable()) {
            player.sendMessage("§cEconomy system is not available!");
            return false;
        }

        Rank rank = rankManager.getRank(rankId);
        if (rank == null) {
            player.sendMessage("§cRank not found!");
            return false;
        }

        if (!rank.isPurchasable()) {
            player.sendMessage("§cThis rank cannot be purchased!");
            return false;
        }

        long price = rank.getPurchasePrice();

        try {
            // Get player balance
            Method getPlayerBalanceMethod = economyManager.getClass()
                    .getMethod("getPlayerBalance", UUID.class);
            long currentBalance = (long) getPlayerBalanceMethod.invoke(economyManager, player.getUniqueId());

            if (currentBalance < price) {
                long needed = price - currentBalance;
                player.sendMessage("§cYou need §f" + needed + " §cmore coins!");
                return false;
            }

            // Deduct coins
            Method takeCoinsMethod = economyManager.getClass()
                    .getMethod("takeCoins", UUID.class, long.class);
            takeCoinsMethod.invoke(economyManager, player.getUniqueId(), price);

            // Assign rank
            playerRankManager.setPlayerPrimaryRank(
                    player.getUniqueId(),
                    rankId,
                    "SYSTEM",
                    "Purchased from shop"
            );

            player.sendMessage("§a✓ Successfully purchased rank " + rank.getDisplayName() + "!");
            player.sendMessage("§aCost: §f" + price + " §acoins");

            return true;
        } catch (Exception e) {
            System.err.println("Failed to process rank purchase: " + e.getMessage());
            player.sendMessage("§cAn error occurred while purchasing the rank!");
            return false;
        }
    }

    /**
     * Get purchasable ranks for shop display
     */
    public java.util.List<java.util.Map<String, Object>> getPurchasableRanks() {
        java.util.List<java.util.Map<String, Object>> purchasableRanks = new java.util.ArrayList<>();

        for (Rank rank : rankManager.getRanksSortedByPriority()) {
            if (rank.isPurchasable()) {
                java.util.Map<String, Object> rankData = new java.util.HashMap<>();
                rankData.put("id", rank.getId());
                rankData.put("displayName", rank.getDisplayName());
                rankData.put("price", rank.getPurchasePrice());
                rankData.put("prefix", rank.getPrefix());
                rankData.put("description", "Purchase rank: " + rank.getDisplayName());
                purchasableRanks.add(rankData);
            }
        }

        return purchasableRanks;
    }

    /**
     * Refund rank purchase
     */
    public boolean refundRank(Player player, String rankId, long refundAmount) {
        if (!isAvailable()) {
            return false;
        }

        try {
            Method giveCoinsMethod = economyManager.getClass()
                    .getMethod("giveCoins", UUID.class, long.class);
            giveCoinsMethod.invoke(economyManager, player.getUniqueId(), refundAmount);

            playerRankManager.removePlayerRank(player.getUniqueId());
            player.sendMessage("§aRank refunded! You received §f" + refundAmount + " §acoins back.");
            return true;
        } catch (Exception e) {
            System.err.println("Failed to refund rank: " + e.getMessage());
            return false;
        }
    }
}
