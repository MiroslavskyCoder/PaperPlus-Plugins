package com.webx.shop.gui;

import com.webx.shop.ShopPlugin;
import com.webx.shop.models.ShopItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Manages shop GUI inventory
 */
public class ShopGUI {
    private final ShopPlugin plugin;
    private final Inventory inventory;
    private final List<ShopItem> items;

    public ShopGUI(ShopPlugin plugin, List<ShopItem> items) {
        this.plugin = plugin;
        this.items = items;
        this.inventory = Bukkit.createInventory(null, 54, Component.text("Shop", NamedTextColor.GOLD, TextDecoration.BOLD));
        populateInventory();
    }

    private void populateInventory() {
        int slot = 0;
        
        for (ShopItem shopItem : items) {
            if (slot >= 45) break; // Reserve last row for navigation
            
            ItemStack item = createItemStack(shopItem);
            inventory.setItem(slot, item);
            slot++;
        }

        // Add info item
        ItemStack infoItem = new ItemStack(Material.PAPER);
        ItemMeta infoMeta = infoItem.getItemMeta();
        infoMeta.displayName(Component.text("Shop Info", NamedTextColor.AQUA, TextDecoration.BOLD));
        List<Component> infoLore = new ArrayList<>();
        infoLore.add(Component.text("Click items to purchase", NamedTextColor.GRAY));
        infoLore.add(Component.text("with your coins!", NamedTextColor.GRAY));
        infoMeta.lore(infoLore);
        infoItem.setItemMeta(infoMeta);
        inventory.setItem(49, infoItem);

        // Add close button
        ItemStack closeItem = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = closeItem.getItemMeta();
        closeMeta.displayName(Component.text("Close", NamedTextColor.RED, TextDecoration.BOLD));
        closeItem.setItemMeta(closeMeta);
        inventory.setItem(53, closeItem);
    }

    private ItemStack createItemStack(ShopItem shopItem) {
        // Get ItemStack from ShopItem
        ItemStack item = shopItem.getItem().clone();
        ItemMeta meta = item.getItemMeta();

        // Update lore with price info
        List<Component> lore = new ArrayList<>();
        if (meta != null && meta.lore() != null) {
            lore.addAll(meta.lore());
        }
        
        lore.add(Component.empty());
        lore.add(Component.text("Price: ", NamedTextColor.GOLD)
                .append(Component.text(String.format("%.2f", shopItem.getBuyPrice()), NamedTextColor.YELLOW))
                .append(Component.text(" coins", NamedTextColor.GOLD)));
        lore.add(Component.empty());
        lore.add(Component.text("Click to purchase!", NamedTextColor.GREEN));

        if (meta != null) {
            meta.lore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public ShopItem getShopItem(int slot) {
        if (slot < 0 || slot >= items.size()) {
            return null;
        }
        return items.get(slot);
    }

    /**
     * Handle item purchase
     */
    public boolean purchaseItem(Player player, ShopItem shopItem) {
        Object accountManager = getAccountManager();
        if (accountManager == null) {
            player.sendMessage(Component.text("Economy system not available!", NamedTextColor.RED));
            return false;
        }

        double balance = getPlayerBalance(accountManager, player.getUniqueId());
        
        if (balance < shopItem.getBuyPrice()) {
            player.sendMessage(Component.text("Insufficient funds! You need ", NamedTextColor.RED)
                    .append(Component.text(String.format("%.2f", shopItem.getBuyPrice() - balance), NamedTextColor.YELLOW))
                    .append(Component.text(" more coins.", NamedTextColor.RED)));
            return false;
        }

        // Check inventory space
        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(Component.text("Your inventory is full!", NamedTextColor.RED));
            return false;
        }

        // Process purchase
        withdrawPlayerBalance(accountManager, player.getUniqueId(), shopItem.getBuyPrice());
        
        // Give item to player
        ItemStack item = shopItem.getItem().clone();
        player.getInventory().addItem(item);

        // Get display name safely
        String itemName = "item";
        if (item.getItemMeta() != null && item.getItemMeta().displayName() != null) {
            itemName = item.getItemMeta().displayName().toString();
        }

        player.sendMessage(Component.text("Successfully purchased ", NamedTextColor.GREEN)
                .append(Component.text(itemName, NamedTextColor.YELLOW))
                .append(Component.text(" for ", NamedTextColor.GREEN))
                .append(Component.text(String.format("%.2f", shopItem.getBuyPrice()), NamedTextColor.GOLD))
                .append(Component.text(" coins!", NamedTextColor.GREEN)));

        return true;
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
            plugin.getLogger().warning("Failed to get balance: " + e.getMessage());
            return 0.0;
        }
    }

    /**
     * Withdraw from player balance using reflection
     */
    private void withdrawPlayerBalance(Object accountManager, UUID uuid, double amount) {
        try {
            java.lang.reflect.Method method = accountManager.getClass().getMethod("withdraw", UUID.class, double.class);
            method.invoke(accountManager, uuid, amount);
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to withdraw: " + e.getMessage());
        }
    }

    /**
     * Get AccountManager from Economy plugin using reflection
     */
    private Object getAccountManager() {
        org.bukkit.plugin.Plugin economyPlugin = Bukkit.getPluginManager().getPlugin("Economy");
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
