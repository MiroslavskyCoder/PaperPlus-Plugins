package com.webx.api.endpoints;

import com.webx.api.services.RankService;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;
import java.util.Map;

/**
 * REST API endpoints for Ranks management
 * Base: /api/v1/ranks
 */
public class RanksEndpoints {
    private final RankService rankService;

    public RanksEndpoints(RankService rankService) {
        this.rankService = rankService;
    }

    public void register(Javalin app) {
        // Rank management
        app.get("/api/v1/ranks", this::getAllRanks);
        app.get("/api/v1/ranks/{rankId}", this::getRank);
        app.put("/api/v1/ranks/{rankId}", this::updateRank);
        app.delete("/api/v1/ranks/{rankId}", this::deleteRank);

        // Player rank management
        app.get("/api/v1/ranks/players/{playerUUID}", this::getPlayerRank);
        app.post("/api/v1/ranks/players/{playerUUID}/assign", this::assignPlayerRank);
        app.post("/api/v1/ranks/players/{playerUUID}/remove", this::removePlayerRank);

        // Statistics
        app.get("/api/v1/ranks/statistics/distribution", this::getRankDistribution);
    }

    // ==================== Rank Endpoints ====================

    private void getAllRanks(Context ctx) {
        if (!rankService.isAvailable()) {
            ctx.status(503).json(createError("Ranks plugin is not available"));
            return;
        }

        List<Map<String, Object>> ranks = rankService.getAllRanks();
        ctx.json(createSuccess("Ranks retrieved successfully", ranks));
    }

    private void getRank(Context ctx) {
        if (!rankService.isAvailable()) {
            ctx.status(503).json(createError("Ranks plugin is not available"));
            return;
        }

        String rankId = ctx.pathParam("rankId");
        Map<String, Object> rank = rankService.getRank(rankId);

        if (rank == null || rank.isEmpty()) {
            ctx.status(404).json(createError("Rank not found: " + rankId));
            return;
        }

        ctx.json(createSuccess("Rank retrieved successfully", rank));
    }

    private void updateRank(Context ctx) {
        if (!rankService.isAvailable()) {
            ctx.status(503).json(createError("Ranks plugin is not available"));
            return;
        }

        String rankId = ctx.pathParam("rankId");
        JsonObject updateData = ctx.bodyAsClass(JsonObject.class);

        if (rankService.updateRank(rankId, updateData)) {
            Map<String, Object> updatedRank = rankService.getRank(rankId);
            ctx.json(createSuccess("Rank updated successfully", updatedRank));
        } else {
            ctx.status(400).json(createError("Failed to update rank"));
        }
    }

    private void deleteRank(Context ctx) {
        if (!rankService.isAvailable()) {
            ctx.status(503).json(createError("Ranks plugin is not available"));
            return;
        }

        String rankId = ctx.pathParam("rankId");

        if (rankService.deleteRank(rankId)) {
            ctx.json(createSuccess("Rank deleted successfully"));
        } else {
            ctx.status(400).json(createError("Cannot delete rank: " + rankId));
        }
    }

    // ==================== Player Rank Endpoints ====================

    private void getPlayerRank(Context ctx) {
        if (!rankService.isAvailable()) {
            ctx.status(503).json(createError("Ranks plugin is not available"));
            return;
        }

        String playerUUID = ctx.pathParam("playerUUID");
        Map<String, Object> playerRank = rankService.getPlayerRank(playerUUID);

        if (playerRank == null || playerRank.isEmpty()) {
            ctx.status(404).json(createError("Player rank not found: " + playerUUID));
            return;
        }

        ctx.json(createSuccess("Player rank retrieved successfully", playerRank));
    }

    private void assignPlayerRank(Context ctx) {
        if (!rankService.isAvailable()) {
            ctx.status(503).json(createError("Ranks plugin is not available"));
            return;
        }

        String playerUUID = ctx.pathParam("playerUUID");
        JsonObject requestBody = ctx.bodyAsClass(JsonObject.class);

        if (!requestBody.has("rankId")) {
            ctx.status(400).json(createError("Missing required field: rankId"));
            return;
        }

        String rankId = requestBody.get("rankId").getAsString();
        String assignedBy = requestBody.has("assignedBy") ? 
                requestBody.get("assignedBy").getAsString() : "SYSTEM";
        String reason = requestBody.has("reason") ? 
                requestBody.get("reason").getAsString() : "Admin assigned";

        if (rankService.setPlayerRank(playerUUID, rankId, assignedBy, reason)) {
            Map<String, Object> updatedRank = rankService.getPlayerRank(playerUUID);
            ctx.json(createSuccess("Rank assigned successfully", updatedRank));
        } else {
            ctx.status(400).json(createError("Failed to assign rank"));
        }
    }

    private void removePlayerRank(Context ctx) {
        if (!rankService.isAvailable()) {
            ctx.status(503).json(createError("Ranks plugin is not available"));
            return;
        }

        String playerUUID = ctx.pathParam("playerUUID");

        if (rankService.removePlayerRank(playerUUID)) {
            ctx.json(createSuccess("Rank removed successfully"));
        } else {
            ctx.status(400).json(createError("Failed to remove rank"));
        }
    }

    // ==================== Statistics Endpoints ====================

    private void getRankDistribution(Context ctx) {
        if (!rankService.isAvailable()) {
            ctx.status(503).json(createError("Ranks plugin is not available"));
            return;
        }

        Map<String, Integer> distribution = rankService.getRankDistribution();
        ctx.json(createSuccess("Rank distribution retrieved successfully", distribution));
    }

    // ==================== Helper Methods ====================

    private Map<String, Object> createSuccess(String message) {
        return Map.of(
                "success", true,
                "message", message
        );
    }

    private Map<String, Object> createSuccess(String message, Object data) {
        return Map.of(
                "success", true,
                "message", message,
                "data", data
        );
    }

    private Map<String, Object> createError(String message) {
        return Map.of(
                "success", false,
                "error", message
        );
    }
}
