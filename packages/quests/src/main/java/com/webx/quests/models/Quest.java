package com.webx.quests.models;

import java.util.*;

public class Quest {
    private final String id;
    private String name;
    private String description;
    private QuestType type;
    private QuestDifficulty difficulty;
    private List<QuestObjective> objectives;
    private Map<String, Object> rewards;
    private List<String> requirements; // prerequisite quest IDs
    private int level;
    private int timeLimit; // in seconds, 0 = no limit
    private boolean repeatable;
    private long cooldown; // in milliseconds
    private boolean daily;
    private String category;
    private Map<String, Object> metadata;

    public Quest(String id) {
        this.id = id;
        this.name = "Unknown Quest";
        this.description = "";
        this.type = QuestType.KILL;
        this.difficulty = QuestDifficulty.EASY;
        this.objectives = new ArrayList<>();
        this.rewards = new HashMap<>();
        this.requirements = new ArrayList<>();
        this.level = 1;
        this.timeLimit = 0;
        this.repeatable = false;
        this.cooldown = 0;
        this.daily = false;
        this.category = "general";
        this.metadata = new HashMap<>();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public QuestType getType() {
        return type;
    }

    public void setType(QuestType type) {
        this.type = type;
    }

    public QuestDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(QuestDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    public List<QuestObjective> getObjectives() {
        return objectives;
    }

    public void addObjective(QuestObjective objective) {
        objectives.add(objective);
    }

    public Map<String, Object> getRewards() {
        return rewards;
    }

    public void setReward(String key, Object value) {
        rewards.put(key, value);
    }

    public List<String> getRequirements() {
        return requirements;
    }

    public void addRequirement(String questId) {
        requirements.add(questId);
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public boolean isRepeatable() {
        return repeatable;
    }

    public void setRepeatable(boolean repeatable) {
        this.repeatable = repeatable;
    }

    public long getCooldown() {
        return cooldown;
    }

    public void setCooldown(long cooldown) {
        this.cooldown = cooldown;
    }

    public boolean isDaily() {
        return daily;
    }

    public void setDaily(boolean daily) {
        this.daily = daily;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(String key, Object value) {
        metadata.put(key, value);
    }

    public Object getMetadata(String key) {
        return metadata.get(key);
    }
}
