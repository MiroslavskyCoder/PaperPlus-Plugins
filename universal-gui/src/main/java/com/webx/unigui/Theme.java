package com.webx.unigui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;

/**
 * Simple palette for themed GUI widgets.
 */
public record Theme(
        String titlePrefix,
        NamedTextColor primary,
        NamedTextColor secondary,
        NamedTextColor accent,
        Material backgroundMaterial,
        Material buttonMaterial,
        Material inputMaterial,
        Material checkboxOnMaterial,
        Material checkboxOffMaterial,
        Material fieldMaterial
) {
    public static Theme dark() {
        return new Theme(
                "§8■ ",
                NamedTextColor.WHITE,
                NamedTextColor.GRAY,
                NamedTextColor.GOLD,
                Material.GRAY_STAINED_GLASS_PANE,
                Material.DEEPSLATE_TILES,
                Material.LODESTONE,
                Material.LIME_STAINED_GLASS_PANE,
                Material.GRAY_STAINED_GLASS_PANE,
                Material.CHISELED_DEEPSLATE
        );
    }

    public static Theme light() {
        return new Theme(
                "§f◆ ",
                NamedTextColor.BLACK,
                NamedTextColor.DARK_GRAY,
                NamedTextColor.GOLD,
                Material.LIGHT_GRAY_STAINED_GLASS_PANE,
                Material.QUARTZ_BLOCK,
                Material.SMOOTH_QUARTZ,
                Material.LIME_STAINED_GLASS_PANE,
                Material.WHITE_STAINED_GLASS_PANE,
                Material.SMOOTH_STONE
        );
    }

    public static Theme neon() {
        return new Theme(
                "§d✦ ",
                NamedTextColor.WHITE,
                NamedTextColor.DARK_GRAY,
                NamedTextColor.LIGHT_PURPLE,
                Material.BLACK_STAINED_GLASS_PANE,
                Material.CRYING_OBSIDIAN,
                Material.OBSIDIAN,
                Material.MAGENTA_STAINED_GLASS_PANE,
                Material.BLACK_STAINED_GLASS_PANE,
                Material.PURPLE_STAINED_GLASS_PANE
        );
    }

    public static Theme retroTerminal() {
        return new Theme(
                "§2> ",
                NamedTextColor.GREEN,
                NamedTextColor.DARK_GREEN,
                NamedTextColor.GREEN,
                Material.GREEN_STAINED_GLASS_PANE,
                Material.DARK_OAK_PLANKS,
                Material.MOSSY_COBBLESTONE,
                Material.LIME_STAINED_GLASS_PANE,
                Material.GREEN_STAINED_GLASS_PANE,
                Material.MOSS_BLOCK
        );
    }

    public static Theme glass() {
        return new Theme(
                "§b◎ ",
                NamedTextColor.WHITE,
                NamedTextColor.GRAY,
                NamedTextColor.AQUA,
                Material.LIGHT_BLUE_STAINED_GLASS_PANE,
                Material.SEA_LANTERN,
                Material.PRISMARINE,
                Material.LIGHT_BLUE_STAINED_GLASS_PANE,
                Material.WHITE_STAINED_GLASS_PANE,
                Material.WHITE_STAINED_GLASS_PANE
        );
    }

    public Component title(String raw) {
        return Component.text(titlePrefix + raw, accent);
    }
}
