package com.webx.abomination;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.Material;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

public class SkullUtil {
    // Creates a player head with a custom texture URL. Returns null if failed.
    public static ItemStack fromUrl(String url) {
        try {
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            if (meta == null) return null;
            GameProfile profile = new GameProfile(UUID.randomUUID(), "abomination");
            String json = "{\"textures\":{\"SKIN\":{\"url\":\"" + url + "\"}}}";
            String encoded = Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
            profile.getProperties().put("textures", new Property("textures", encoded));

            Method m = meta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
            m.setAccessible(true);
            m.invoke(meta, profile);
            skull.setItemMeta(meta);
            return skull;
        } catch (Exception e) {
            Bukkit.getLogger().warning("Failed to create custom head: " + e.getMessage());
            return null;
        }
    }
}
