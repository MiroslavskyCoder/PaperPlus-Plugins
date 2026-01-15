package com.webx.unigui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Map;

/**
 * Immutable view definition: provides inventory and handles clicks.
 */
public interface GuiView {
    Inventory inventory();
    Map<Integer, GuiItem> items();

    default void open(Player player) {
        player.openInventory(inventory());
    }

    default void onClose(Player player) {
        // optional override
    }

    static Inventory createInventory(int rows, String title) {
        int size = Math.max(1, Math.min(rows, 6)) * 9;
        return Bukkit.createInventory(null, size, title);
    }
}
