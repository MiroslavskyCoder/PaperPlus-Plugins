package com.webx.shop.managers;

import com.webx.shop.ShopPlugin;
import com.webx.shop.models.Shop;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryManager {
    private final ShopPlugin plugin;

    public InventoryManager(ShopPlugin plugin) {
        this.plugin = plugin;
    }

    public Inventory createShopInventory(Shop shop) {
        int rows = plugin.getConfig().getInt("gui.rows", 6);
        Inventory inventory = Bukkit.createInventory(null, rows * 9,
                "§6" + shop.getName());

        shop.getItems().forEach(item -> {
            ItemStack stack = item.toItemStack();
            inventory.addItem(stack);
        });

        return inventory;
    }

    public Inventory createShopListInventory() {
        Inventory inventory = Bukkit.createInventory(null, 27, "§6Shops");

        int index = 0;
        for (Shop shop : plugin.getShopManager().getAllShops()) {
            if (index >= 27) break;
            // TODO: Add shop items to inventory
            index++;
        }

        return inventory;
    }
}
