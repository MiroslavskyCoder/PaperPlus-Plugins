package com.webx.customenchants.utils;

import com.webx.customenchants.models.CustomEnchant;
import java.util.List;

public class EnchantUtils {
    
    public static CustomEnchant findByMaxLevel(List<CustomEnchant> enchants, int maxLevel) {
        return enchants.stream()
            .filter(e -> e.getMaxLevel() == maxLevel)
            .findFirst()
            .orElse(null);
    }
}
