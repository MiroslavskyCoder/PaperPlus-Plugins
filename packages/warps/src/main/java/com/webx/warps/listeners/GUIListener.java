package com.webx.warps.listeners;

import com.webx.warps.WarpsPlugin;
import com.webx.warps.models.Warp;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GUIListener implements Listener {
    private final WarpsPlugin plugin;

    public GUIListener(WarpsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        String title = plugin.getConfigManager().getColoredString("gui.title");
        if (!event.getView().getTitle().equals(title)) {
            return;
        }

        event.setCancelled(true);

        if (event.getCurrentItem() == null) {
            return;
        }

        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();
        String warpName = itemName.replace("§6", "");

        Warp warp = plugin.getWarpManager().getWarp(warpName);
        if (warp != null) {
            player.closeInventory();
            player.performCommand("warp " + warpName);
        }
    }
}
