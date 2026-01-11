package com.webx.enchanting;

import org.bukkit.inventory.ItemStack;
import java.util.*;

public class EnchantingManager {
    private Map<String, Integer> enchantLevels = new HashMap<>();
    
    public void addEnchant(String name, int maxLevel) {
        enchantLevels.put(name, maxLevel);
    }
    
    public boolean canEnchant(String name, int level) {
        return enchantLevels.containsKey(name) && level <= enchantLevels.get(name);
    }
    
    public int getMaxLevel(String name) {
        return enchantLevels.getOrDefault(name, 0);
    }
    
    public Collection<String> getAllEnchants() {
        return enchantLevels.keySet();
    }
}
