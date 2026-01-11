package com.webx.quests.models;

import java.util.*;

public class QuestProgress {
    private final UUID player;
    private final String questId;
    private final Map<String, Integer> objectiveProgress;
    private long startedAt;
    private long completedAt;
    private boolean completed;

    public QuestProgress(UUID player, String questId) {
        this.player = player;
        this.questId = questId;
        this.objectiveProgress = new HashMap<>();
        this.startedAt = System.currentTimeMillis();
        this.completedAt = -1;
        this.completed = false;
    }

    public UUID getPlayer() {
        return player;
    }

    public String getQuestId() {
        return questId;
    }

    public void setObjectiveProgress(String objective, int progress) {
        objectiveProgress.put(objective, progress);
    }

    public int getObjectiveProgress(String objective) {
        return objectiveProgress.getOrDefault(objective, 0);
    }

    public void addObjectiveProgress(String objective, int amount) {
        int current = getObjectiveProgress(objective);
        objectiveProgress.put(objective, current + amount);
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
        if (completed) {
            this.completedAt = System.currentTimeMillis();
        }
    }

    public long getStartedAt() {
        return startedAt;
    }

    public long getCompletedAt() {
        return completedAt;
    }
}
