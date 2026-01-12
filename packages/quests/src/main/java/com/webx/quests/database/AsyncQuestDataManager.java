package com.webx.quests.database;

import com.google.gson.Gson;
import com.webx.quests.models.Quest;
import com.webx.quests.models.QuestProgress;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class AsyncQuestDataManager {
    private static final String PLUGIN_NAME = "Quests";
    private final Plugin plugin;
    private final File dataDir;
    private final File questsDir;
    private final File progressDir;
    private final File statsDir;
    private final Gson gson;

    public AsyncQuestDataManager(Plugin plugin) {
        this.plugin = plugin;
        this.dataDir = new File(plugin.getDataFolder(), "data");
        this.questsDir = new File(dataDir, "quests");
        this.progressDir = new File(dataDir, "progress");
        this.statsDir = new File(dataDir, "stats");
        this.dataDir.mkdirs();
        this.questsDir.mkdirs();
        this.progressDir.mkdirs();
        this.statsDir.mkdirs();
        this.gson = new Gson();
    }

    // Quest CRUD Operations
    public CompletableFuture<Void> saveQuestAsync(Quest quest) {
        return CompletableFuture.runAsync(() -> {
            String json = gson.toJson(quest);
            File out = new File(questsDir, quest.getId() + ".json");
            writeString(out.toPath(), json);
        });
    }

    public CompletableFuture<Quest> loadQuestAsync(String questId) {
        return CompletableFuture.supplyAsync(() -> {
            Path path = new File(questsDir, questId + ".json").toPath();
            if (!Files.exists(path)) return null;
            try {
                String json = Files.readString(path);
                return gson.fromJson(json, Quest.class);
            } catch (IOException e) {
                return null;
            }
        });
    }

    public CompletableFuture<List<Quest>> loadAllQuestsAsync() {
        return CompletableFuture.supplyAsync(() -> {
            List<Quest> quests = new ArrayList<>();
            File[] files = questsDir.listFiles((dir, name) -> name.endsWith(".json"));
            if (files != null) {
                for (File file : files) {
                    try {
                        String json = Files.readString(file.toPath());
                        Quest quest = gson.fromJson(json, Quest.class);
                        if (quest != null) {
                            quests.add(quest);
                        }
                    } catch (IOException ignored) {}
                }
            }
            return quests;
        });
    }

    public CompletableFuture<Void> deleteQuestAsync(String questId) {
        return CompletableFuture.runAsync(() -> {
            File file = new File(questsDir, questId + ".json");
            file.delete();
        });
    }

    // Player Progress Operations
    public CompletableFuture<Void> saveProgressAsync(QuestProgress progress) {
        return CompletableFuture.runAsync(() -> {
            String json = gson.toJson(progress);
            File playerDir = new File(progressDir, progress.getPlayer().toString());
            playerDir.mkdirs();
            File out = new File(playerDir, progress.getQuestId() + ".json");
            writeString(out.toPath(), json);
        });
    }

    public CompletableFuture<QuestProgress> loadProgressAsync(UUID playerId, String questId) {
        return CompletableFuture.supplyAsync(() -> {
            Path path = new File(new File(progressDir, playerId.toString()), questId + ".json").toPath();
            if (!Files.exists(path)) return null;
            try {
                String json = Files.readString(path);
                return gson.fromJson(json, QuestProgress.class);
            } catch (IOException e) {
                return null;
            }
        });
    }

    public CompletableFuture<List<QuestProgress>> loadPlayerProgressesAsync(UUID playerId) {
        return CompletableFuture.supplyAsync(() -> {
            List<QuestProgress> progresses = new ArrayList<>();
            File playerDir = new File(progressDir, playerId.toString());
            File[] files = playerDir.listFiles((dir, name) -> name.endsWith(".json"));
            if (files != null) {
                for (File file : files) {
                    try {
                        String json = Files.readString(file.toPath());
                        QuestProgress progress = gson.fromJson(json, QuestProgress.class);
                        if (progress != null) {
                            progresses.add(progress);
                        }
                    } catch (IOException ignored) {}
                }
            }
            return progresses;
        });
    }

    public CompletableFuture<Void> deleteProgressAsync(UUID playerId, String questId) {
        return CompletableFuture.runAsync(() -> {
            File file = new File(new File(progressDir, playerId.toString()), questId + ".json");
            file.delete();
        });
    }

    // Player Statistics
    public CompletableFuture<Void> incrementCompletedQuestsAsync(UUID playerId) {
        return CompletableFuture.runAsync(() -> {
            Path path = new File(statsDir, playerId.toString() + "_completed.json").toPath();
            int current = readInt(path, 0);
            writeString(path, String.valueOf(current + 1));
        });
    }

    public CompletableFuture<Integer> getCompletedQuestsCountAsync(UUID playerId) {
        return CompletableFuture.supplyAsync(() -> readInt(new File(statsDir, playerId.toString() + "_completed.json").toPath(), 0));
    }

    public CompletableFuture<Void> setLastQuestCompletionAsync(UUID playerId, long timestamp) {
        return CompletableFuture.runAsync(() -> {
            writeString(new File(statsDir, playerId.toString() + "_last_completion.json").toPath(), String.valueOf(timestamp));
        });
    }

    public CompletableFuture<Long> getLastQuestCompletionAsync(UUID playerId) {
        return CompletableFuture.supplyAsync(() -> readLong(new File(statsDir, playerId.toString() + "_last_completion.json").toPath(), 0L));
    }

    // Quest Completion History
    public CompletableFuture<Void> addCompletionHistoryAsync(UUID playerId, String questId) {
        return CompletableFuture.runAsync(() -> {
            Path path = new File(statsDir, playerId.toString() + "_history.json").toPath();
            List<String> history = readStringList(path);
            history.add(questId + ":" + System.currentTimeMillis());
            writeString(path, gson.toJson(history));
        });
    }

    public CompletableFuture<List<String>> getCompletionHistoryAsync(UUID playerId) {
        return CompletableFuture.supplyAsync(() -> {
            Path path = new File(statsDir, playerId.toString() + "_history.json").toPath();
            List<String> history = readStringList(path);
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
        return CompletableFuture.completedFuture(null);
    }

    private void writeString(Path path, String content) {
        try {
            Files.createDirectories(path.getParent());
            Files.writeString(path, content, StandardCharsets.UTF_8);
        } catch (IOException ignored) {}
    }

    private int readInt(Path path, int def) {
        if (!Files.exists(path)) return def;
        try {
            return Integer.parseInt(Files.readString(path, StandardCharsets.UTF_8));
        } catch (Exception e) {
            return def;
        }
    }

    private long readLong(Path path, long def) {
        if (!Files.exists(path)) return def;
        try {
            return Long.parseLong(Files.readString(path, StandardCharsets.UTF_8));
        } catch (Exception e) {
            return def;
        }
    }

    private List<String> readStringList(Path path) {
        if (!Files.exists(path)) return new ArrayList<>();
        try {
            String json = Files.readString(path, StandardCharsets.UTF_8);
            List<String> list = gson.fromJson(json, List.class);
            return list != null ? list : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
