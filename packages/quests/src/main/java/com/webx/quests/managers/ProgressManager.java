package com.webx.quests.managers;

import com.webx.quests.QuestsPlugin;
import com.webx.quests.models.QuestProgress;

import java.util.*;

public class ProgressManager {
    private final QuestsPlugin plugin;
    private final Map<UUID, Map<String, QuestProgress>> playerProgress;

    public ProgressManager(QuestsPlugin plugin) {
        this.plugin = plugin;
        this.playerProgress = new HashMap<>();
    }

    public void loadProgress() {
        playerProgress.clear();
        // TODO: Load from storage
        plugin.getLogger().info("Loaded progress for players");
    }

    public void saveProgress() {
        // TODO: Save to storage
    }

    public void startQuest(UUID player, String questId) {
        Map<String, QuestProgress> progress = playerProgress.computeIfAbsent(player, k -> new HashMap<>());
        QuestProgress questProgress = new QuestProgress(player, questId);
        progress.put(questId, questProgress);
    }

    public QuestProgress getProgress(UUID player, String questId) {
        Map<String, QuestProgress> progress = playerProgress.get(player);
        return progress != null ? progress.get(questId) : null;
    }

    public void completeQuest(UUID player, String questId) {
        QuestProgress progress = getProgress(player, questId);
        if (progress != null) {
            progress.setCompleted(true);
        }
    }

    public void abandonQuest(UUID player, String questId) {
        Map<String, QuestProgress> progress = playerProgress.get(player);
        if (progress != null) {
            progress.remove(questId);
        }
    }

    public Collection<QuestProgress> getActiveQuests(UUID player) {
        Map<String, QuestProgress> progress = playerProgress.get(player);
        return progress != null ? progress.values() : new ArrayList<>();
    }
}
