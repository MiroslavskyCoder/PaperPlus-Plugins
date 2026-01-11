package com.webx.api.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.webx.clans.managers.ClanManager;
import com.webx.clans.models.Clan;
import io.javalin.http.Context;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.stream.Collectors;

/**
 * REST API service for Clans
 */
public class ClanService {
    private final Gson gson;

    public ClanService() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    /**
     * GET /api/clans - List all clans
     */
    public void getAllClans(Context ctx) {
        ClanManager clanManager = getClanManager();
        if (clanManager == null) {
            ctx.status(503).json(Map.of("error", "Clans plugin not available"));
            return;
        }

        Collection<Clan> clans = clanManager.getAllClans();
        List<Map<String, Object>> clanData = clans.stream()
                .map(this::clanToMap)
                .collect(Collectors.toList());

        ctx.json(Map.of(
                "clans", clanData,
                "total", clans.size()
        ));
    }

    /**
     * GET /api/clans/{name} - Get clan by name
     */
    public void getClan(Context ctx) {
        String clanName = ctx.pathParam("name");
        ClanManager clanManager = getClanManager();
        
        if (clanManager == null) {
            ctx.status(503).json(Map.of("error", "Clans plugin not available"));
            return;
        }

        Clan clan = clanManager.getClan(clanName);
        if (clan == null) {
            ctx.status(404).json(Map.of("error", "Clan not found"));
            return;
        }

        ctx.json(clanToMap(clan));
    }

    /**
     * GET /api/clans/player/{uuid} - Get clan by player UUID
     */
    public void getClanByPlayer(Context ctx) {
        String uuidStr = ctx.pathParam("uuid");
        ClanManager clanManager = getClanManager();
        
        if (clanManager == null) {
            ctx.status(503).json(Map.of("error", "Clans plugin not available"));
            return;
        }

        try {
            UUID uuid = UUID.fromString(uuidStr);
            Clan clan = clanManager.getClanByMember(uuid);
            
            if (clan == null) {
                ctx.status(404).json(Map.of("error", "Player not in any clan"));
                return;
            }

            ctx.json(clanToMap(clan));
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(Map.of("error", "Invalid UUID format"));
        }
    }

    /**
     * GET /api/leaderboards/clans - Get top clans by power
     */
    public void getTopClans(Context ctx) {
        int limit = ctx.queryParamAsClass("limit", Integer.class).getOrDefault(10);
        if (limit < 1 || limit > 100) {
            limit = 10;
        }

        ClanManager clanManager = getClanManager();
        if (clanManager == null) {
            ctx.status(503).json(Map.of("error", "Clans plugin not available"));
            return;
        }

        List<Clan> topClans = clanManager.getAllClans().stream()
                .sorted(Comparator.comparingDouble(Clan::getPower).reversed())
                .limit(limit)
                .toList();

        List<Map<String, Object>> leaderboard = new ArrayList<>();
        int position = 1;
        for (Clan clan : topClans) {
            Map<String, Object> entry = new HashMap<>(clanToMap(clan));
            entry.put("position", position++);
            leaderboard.add(entry);
        }

        ctx.json(Map.of(
                "leaderboard", leaderboard,
                "total", topClans.size()
        ));
    }

    /**
     * Convert Clan to Map for JSON serialization
     */
    private Map<String, Object> clanToMap(Clan clan) {
        OfflinePlayer leader = Bukkit.getOfflinePlayer(clan.getLeader());
        
        Map<String, Object> data = new HashMap<>();
        data.put("id", clan.getId());
        data.put("name", clan.getName());
        data.put("tag", clan.getTag());
        data.put("leader", Map.of(
                "uuid", clan.getLeader().toString(),
                "name", leader.getName() != null ? leader.getName() : "Unknown"
        ));
        data.put("memberCount", clan.getMemberCount());
        data.put("power", clan.getPower());
        data.put("level", clan.getLevel());
        data.put("experience", clan.getExperience());
        data.put("description", clan.getDescription());
        data.put("createdAt", clan.getCreatedAt());

        // Add member list with details
        List<Map<String, Object>> members = new ArrayList<>();
        for (UUID memberUuid : clan.getMembers()) {
            OfflinePlayer member = Bukkit.getOfflinePlayer(memberUuid);
            members.add(Map.of(
                    "uuid", memberUuid.toString(),
                    "name", member.getName() != null ? member.getName() : "Unknown",
                    "rank", clan.getMemberRank(memberUuid),
                    "online", member.isOnline()
            ));
        }
        data.put("members", members);

        return data;
    }

    /**
     * Get ClanManager from Clans plugin
     */
    private ClanManager getClanManager() {
        Plugin clansPlugin = Bukkit.getPluginManager().getPlugin("Clans");
        if (clansPlugin == null || !clansPlugin.isEnabled()) {
            return null;
        }

        try {
            Class<?> clansClass = clansPlugin.getClass();
            java.lang.reflect.Method method = clansClass.getMethod("getClanManager");
            return (ClanManager) method.invoke(clansPlugin);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
