package com.webx.quests.database;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webx.quests.models.Quest;
import com.webx.quests.models.QuestProgress;
import lxxv.shared.dbjson.SharedPluginDatabase;
import lxxv.shared.dbjson.PluginDataHelper;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class AsyncQuestDataManager {
    private static final String PLUGIN_NAME = "Quests";
    private final Plugin plugin;
    private final SharedPluginDatabase database;
    private final PluginDataHelper helper;
    private final Gson gson;

    public AsyncQuestDataManager(Plugin plugin) {
        this.plugin = plugin;
        this.database = SharedPluginDatabase.getInstance(plugin.getDataFolder().getParentFile().getParentFile());
        this.helper = new PluginDataHelper(database, PLUGIN_NAME);
        this.gson = new Gson();
    }

    // Quest CRUD Operations
    public CompletableFuture<Void> saveQuestAsync(Quest quest) {
        return CompletableFuture.runAsync(() -> {
            String json = gson.toJson(quest);
            database.setPluginValue(PLUGIN_NAME, "quest_" + quest.getId(), json);
            database.save();
        });
    }

    public CompletableFuture<Quest> loadQuestAsync(String questId) {
        return CompletableFuture.supplyAsync(() -> {
            Object data = database.getPluginData(PLUGIN_NAME, "quest_" + questId);
            if (data == null) return null;
            return gson.fromJson(data.toString(), Quest.class);
        });
    }

    public CompletableFuture<List<Quest>> loadAllQuestsAsync() {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, Object> pluginData = database.getPluginData(PLUGIN_NAME);
            List<Quest> quests = new ArrayList<>();
            
            for (Map.Entry<String, Object> entry : pluginData.entrySet()) {
                if (entry.getKey().startsWith("quest_")) {
                    Quest quest = gson.fromJson(entry.getValue().toString(), Quest.class);
                    if (quest != null) {
                        quests.add(quest);
                    }
                }
            }
            
            return quests;
        });
    }

    public CompletableFuture<Void> deleteQuestAsync(String questId) {
        return CompletableFuture.runAsync(() -> {
            database.setPluginValue(PLUGIN_NAME, "quest_" + questId, null);
            database.save();
        });
    }

    // Player Progress Operations
    public CompletableFuture<Void> saveProgressAsync(QuestProgress progress) {
        return CompletableFuture.runAsync(() -> {
            String json = gson.toJson(progress);
            String key = "progress_" + progress.getQuestId();
            helper.setPlayerObject(progress.getPlayerId(), key, json);
            database.save();
        });
    }

    public CompletableFuture<QuestProgress> loadProgressAsync(UUID playerId, String questId) {
        return CompletableFuture.supplyAsync(() -> {
            String key = "progress_" + questId;
            Object data = helper.getPlayerObject(playerId, key);
            if (data == null) return null;
            return gson.fromJson(data.toString(), QuestProgress.class);
        });
    }

    public CompletableFuture<List<QuestProgress>> loadPlayerProgressesAsync(UUID playerId) {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, Object> playerData = database.getPlayerData(playerId.toString());
            List<QuestProgress> progresses = new ArrayList<>();
            
            for (Map.Entry<String, Object> entry : playerData.entrySet()) {
                if (entry.getKey().startsWith(PLUGIN_NAME + "_progress_")) {
                    QuestProgress progress = gson.fromJson(entry.getValue().toString(), QuestProgress.class);
                    if (progress != null) {
                        progresses.add(progress);
                    }
                }
            }
            
            return progresses;
        });
    }

    public CompletableFuture<Void> deleteProgressAsync(UUID playerId, String questId) {
        return CompletableFuture.runAsync(() -> {
            String key = "progress_" + questId;
            helper.setPlayerObject(playerId, key, null);
            database.save();
        });
    }

    // Player Statistics
    public CompletableFuture<Void> incrementCompletedQuestsAsync(UUID playerId) {
        return CompletableFuture.runAsync(() -> {
            int current = helper.getPlayerInt(playerId, "completed_quests", 0);
            helper.setPlayerInt(playerId, "completed_quests", current + 1);
            database.save();
        });
    }

    public CompletableFuture<Integer> getCompletedQuestsCountAsync(UUID playerId) {
        return CompletableFuture.supplyAsync(() -> 
            helper.getPlayerInt(playerId, "completed_quests", 0)
        );
    }

    public CompletableFuture<Void> setLastQuestCompletionAsync(UUID playerId, long timestamp) {
        return CompletableFuture.runAsync(() -> {
            helper.setPlayerLong(playerId, "last_quest_completion", timestamp);
            database.save();
        });
    }

    public CompletableFuture<Long> getLastQuestCompletionAsync(UUID playerId) {
        return CompletableFuture.supplyAsync(() -> 
            helper.getPlayerLong(playerId, "last_quest_completion", 0L)
        );
    }

    // Quest Completion History
    public CompletableFuture<Void> addCompletionHistoryAsync(UUID playerId, String questId) {
        return CompletableFuture.runAsync(() -> {
            List<String> history = helper.getPlayerArray(playerId, "completion_history", String.class);
            if (history == null) history = new ArrayList<>();
            history.add(questId + ":" + System.currentTimeMillis());
            helper.setPlayerArray(playerId, "completion_history", history);
            database.save();
        });
    }

    public CompletableFuture<List<String>> getCompletionHistoryAsync(UUID playerId) {
        return CompletableFuture.supplyAsync(() -> {
            List<String> history = helper.getPlayerArray(playerId, "completion_history", String.class);
            return history != null ? history : new ArrayList<>();
        });
    }

    // Synchronous Helper Methods (for main thread usage)
    public void saveQuestSync(Quest quest) {
        new BukkitRunnable() {
            @Override
            public void run() {
                saveQuestAsync(quest);
            }
        }.runTaskAsynchronously(plugin);
    }

    public void saveProgressSync(QuestProgress progress) {
        new BukkitRunnable() {
            @Override
            public void run() {
                saveProgressAsync(progress);
            }
        }.runTaskAsynchronously(plugin);
    }

    // Reload database from disk
    public CompletableFuture<Void> reloadAsync() {
        return CompletableFuture.runAsync(() -> database.reload());
    }
}
