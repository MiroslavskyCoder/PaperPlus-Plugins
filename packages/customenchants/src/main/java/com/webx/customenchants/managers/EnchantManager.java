package com.webx.customenchants.managers;

import com.webx.customenchants.models.CustomEnchant;
import java.util.*;

public class EnchantManager {
    private final Map<String, CustomEnchant> enchants = new HashMap<>();
    
    public void registerEnchant(CustomEnchant enchant) {
        enchants.put(enchant.getName(), enchant);
    }
    
    public CustomEnchant getEnchant(String name) {
        return enchants.get(name);
    }
    
    public Collection<CustomEnchant> getAllEnchants() {
        return enchants.values();
    }
}
