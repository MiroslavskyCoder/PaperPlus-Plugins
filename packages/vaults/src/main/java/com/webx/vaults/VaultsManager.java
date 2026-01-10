package com.webx.vaults;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.*;

public class VaultsManager {
    private Map<UUID, Vault> playerVaults = new HashMap<>();
    
    public void createVault(Player player, String vaultName) {
        UUID uuid = player.getUniqueId();
        playerVaults.put(uuid, new Vault(vaultName));
        player.sendMessage("§aVault §f" + vaultName + " §acreated!");
    }
    
    public void storeItem(Player player, ItemStack item) {
        Vault vault = playerVaults.get(player.getUniqueId());
        if (vault != null) {
            vault.addItem(item);
            player.sendMessage("§aItem stored in vault!");
        }
    }
    
    static class Vault {
        String name;
        List<ItemStack> items = new ArrayList<>();
        
        Vault(String name) {
            this.name = name;
        }
        
        void addItem(ItemStack item) {
            items.add(item);
        }
    }
}
