package com.webx.quests.loaders;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.webx.quests.models.*;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class QuestFileLoader {
    private final Plugin plugin;
    private final Gson gson;
    private final File questsFolder;

    public QuestFileLoader(Plugin plugin) {
        this.plugin = plugin;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.questsFolder = new File(plugin.getDataFolder(), "quests");
        
        if (!questsFolder.exists()) {
            questsFolder.mkdirs();
            copyDefaultQuests();
        }
    }

    /**
     * Load all quests from JSON files
     */
    public Map<String, Quest> loadAllQuests() {
        Map<String, Quest> quests = new HashMap<>();
        
        File[] files = questsFolder.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null || files.length == 0) {
            plugin.getLogger().warning("No quest files found in " + questsFolder.getPath());
            return quests;
        }
        
        for (File file : files) {
            try {
                Quest quest = loadQuestFromFile(file);
                if (quest != null) {
                    quests.put(quest.getId(), quest);
                    plugin.getLogger().info("Loaded quest: " + quest.getName() + " (" + quest.getId() + ")");
                }
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to load quest from file: " + file.getName(), e);
            }
        }
        
        plugin.getLogger().info("Loaded " + quests.size() + " quests from files");
        return quests;
    }

    /**
     * Load a single quest from a JSON file
     */
    private Quest loadQuestFromFile(File file) throws IOException {
        try (FileReader reader = new FileReader(file)) {
            QuestData data = gson.fromJson(reader, QuestData.class);
            return convertToQuest(data);
        }
    }

    /**
     * Convert QuestData to Quest object
     */
    private Quest convertToQuest(QuestData data) {
        Quest quest = new Quest(data.id);
        quest.setName(data.name);
        quest.setDescription(data.description);
        
        // Set type
        if (data.type != null) {
            quest.setType(QuestType.valueOf(data.type.toUpperCase()));
        }
        
        // Set difficulty
        if (data.difficulty != null) {
            quest.setDifficulty(QuestDifficulty.valueOf(data.difficulty.toUpperCase()));
        }
        
        quest.setLevel(data.level);
        quest.setCategory(data.category != null ? data.category : "general");
        quest.setTimeLimit(data.timeLimit);
        quest.setRepeatable(data.repeatable);
        quest.setDaily(data.daily);
        
        if (data.cooldown > 0) {
            quest.setCooldown(data.cooldown);
        }
        
        // Add requirements
        if (data.requirements != null) {
            for (String req : data.requirements) {
                quest.addRequirement(req);
            }
        }
        
        // Add objectives
        if (data.objectives != null) {
            for (ObjectiveData objData : data.objectives) {
                QuestObjective objective = new QuestObjective(
                    objData.id,
                    QuestType.valueOf(objData.type.toUpperCase()),
                    objData.description,
                    objData.target,
                    objData.required
                );
                
                if (objData.location != null) {
                    objective.setLocation(objData.location);
                }
                
                if (objData.optional) {
                    objective.setOptional(true);
                }
                
                quest.addObjective(objective);
            }
        }
        
        // Add rewards
        if (data.rewards != null) {
            if (data.rewards.money != null) {
                quest.setReward("money", data.rewards.money);
            }
            if (data.rewards.exp != null) {
                quest.setReward("exp", data.rewards.exp);
            }
            if (data.rewards.items != null) {
                quest.setReward("items", data.rewards.items);
            }
        }
        
        return quest;
    }

    /**
     * Copy default quest files from resources
     */
    private void copyDefaultQuests() {
        plugin.getLogger().info("Creating default quest files...");
        
        // Copy quest files from plugin resources to data folder
        File resourcesQuestsFolder = new File(plugin.getDataFolder().getParentFile().getParentFile(), 
            "packages/quests/src/main/resources/quests");
        
        if (resourcesQuestsFolder.exists()) {
            File[] files = resourcesQuestsFolder.listFiles((dir, name) -> name.endsWith(".json"));
            if (files != null) {
                for (File file : files) {
                    try {
                        java.nio.file.Files.copy(
                            file.toPath(), 
                            new File(questsFolder, file.getName()).toPath()
                        );
                    } catch (IOException e) {
                        plugin.getLogger().log(Level.WARNING, "Failed to copy quest file: " + file.getName(), e);
                    }
                }
            }
        }
    }

    // Data classes for JSON deserialization
    private static class QuestData {
        String id;
        String name;
        String description;
        String type;
        String difficulty;
        int level;
        String category;
        int timeLimit;
        boolean repeatable;
        boolean daily;
        long cooldown;
        List<String> requirements;
        List<ObjectiveData> objectives;
        RewardData rewards;
    }

    private static class ObjectiveData {
        String id;
        String type;
        String description;
        String target;
        int required;
        String location;
        boolean optional;
    }

    private static class RewardData {
        Integer money;
        Integer exp;
        List<ItemData> items;
    }

    private static class ItemData {
        String type;
        int amount;
        Map<String, Integer> enchantments;
    }
}
