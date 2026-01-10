package com.webx.abomination;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;

import java.net.URL;
import java.util.UUID;

public class SkullUtil {
    // Creates a player head with a custom texture URL. Returns null if failed.
    public static ItemStack fromUrl(String url) {
        try {
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            if (meta == null) return null;

            PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID(), "abomination");
            profile.getTextures().setSkin(new URL(url));
            meta.setOwnerProfile(profile);
            skull.setItemMeta(meta);
            return skull;
        } catch (Exception e) {
            Bukkit.getLogger().warning("Failed to create custom head: " + e.getMessage());
            return null;
        }
    }
}
