package com.webx.quests.models;

import java.util.*;

public class QuestProgress {
    private final UUID player;
    private final String questId;
    private final Map<String, Integer> objectiveProgress;
    private long startedAt;
    private long completedAt;
    private QuestStatus status;
    private long lastUpdateTime;
    private int attempts;

    public QuestProgress(UUID player, String questId) {
        this.player = player;
        this.questId = questId;
        this.objectiveProgress = new HashMap<>();
        this.startedAt = System.currentTimeMillis();
        this.completedAt = -1;
        this.status = QuestStatus.NOT_STARTED;
        this.lastUpdateTime = System.currentTimeMillis();
        this.attempts = 0;
    }

    public UUID getPlayer() {
        return player;
    }

    public String getQuestId() {
        return questId;
    }

    public QuestStatus getStatus() {
        return status;
    }

    public void setStatus(QuestStatus status) {
        this.status = status;
        this.lastUpdateTime = System.currentTimeMillis();
        if (status == QuestStatus.IN_PROGRESS && startedAt == 0) {
            this.startedAt = System.currentTimeMillis();
        } else if (status == QuestStatus.COMPLETED) {
            this.completedAt = System.currentTimeMillis();
        }
    }

    public void setObjectiveProgress(String objective, int progress) {
        objectiveProgress.put(objective, progress);
        this.lastUpdateTime = System.currentTimeMillis();
    }

    public int getObjectiveProgress(String objective) {
        return objectiveProgress.getOrDefault(objective, 0);
    }

    public void addObjectiveProgress(String objective, int amount) {
        int current = getObjectiveProgress(objective);
        objectiveProgress.put(objective, current + amount);
        this.lastUpdateTime = System.currentTimeMillis();
    }

    public boolean isCompleted() {
        return status == QuestStatus.COMPLETED;
    }

    public void setCompleted(boolean completed) {
        this.status = completed ? QuestStatus.COMPLETED : QuestStatus.IN_PROGRESS;
        if (completed) {
            this.completedAt = System.currentTimeMillis();
        }
        this.lastUpdateTime = System.currentTimeMillis();
    }

    public long getStartedAt() {
        return startedAt;
    }

    public long getCompletedAt() {
        return completedAt;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public int getAttempts() {
        return attempts;
    }

    public void incrementAttempts() {
        this.attempts++;
    }

    public Map<String, Integer> getAllProgress() {
        return new HashMap<>(objectiveProgress);
    }

    public void reset() {
        this.status = QuestStatus.NOT_STARTED;
        this.objectiveProgress.clear();
        this.startedAt = System.currentTimeMillis();
        this.completedAt = -1;
        this.lastUpdateTime = System.currentTimeMillis();
    }
}
}
