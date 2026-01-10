package com.webx.cooking;

import org.bukkit.entity.Player;
import java.util.*;

public class CookingManager {
    private Map<UUID, Map<String, Integer>> recipesLearned = new HashMap<>();
    
    public void learnRecipe(Player player, String recipe) {
        UUID uuid = player.getUniqueId();
        recipesLearned.computeIfAbsent(uuid, k -> new HashMap<>()).put(recipe, 1);
        player.sendMessage("§6✓ Learned recipe: " + recipe);
    }
    
    public Set<String> getLearnedRecipes(Player player) {
        return recipesLearned.getOrDefault(player.getUniqueId(), new HashMap<>()).keySet();
    }
    
    public boolean knowsRecipe(Player player, String recipe) {
        return recipesLearned.getOrDefault(player.getUniqueId(), new HashMap<>()).containsKey(recipe);
    }
}
