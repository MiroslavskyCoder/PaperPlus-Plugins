package com.webx.quests.database;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webx.quests.models.Quest;
import com.webx.quests.models.QuestProgress;
import com.webx.redis.ConfigStandardConfig;
import com.webx.redis.RedisConfig;
import com.webx.redis.RedisIO;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class AsyncQuestDataManager {
    private static final String PLUGIN_NAME = "Quests";
    private final JavaPlugin plugin;
    private final File dataDir;
    private final File questsDir;
    private final File progressDir;
    private final File statsDir;
    private final Gson gson;
    private final boolean useRedis;
    private final String questKeyPrefix;
    private final String progressKeyPrefix;
    private RedisIO redisIO;

    public AsyncQuestDataManager(JavaPlugin plugin) {
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

        String storageType = plugin.getConfig().getString("storage.type", "redis").toLowerCase();
        this.questKeyPrefix = plugin.getConfig().getString("storage.redis.quest-key-prefix", "quests:data");
        this.progressKeyPrefix = plugin.getConfig().getString("storage.redis.progress-key-prefix", "quests:progress");
        this.useRedis = "redis".equals(storageType);

        if (useRedis) {
            try {
                this.redisIO = new RedisIO(loadRedisConfig(), plugin.getLogger());
                this.redisIO.ping();
                plugin.getLogger().info("Quests Redis storage enabled (" + questKeyPrefix + ")");
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to initialize Redis, falling back to file storage: " + e.getMessage());
            }
        }
    }

    // Quest CRUD Operations
    public CompletableFuture<Void> saveQuestAsync(Quest quest) {
        if (useRedis && redisIO != null) {
            return CompletableFuture.runAsync(() -> saveQuestRedis(quest));
        }

        return CompletableFuture.runAsync(() -> {
            String json = gson.toJson(quest);
            File out = new File(questsDir, quest.getId() + ".json");
            writeString(out.toPath(), json);
        });
    }

    public CompletableFuture<Quest> loadQuestAsync(String questId) {
        if (useRedis && redisIO != null) {
            return CompletableFuture.supplyAsync(() -> loadQuestFromRedis(questId));
        }

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
        if (useRedis && redisIO != null) {
            return CompletableFuture.supplyAsync(() -> new ArrayList<>(loadQuestMap().values()));
        }

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
        if (useRedis && redisIO != null) {
            return CompletableFuture.runAsync(() -> deleteQuestFromRedis(questId));
        }

        return CompletableFuture.runAsync(() -> {
            File file = new File(questsDir, questId + ".json");
            file.delete();
        });
    }

    // Player Progress Operations
    public CompletableFuture<Void> saveProgressAsync(QuestProgress progress) {
        if (useRedis && redisIO != null) {
            return CompletableFuture.runAsync(() -> saveProgressRedis(progress));
        }

        return CompletableFuture.runAsync(() -> {
            String json = gson.toJson(progress);
            File playerDir = new File(progressDir, progress.getPlayer().toString());
            playerDir.mkdirs();
            File out = new File(playerDir, progress.getQuestId() + ".json");
            writeString(out.toPath(), json);
        });
    }

    public CompletableFuture<QuestProgress> loadProgressAsync(UUID playerId, String questId) {
        if (useRedis && redisIO != null) {
            return CompletableFuture.supplyAsync(() -> loadProgressFromRedis(playerId, questId));
        }

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
        if (useRedis && redisIO != null) {
            return CompletableFuture.supplyAsync(() -> new ArrayList<>(loadPlayerProgressMap(playerId).values()));
        }

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
        if (useRedis && redisIO != null) {
            return CompletableFuture.runAsync(() -> {
                Map<String, QuestProgress> map = loadPlayerProgressMap(playerId);
                map.remove(questId);
                savePlayerProgressMap(playerId, map);
            });
        }

        return CompletableFuture.runAsync(() -> {
            File file = new File(new File(progressDir, playerId.toString()), questId + ".json");
            file.delete();
        });
    }

    // Player Statistics
    public CompletableFuture<Void> incrementCompletedQuestsAsync(UUID playerId) {
        if (useRedis && redisIO != null) {
            return CompletableFuture.runAsync(() -> {
                int current = readIntRedis(completedKey(playerId), 0);
                redisIO.setString(completedKey(playerId), String.valueOf(current + 1));
            });
        }

        return CompletableFuture.runAsync(() -> {
            Path path = new File(statsDir, playerId.toString() + "_completed.json").toPath();
            int current = readInt(path, 0);
            writeString(path, String.valueOf(current + 1));
        });
    }

    public CompletableFuture<Integer> getCompletedQuestsCountAsync(UUID playerId) {
        if (useRedis && redisIO != null) {
            return CompletableFuture.supplyAsync(() -> readIntRedis(completedKey(playerId), 0));
        }

        return CompletableFuture.supplyAsync(() -> readInt(new File(statsDir, playerId.toString() + "_completed.json").toPath(), 0));
    }

    public CompletableFuture<Void> setLastQuestCompletionAsync(UUID playerId, long timestamp) {
        if (useRedis && redisIO != null) {
            return CompletableFuture.runAsync(() -> redisIO.setString(lastCompletionKey(playerId), String.valueOf(timestamp)));
        }

        return CompletableFuture.runAsync(() -> {
            writeString(new File(statsDir, playerId.toString() + "_last_completion.json").toPath(), String.valueOf(timestamp));
        });
    }

    public CompletableFuture<Long> getLastQuestCompletionAsync(UUID playerId) {
        if (useRedis && redisIO != null) {
            return CompletableFuture.supplyAsync(() -> readLongRedis(lastCompletionKey(playerId), 0L));
        }

        return CompletableFuture.supplyAsync(() -> readLong(new File(statsDir, playerId.toString() + "_last_completion.json").toPath(), 0L));
    }

    // Quest Completion History
    public CompletableFuture<Void> addCompletionHistoryAsync(UUID playerId, String questId) {
        if (useRedis && redisIO != null) {
            return CompletableFuture.runAsync(() -> {
                List<String> history = readStringListRedis(historyKey(playerId));
                history.add(questId + ":" + System.currentTimeMillis());
                redisIO.setString(historyKey(playerId), gson.toJson(history));
            });
        }

        return CompletableFuture.runAsync(() -> {
            Path path = new File(statsDir, playerId.toString() + "_history.json").toPath();
            List<String> history = readStringList(path);
            history.add(questId + ":" + System.currentTimeMillis());
            writeString(path, gson.toJson(history));
        });
    }

    public CompletableFuture<List<String>> getCompletionHistoryAsync(UUID playerId) {
        if (useRedis && redisIO != null) {
            return CompletableFuture.supplyAsync(() -> new ArrayList<>(readStringListRedis(historyKey(playerId))));
        }

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

    private void saveQuestRedis(Quest quest) {
        Map<String, Quest> map = loadQuestMap();
        map.put(quest.getId(), quest);
        redisIO.setString(questAllKey(), gson.toJson(map));
    }

    private Quest loadQuestFromRedis(String questId) {
        Map<String, Quest> map = loadQuestMap();
        return map.get(questId);
    }

    private void deleteQuestFromRedis(String questId) {
        Map<String, Quest> map = loadQuestMap();
        map.remove(questId);
        redisIO.setString(questAllKey(), gson.toJson(map));
    }

    private Map<String, Quest> loadQuestMap() {
        try {
            Optional<String> payload = redisIO.getString(questAllKey());
            if (payload.isPresent() && !payload.get().isBlank()) {
                Type type = new TypeToken<Map<String, Quest>>() {}.getType();
                Map<String, Quest> map = gson.fromJson(payload.get(), type);
                if (map != null) {
                    return map;
                }
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to read quests from Redis: " + e.getMessage());
        }
        return new HashMap<>();
    }

    private void saveProgressRedis(QuestProgress progress) {
        Map<String, QuestProgress> map = loadPlayerProgressMap(progress.getPlayer());
        map.put(progress.getQuestId(), progress);
        savePlayerProgressMap(progress.getPlayer(), map);
    }

    private QuestProgress loadProgressFromRedis(UUID playerId, String questId) {
        Map<String, QuestProgress> map = loadPlayerProgressMap(playerId);
        return map.get(questId);
    }

    private Map<String, QuestProgress> loadPlayerProgressMap(UUID playerId) {
        try {
            Optional<String> payload = redisIO.getString(progressKey(playerId));
            if (payload.isPresent() && !payload.get().isBlank()) {
                Type type = new TypeToken<Map<String, QuestProgress>>() {}.getType();
                Map<String, QuestProgress> map = gson.fromJson(payload.get(), type);
                if (map != null) {
                    return map;
                }
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to read quest progress from Redis: " + e.getMessage());
        }
        return new HashMap<>();
    }

    private void savePlayerProgressMap(UUID playerId, Map<String, QuestProgress> map) {
        redisIO.setString(progressKey(playerId), gson.toJson(map));
    }

    private String questAllKey() {
        return questKeyPrefix + ":all";
    }

    private String progressKey(UUID playerId) {
        return progressKeyPrefix + ":" + playerId;
    }

    private String completedKey(UUID playerId) {
        return progressKeyPrefix + ":" + playerId + ":completed";
    }

    private String lastCompletionKey(UUID playerId) {
        return progressKeyPrefix + ":" + playerId + ":last";
    }

    private String historyKey(UUID playerId) {
        return progressKeyPrefix + ":" + playerId + ":history";
    }

    private int readIntRedis(String key, int def) {
        try {
            Optional<String> val = redisIO.getString(key);
            if (val.isPresent() && !val.get().isBlank()) {
                return Integer.parseInt(val.get());
            }
        } catch (Exception ignored) {}
        return def;
    }

    private long readLongRedis(String key, long def) {
        try {
            Optional<String> val = redisIO.getString(key);
            if (val.isPresent() && !val.get().isBlank()) {
                return Long.parseLong(val.get());
            }
        } catch (Exception ignored) {}
        return def;
    }

    private List<String> readStringListRedis(String key) {
        try {
            Optional<String> val = redisIO.getString(key);
            if (val.isPresent() && !val.get().isBlank()) {
                List<String> list = gson.fromJson(val.get(), List.class);
                return list != null ? list : new ArrayList<>();
            }
        } catch (Exception ignored) {}
        return new ArrayList<>();
    }

    private RedisConfig loadRedisConfig() {
        boolean useRoot = plugin.getConfig().getBoolean("storage.redis.use-root-config", true);
        File serverRoot = plugin.getDataFolder().getParentFile() != null
                ? plugin.getDataFolder().getParentFile().getParentFile()
                : null;

        RedisConfig cfg = useRoot
                ? ConfigStandardConfig.load(serverRoot, new RedisConfig(), plugin.getLogger())
                : new RedisConfig();

        cfg.host = plugin.getConfig().getString("storage.redis.host", cfg.host);
        cfg.port = plugin.getConfig().getInt("storage.redis.port", cfg.port);
        cfg.password = plugin.getConfig().getString("storage.redis.password", cfg.password);
        cfg.database = plugin.getConfig().getInt("storage.redis.database", cfg.database);
        return cfg;
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
