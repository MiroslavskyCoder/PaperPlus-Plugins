package com.webx.quests.models;

import java.util.*;

public class Quest {
    private final String id;
    private String name;
    private String description;
    private List<String> objectives;
    private Map<String, Object> rewards;
    private int level;
    private boolean repeatable;

    public Quest(String id) {
        this.id = id;
        this.name = "Unknown Quest";
        this.description = "";
        this.objectives = new ArrayList<>();
        this.rewards = new HashMap<>();
        this.level = 1;
        this.repeatable = false;
    }

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

    public List<String> getObjectives() {
        return objectives;
    }

    public void addObjective(String objective) {
        objectives.add(objective);
    }

    public Map<String, Object> getRewards() {
        return rewards;
    }

    public void setReward(String key, Object value) {
        rewards.put(key, value);
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isRepeatable() {
        return repeatable;
    }

    public void setRepeatable(boolean repeatable) {
        this.repeatable = repeatable;
    }
}
