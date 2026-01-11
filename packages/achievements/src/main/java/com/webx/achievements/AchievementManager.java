package com.webx.achievements;

import org.bukkit.entity.Player;
import java.util.*;

public class AchievementManager {
    private AchievementsPlugin plugin;
    private Map<String, Achievement> achievements = new HashMap<>();
    private Map<UUID, Set<String>> playerAchievements = new HashMap<>();
    
    public AchievementManager(AchievementsPlugin plugin) {
        this.plugin = plugin;
        loadAchievements();
    }
    
    private void loadAchievements() {
        // Initialize default achievements
        achievements.put("first_step", new Achievement("first_step", "First Step", "Join the server"));
        achievements.put("first_kill", new Achievement("first_kill", "First Kill", "Kill your first mob"));
        achievements.put("collector", new Achievement("collector", "Collector", "Collect 1000 items"));
    }
    
    public void unlockAchievement(Player player, String achievementId) {
        UUID uuid = player.getUniqueId();
        playerAchievements.computeIfAbsent(uuid, k -> new HashSet<>()).add(achievementId);
        player.sendMessage("§6✓ Achievement unlocked: " + achievements.get(achievementId).getDisplayName());
    }
    
    public Set<String> getPlayerAchievements(UUID uuid) {
        return playerAchievements.getOrDefault(uuid, new HashSet<>());
    }
    
    public Achievement getAchievement(String id) {
        return achievements.get(id);
    }
    
    public Collection<Achievement> getAllAchievements() {
        return achievements.values();
    }
}
