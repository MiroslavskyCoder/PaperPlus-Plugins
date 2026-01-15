package com.webx.unigui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public record GuiContext(Player player, ClickType clickType, int slot) {
}
