package com.webx.unigui;

import org.bukkit.inventory.ItemStack;
import java.util.function.Consumer;

/**
 * Represents a clickable item in a GUI.
 */
public final class GuiItem {
    private final ItemStack item;
    private final Consumer<GuiContext> onClick;

    public GuiItem(ItemStack item, Consumer<GuiContext> onClick) {
        this.item = item;
        this.onClick = onClick;
    }

    public ItemStack item() {
        return item;
    }

    public void handleClick(GuiContext ctx) {
        if (onClick != null) {
            onClick.accept(ctx);
        }
    }
}
