package com.webx.shop.listeners;

import com.webx.shop.ShopPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickListener implements Listener {
    private final ShopPlugin plugin;

    public InventoryClickListener(ShopPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (!title.startsWith("§6")) {
            return;
        }

        event.setCancelled(true);
        // TODO: Handle shop transactions
    }
}
