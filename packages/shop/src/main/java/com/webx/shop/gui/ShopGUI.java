package com.webx.shop.gui;

import com.webx.economy.managers.AccountManager;
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
        Material material;
        try {
            material = Material.valueOf(shopItem.getMaterial().toUpperCase());
        } catch (IllegalArgumentException e) {
            material = Material.STONE;
            plugin.getLogger().warning("Invalid material: " + shopItem.getMaterial());
        }

        ItemStack item = new ItemStack(material, shopItem.getAmount());
        ItemMeta meta = item.getItemMeta();

        // Set display name
        meta.displayName(Component.text(shopItem.getName(), NamedTextColor.YELLOW, TextDecoration.BOLD));

        // Set lore
        List<Component> lore = new ArrayList<>();
        
        if (shopItem.getLore() != null) {
            for (String line : shopItem.getLore()) {
                lore.add(Component.text(line, NamedTextColor.GRAY));
            }
        }
        
        lore.add(Component.empty());
        lore.add(Component.text("Price: ", NamedTextColor.GOLD)
                .append(Component.text(String.format("%.2f", shopItem.getPrice()), NamedTextColor.YELLOW))
                .append(Component.text(" coins", NamedTextColor.GOLD)));
        lore.add(Component.empty());
        lore.add(Component.text("Click to purchase!", NamedTextColor.GREEN));

        meta.lore(lore);
        item.setItemMeta(meta);

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
        AccountManager accountManager = getAccountManager();
        if (accountManager == null) {
            player.sendMessage(Component.text("Economy system not available!", NamedTextColor.RED));
            return false;
        }

        double balance = accountManager.getBalance(player.getUniqueId());
        
        if (balance < shopItem.getPrice()) {
            player.sendMessage(Component.text("Insufficient funds! You need ", NamedTextColor.RED)
                    .append(Component.text(String.format("%.2f", shopItem.getPrice() - balance), NamedTextColor.YELLOW))
                    .append(Component.text(" more coins.", NamedTextColor.RED)));
            return false;
        }

        // Check inventory space
        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(Component.text("Your inventory is full!", NamedTextColor.RED));
            return false;
        }

        // Check permission
        if (shopItem.getPermission() != null && !shopItem.getPermission().isEmpty()) {
            if (!player.hasPermission(shopItem.getPermission())) {
                player.sendMessage(Component.text("You don't have permission to buy this item!", NamedTextColor.RED));
                return false;
            }
        }

        // Process purchase
        accountManager.withdraw(player.getUniqueId(), shopItem.getPrice());
        
        // Give item to player
        Material material = Material.valueOf(shopItem.getMaterial().toUpperCase());
        ItemStack item = new ItemStack(material, shopItem.getAmount());
        player.getInventory().addItem(item);

        player.sendMessage(Component.text("Successfully purchased ", NamedTextColor.GREEN)
                .append(Component.text(shopItem.getName(), NamedTextColor.YELLOW))
                .append(Component.text(" for ", NamedTextColor.GREEN))
                .append(Component.text(String.format("%.2f", shopItem.getPrice()), NamedTextColor.GOLD))
                .append(Component.text(" coins!", NamedTextColor.GREEN)));

        return true;
    }

    private AccountManager getAccountManager() {
        org.bukkit.plugin.Plugin economyPlugin = Bukkit.getPluginManager().getPlugin("Economy");
        if (economyPlugin == null || !economyPlugin.isEnabled()) {
            return null;
        }

        try {
            Class<?> economyClass = economyPlugin.getClass();
            java.lang.reflect.Method method = economyClass.getMethod("getAccountManager");
            return (AccountManager) method.invoke(economyPlugin);
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to get AccountManager: " + e.getMessage());
            return null;
        }
    }
}
