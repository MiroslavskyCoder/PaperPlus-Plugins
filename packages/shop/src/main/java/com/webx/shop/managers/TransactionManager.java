package com.webx.shop.managers;

import com.webx.shop.ShopPlugin;
import com.webx.shop.models.Shop;
import com.webx.shop.models.ShopItem;
import org.bukkit.entity.Player;

public class TransactionManager {
    private final ShopPlugin plugin;

    public TransactionManager(ShopPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean buyItem(Player player, Shop shop, ShopItem item, int amount) {
        if (!item.canBuy()) {
            return false;
        }

        double totalPrice = item.getBuyPrice() * amount;

        // TODO: Check player balance via Economy plugin
        // if (!hasBalance(player, totalPrice)) return false;

        if (!item.removeStock(amount)) {
            return false;
        }

        // TODO: Charge player
        // TODO: Give item to player

        return true;
    }

    public boolean sellItem(Player player, Shop shop, ShopItem item, int amount) {
        if (!item.canSell()) {
            return false;
        }

        double totalPrice = item.getSellPrice() * amount;

        // TODO: Check if player has item
        // if (!hasItem(player, item, amount)) return false;

        item.addStock(amount);

        // TODO: Pay player
        // TODO: Take item from player

        return true;
    }
}
