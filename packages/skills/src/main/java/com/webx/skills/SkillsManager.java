package com.webx.skills;

import org.bukkit.entity.Player;
import java.util.*;

public class SkillsManager {
    private Map<UUID, Map<String, Integer>> playerSkills = new HashMap<>();
    private static final String[] SKILLS = {"strength", "agility", "intelligence", "endurance", "wisdom"};
    
    public void addSkillXp(Player player, String skill, int xp) {
        UUID uuid = player.getUniqueId();
        Map<String, Integer> skills = playerSkills.computeIfAbsent(uuid, k -> initializeSkills());
        skills.put(skill, skills.getOrDefault(skill, 0) + xp);
        player.sendMessage("§b+" + xp + " " + skill + " XP");
    }
    
    private Map<String, Integer> initializeSkills() {
        Map<String, Integer> skills = new HashMap<>();
        for (String skill : SKILLS) {
            skills.put(skill, 0);
        }
        return skills;
    }
    
    public Map<String, Integer> getSkills(Player player) {
        return playerSkills.getOrDefault(player.getUniqueId(), initializeSkills());
    }
}
