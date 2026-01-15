package com.webx.unigui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central registry that dispatches click/close events to active GUI views.
 */
public final class GuiService implements Listener {
    private final Plugin plugin;
    private final Map<UUID, GuiView> openViews = new ConcurrentHashMap<>();

    public GuiService(Plugin plugin) {
        this.plugin = plugin;
    }

    public void registerEvents() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void open(Player player, GuiView view) {
        if (view == null) return;
        openViews.put(player.getUniqueId(), view);
        view.open(player);
    }

    public void close(Player player) {
        GuiView view = openViews.remove(player.getUniqueId());
        if (view != null) {
            view.onClose(player);
            player.closeInventory();
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        GuiView view = openViews.get(player.getUniqueId());
        if (view == null) return;
        if (!event.getInventory().equals(view.inventory())) return;

        event.setCancelled(true);
        GuiItem guiItem = view.items().get(event.getRawSlot());
        if (guiItem != null) {
            guiItem.handleClick(new GuiContext(player, event.getClick(), event.getSlot()));
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        GuiView view = openViews.remove(player.getUniqueId());
        if (view != null) {
            view.onClose(player);
        }
    }
}
