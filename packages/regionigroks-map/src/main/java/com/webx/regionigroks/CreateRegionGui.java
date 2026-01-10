package com.webx.regionigroks;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.ChatColor;
import org.bukkit.Color;

import java.util.Arrays;

public class CreateRegionGui {
    public static final String TITLE = ChatColor.GREEN + "Create Region: Choose Color";
    public static final String RADIUS_INPUT_TITLE = ChatColor.GREEN + "Region Radius Input";

    public static void openColorSelector(Player player) {
        Inventory inv = Bukkit.createInventory(player, 27, TITLE);
        addColor(inv, 10, Material.RED_WOOL, ChatColor.RED + "Red", Color.RED);
        addColor(inv, 11, Material.BLUE_WOOL, ChatColor.BLUE + "Blue", Color.BLUE);
        addColor(inv, 12, Material.GREEN_WOOL, ChatColor.GREEN + "Green", Color.GREEN);
        addColor(inv, 13, Material.YELLOW_WOOL, ChatColor.YELLOW + "Yellow", Color.YELLOW);
        addColor(inv, 14, Material.PURPLE_WOOL, ChatColor.LIGHT_PURPLE + "Purple", Color.PURPLE);
        addColor(inv, 15, Material.ORANGE_WOOL, ChatColor.GOLD + "Orange", Color.ORANGE);
        player.openInventory(inv);
    }

    private static void addColor(Inventory inv, int slot, Material mat, String name, Color color) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(Arrays.asList(ChatColor.GRAY + "Click to choose", ChatColor.DARK_GRAY + color.toString()));
            item.setItemMeta(meta);
        }
        inv.setItem(slot, item);
    }

    public static void openRadiusInput(Player player) {
        Inventory inv = Bukkit.createInventory(player, 9, RADIUS_INPUT_TITLE);
        ItemStack paper = new ItemStack(Material.PAPER);
        ItemMeta meta = paper.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.YELLOW + "Enter radius in chat");
            meta.setLore(Arrays.asList(
                ChatColor.GRAY + "Example: 32",
                ChatColor.GRAY + "Minimum: 5, Maximum: 256"
            ));
            paper.setItemMeta(meta);
        }
        inv.setItem(4, paper);
        player.openInventory(inv);
    }
}
