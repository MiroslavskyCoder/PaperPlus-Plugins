package com.webx.cosmetics;

import org.bukkit.entity.Player;
import java.util.*;

public class CosmeticsManager {
    private Map<UUID, Set<String>> purchasedCosmetics = new HashMap<>();
    
    public void purchaseCosmetic(Player player, String cosmetic) {
        UUID uuid = player.getUniqueId();
        purchasedCosmetics.computeIfAbsent(uuid, k -> new HashSet<>()).add(cosmetic);
        player.sendMessage("§6✓ Purchased cosmetic: " + cosmetic);
    }
    
    public Set<String> getCosmetics(Player player) {
        return purchasedCosmetics.getOrDefault(player.getUniqueId(), new HashSet<>());
    }
    
    public boolean hasCosmetic(Player player, String cosmetic) {
        return purchasedCosmetics.getOrDefault(player.getUniqueId(), new HashSet<>()).contains(cosmetic);
    }
}
