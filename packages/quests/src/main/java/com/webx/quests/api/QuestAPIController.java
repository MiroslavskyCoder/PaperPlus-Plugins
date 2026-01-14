package com.webx.quests.api;

import com.google.gson.Gson;
import com.webx.quests.QuestsPlugin;
import com.webx.quests.database.AsyncQuestDataManager;
import com.webx.quests.models.Quest;
import com.webx.quests.models.QuestProgress;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class QuestAPIController {
    private final QuestsPlugin plugin;
    private final AsyncQuestDataManager dataManager;
    private final Gson gson;

    public QuestAPIController(QuestsPlugin plugin) {
        this.plugin = plugin;
        this.dataManager = new AsyncQuestDataManager(plugin);
        this.gson = new Gson();
    }

    public void registerRoutes(Javalin app) {
        // Quest Management Endpoints
        app.get("/api/quests", this::getAllQuests);
        app.get("/api/quests/{id}", this::getQuest);
        app.post("/api/quests", this::createQuest);
        app.put("/api/quests/{id}", this::updateQuest);
        app.delete("/api/quests/{id}", this::deleteQuest);

        // Player Progress Endpoints
        app.get("/api/quests/progress/{playerId}", this::getPlayerProgress);
        app.get("/api/quests/progress/{playerId}/{questId}", this::getSpecificProgress);
        app.post("/api/quests/progress/{playerId}/{questId}/start", this::startQuest);
        app.post("/api/quests/progress/{playerId}/{questId}/complete", this::completeQuest);
        app.post("/api/quests/progress/{playerId}/{questId}/reset", this::resetQuest);

        // Statistics Endpoints
        app.get("/api/quests/stats/{playerId}", this::getPlayerStats);
        app.get("/api/quests/leaderboard", this::getLeaderboard);

        // Admin Endpoints
        app.post("/api/quests/reload", this::reloadQuests);
        app.get("/api/quests/types", this::getQuestTypes);
        app.get("/api/quests/difficulties", this::getDifficulties);
    }

    // Get all quests
    private void getAllQuests(Context ctx) {
        dataManager.loadAllQuestsAsync().thenAccept(quests -> {
            ctx.json(quests);
        }).exceptionally(ex -> {
            ctx.status(500).json(Map.of("error", "Failed to load quests", "message", ex.getMessage()));
            return null;
        });
    }

    // Get specific quest
    private void getQuest(Context ctx) {
        String questId = ctx.pathParam("id");
        dataManager.loadQuestAsync(questId).thenAccept(quest -> {
            if (quest != null) {
                ctx.json(quest);
            } else {
                ctx.status(404).json(Map.of("error", "Quest not found"));
            }
        }).exceptionally(ex -> {
            ctx.status(500).json(Map.of("error", "Failed to load quest", "message", ex.getMessage()));
            return null;
        });
    }

    // Create new quest
    private void createQuest(Context ctx) {
        try {
            Quest quest = gson.fromJson(ctx.body(), Quest.class);
            dataManager.saveQuestAsync(quest).thenAccept(v -> {
                ctx.status(201).json(Map.of("success", true, "questId", quest.getId()));
            }).exceptionally(ex -> {
                ctx.status(500).json(Map.of("error", "Failed to create quest", "message", ex.getMessage()));
                return null;
            });
        } catch (Exception e) {
            ctx.status(400).json(Map.of("error", "Invalid quest data", "message", e.getMessage()));
        }
    }

    // Update existing quest
    private void updateQuest(Context ctx) {
        String questId = ctx.pathParam("id");
        try {
            Quest quest = gson.fromJson(ctx.body(), Quest.class);
            if (!quest.getId().equals(questId)) {
                ctx.status(400).json(Map.of("error", "Quest ID mismatch"));
                return;
            }
            
            dataManager.saveQuestAsync(quest).thenAccept(v -> {
                ctx.json(Map.of("success", true, "questId", quest.getId()));
            }).exceptionally(ex -> {
                ctx.status(500).json(Map.of("error", "Failed to update quest", "message", ex.getMessage()));
                return null;
            });
        } catch (Exception e) {
            ctx.status(400).json(Map.of("error", "Invalid quest data", "message", e.getMessage()));
        }
    }

    // Delete quest
    private void deleteQuest(Context ctx) {
        String questId = ctx.pathParam("id");
        dataManager.deleteQuestAsync(questId).thenAccept(v -> {
            ctx.json(Map.of("success", true));
        }).exceptionally(ex -> {
            ctx.status(500).json(Map.of("error", "Failed to delete quest", "message", ex.getMessage()));
            return null;
        });
    }

    // Get player progress
    private void getPlayerProgress(Context ctx) {
        try {
            UUID playerId = UUID.fromString(ctx.pathParam("playerId"));
            dataManager.loadPlayerProgressesAsync(playerId).thenAccept(progresses -> {
                ctx.json(progresses);
            }).exceptionally(ex -> {
                ctx.status(500).json(Map.of("error", "Failed to load progress", "message", ex.getMessage()));
                return null;
            });
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(Map.of("error", "Invalid player ID"));
        }
    }

    // Get specific progress
    private void getSpecificProgress(Context ctx) {
        try {
            UUID playerId = UUID.fromString(ctx.pathParam("playerId"));
            String questId = ctx.pathParam("questId");
            
            dataManager.loadProgressAsync(playerId, questId).thenAccept(progress -> {
                if (progress != null) {
                    ctx.json(progress);
                } else {
                    ctx.status(404).json(Map.of("error", "Progress not found"));
                }
            }).exceptionally(ex -> {
                ctx.status(500).json(Map.of("error", "Failed to load progress", "message", ex.getMessage()));
                return null;
            });
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(Map.of("error", "Invalid player ID"));
        }
    }

    // Start quest for player
    private void startQuest(Context ctx) {
        try {
            UUID playerId = UUID.fromString(ctx.pathParam("playerId"));
            String questId = ctx.pathParam("questId");
            
            // Check if quest exists
            dataManager.loadQuestAsync(questId).thenAccept(quest -> {
                if (quest == null) {
                    ctx.status(404).json(Map.of("error", "Quest not found"));
                    return;
                }
                
                // Create new progress
                QuestProgress progress = new QuestProgress(playerId, questId);
                progress.setStatus(com.webx.quests.models.QuestStatus.IN_PROGRESS);
                
                dataManager.saveProgressAsync(progress).thenAccept(v -> {
                    ctx.json(Map.of("success", true, "progress", progress));
                }).exceptionally(ex -> {
                    ctx.status(500).json(Map.of("error", "Failed to start quest", "message", ex.getMessage()));
                    return null;
                });
            });
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(Map.of("error", "Invalid player ID"));
        }
    }

    // Complete quest
    private void completeQuest(Context ctx) {
        try {
            UUID playerId = UUID.fromString(ctx.pathParam("playerId"));
            String questId = ctx.pathParam("questId");
            
            dataManager.loadProgressAsync(playerId, questId).thenAccept(progress -> {
                if (progress == null) {
                    ctx.status(404).json(Map.of("error", "Progress not found"));
                    return;
                }
                
                progress.setStatus(com.webx.quests.models.QuestStatus.COMPLETED);
                
                CompletableFuture.allOf(
                    dataManager.saveProgressAsync(progress),
                    dataManager.incrementCompletedQuestsAsync(playerId),
                    dataManager.setLastQuestCompletionAsync(playerId, System.currentTimeMillis()),
                    dataManager.addCompletionHistoryAsync(playerId, questId)
                ).thenAccept(v -> {
                    ctx.json(Map.of("success", true, "progress", progress));
                }).exceptionally(ex -> {
                    ctx.status(500).json(Map.of("error", "Failed to complete quest", "message", ex.getMessage()));
                    return null;
                });
            });
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(Map.of("error", "Invalid player ID"));
        }
    }

    // Reset quest progress
    private void resetQuest(Context ctx) {
        try {
            UUID playerId = UUID.fromString(ctx.pathParam("playerId"));
            String questId = ctx.pathParam("questId");
            
            dataManager.deleteProgressAsync(playerId, questId).thenAccept(v -> {
                ctx.json(Map.of("success", true));
            }).exceptionally(ex -> {
                ctx.status(500).json(Map.of("error", "Failed to reset quest", "message", ex.getMessage()));
                return null;
            });
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(Map.of("error", "Invalid player ID"));
        }
    }

    // Get player statistics
    private void getPlayerStats(Context ctx) {
        try {
            UUID playerId = UUID.fromString(ctx.pathParam("playerId"));
            
            CompletableFuture.allOf(
                dataManager.getCompletedQuestsCountAsync(playerId),
                dataManager.getLastQuestCompletionAsync(playerId),
                dataManager.getCompletionHistoryAsync(playerId)
            ).thenAccept(v -> {
                ctx.json(Map.of(
                    "completedQuests", dataManager.getCompletedQuestsCountAsync(playerId).join(),
                    "lastCompletion", dataManager.getLastQuestCompletionAsync(playerId).join(),
                    "history", dataManager.getCompletionHistoryAsync(playerId).join()
                ));
            }).exceptionally(ex -> {
                ctx.status(500).json(Map.of("error", "Failed to load stats", "message", ex.getMessage()));
                return null;
            });
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(Map.of("error", "Invalid player ID"));
        }
    }

    // Get leaderboard (top players by completed quests)
    private void getLeaderboard(Context ctx) {
        // TODO: Implement leaderboard logic
        ctx.json(Map.of("message", "Leaderboard not yet implemented"));
    }

    // Reload quests
    private void reloadQuests(Context ctx) {
        dataManager.reloadAsync().thenAccept(v -> {
            ctx.json(Map.of("success", true, "message", "Quests reloaded"));
        }).exceptionally(ex -> {
            ctx.status(500).json(Map.of("error", "Failed to reload quests", "message", ex.getMessage()));
            return null;
        });
    }

    // Get available quest types
    private void getQuestTypes(Context ctx) {
        ctx.json(com.webx.quests.models.QuestType.values());
    }

    // Get available difficulties
    private void getDifficulties(Context ctx) {
        ctx.json(com.webx.quests.models.QuestDifficulty.values());
    }
}
