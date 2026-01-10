package com.webx.marketplace;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MarketplacePlugin extends JavaPlugin implements Listener {
    private static MarketplacePlugin instance;
    private Map<UUID, MarketListing> listings = new HashMap<>();
    private Inventory marketplace;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        
        getServer().getPluginManager().registerEvents(this, this);
        
        marketplace = Bukkit.createInventory(null, 54, "§6Marketplace");
        initializeMarketplace();
        
        getCommand("marketplace").setExecutor((sender, cmd, label, args) -> {
            if (!(sender instanceof Player)) return true;
            Player player = (Player) sender;
            player.openInventory(marketplace);
            return true;
        });
        
        getLogger().info("Marketplace Plugin enabled!");
    }
    
    private void initializeMarketplace() {
        // Добавляем несколько примеров товаров
        addMarketItem(0, Material.DIAMOND, "§bDiamond §8- §f$100", 100);
        addMarketItem(1, Material.IRON_INGOT, "§7Iron §8- §f$10", 10);
        addMarketItem(2, Material.GOLD_INGOT, "§6Gold §8- §f$50", 50);
        addMarketItem(3, Material.EMERALD, "§aEmerald §8- §f$75", 75);
    }
    
    private void addMarketItem(int slot, Material material, String displayName, double price) {
        ItemStack item = new ItemStack(material);
        var meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(displayName);
            item.setItemMeta(meta);
        }
        marketplace.setItem(slot, item);
    }
    
    @EventHandler
    public void onMarketplaceClick(InventoryClickEvent event) {
        if (!event.getInventory().equals(marketplace)) return;
        
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();
        
        ItemStack item = event.getInventory().getItem(slot);
        if (item == null || item.getType() == Material.AIR) return;
        
        player.sendMessage("§aYou would buy: " + item.getType().name());
        // Здесь добавить логику покупки с Economy плагином
    }
    
    public static MarketplacePlugin getInstance() {
        return instance;
    }
    
    private static class MarketListing {
        String seller;
        ItemStack item;
        double price;
        
        MarketListing(String seller, ItemStack item, double price) {
            this.seller = seller;
            this.item = item;
            this.price = price;
        }
    }
}
