package com.webx.unigui;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Simple paginated view with next/prev controls.
 */
public final class PagedView<T> implements GuiView {
    private final Inventory inventory;
    private final Map<Integer, GuiItem> items = new HashMap<>();
    private final List<T> entries;
    private final Function<T, ItemStack> renderer;
    private final int pageSize;
    private int page = 0;

    public PagedView(String title, int rows, List<T> entries, Function<T, ItemStack> renderer) {
        this.inventory = GuiView.createInventory(rows, title);
        this.entries = entries;
        this.renderer = renderer;
        this.pageSize = (rows - 1) * 9; // reserve last row for controls
        renderPage();
    }

    @Override
    public Inventory inventory() {
        return inventory;
    }

    @Override
    public Map<Integer, GuiItem> items() {
        return items;
    }

    private void renderPage() {
        items.clear();
        inventory.clear();

        int start = page * pageSize;
        int end = Math.min(entries.size(), start + pageSize);
        int slot = 0;
        for (int i = start; i < end; i++) {
            ItemStack stack = renderer.apply(entries.get(i));
            inventory.setItem(slot, stack);
            items.put(slot, new GuiItem(stack, ctx -> {}));
            slot++;
        }

        // Prev button
        ItemStack prev = button(Material.ARROW, Component.text("Previous"));
        inventory.setItem(inventory.getSize() - 9, prev);
        items.put(inventory.getSize() - 9, new GuiItem(prev, ctx -> changePage(-1)));

        // Next button
        ItemStack next = button(Material.ARROW, Component.text("Next"));
        inventory.setItem(inventory.getSize() - 1, next);
        items.put(inventory.getSize() - 1, new GuiItem(next, ctx -> changePage(1)));
    }

    private void changePage(int delta) {
        int maxPage = Math.max(0, (entries.size() - 1) / pageSize);
        page = Math.max(0, Math.min(page + delta, maxPage));
        renderPage();
    }

    private ItemStack button(Material material, Component name) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        meta.displayName(name);
        stack.setItemMeta(meta);
        return stack;
    }

    @Override
    public void onClose(Player player) {
        // no-op by default
    }
}
