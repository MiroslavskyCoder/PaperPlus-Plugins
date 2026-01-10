package com.webx.warps.managers;

import com.webx.warps.WarpsPlugin;
import com.webx.warps.models.Warp;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GUIManager {
    private final WarpsPlugin plugin;

    public GUIManager(WarpsPlugin plugin) {
        this.plugin = plugin;
    }

    public void openWarpsGUI(Player player) {
        if (!plugin.getConfig().getBoolean("gui.enabled", true)) {
            return;
        }

        int rows = plugin.getConfig().getInt("gui.rows", 3);
        String title = plugin.getConfigManager().getColoredString("gui.title");

        Inventory inv = Bukkit.createInventory(null, rows * 9, title);

        List<Warp> warps = plugin.getWarpManager().getWarpsForPlayer(player);
        int slot = 0;

        for (Warp warp : warps) {
            if (slot >= rows * 9) break;

            ItemStack item = createWarpItem(warp);
            inv.setItem(slot++, item);
        }

        // Fill empty slots
        if (plugin.getConfig().getBoolean("gui.fill-empty", true)) {
            ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            ItemMeta meta = filler.getItemMeta();
            meta.setDisplayName(" ");
            filler.setItemMeta(meta);

            for (int i = slot; i < rows * 9; i++) {
                inv.setItem(i, filler);
            }
        }

        player.openInventory(inv);
    }

    private ItemStack createWarpItem(Warp warp) {
        Material material = Material.ENDER_PEARL;
        if (warp.getIcon() != null) {
            try {
                material = Material.valueOf(warp.getIcon().toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName("§6" + warp.getName());

        List<String> lore = new ArrayList<>();
        if (warp.getDescription() != null) {
            lore.add("§7" + warp.getDescription());
            lore.add("");
        }
        lore.add("§eClick to teleport!");
        if (warp.getCost() > 0) {
            lore.add("§6Cost: §e" + warp.getCost());
        }

        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }
}
