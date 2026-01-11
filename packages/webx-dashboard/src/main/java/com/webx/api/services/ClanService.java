package com.webx.api.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.http.Context;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * REST API service for Clans using reflection
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
        Object clanManager = getClanManager();
        if (clanManager == null) {
            ctx.status(503).json(Map.of("error", "Clans plugin not available"));
            return;
        }

        try {
            Method getAllClansMethod = clanManager.getClass().getMethod("getAllClans");
            Collection<?> clans = (Collection<?>) getAllClansMethod.invoke(clanManager);
            
            List<Map<String, Object>> clanData = new ArrayList<>();
            for (Object clan : clans) {
                clanData.add(clanToMap(clan));
            }

            ctx.json(Map.of(
                    "clans", clanData,
                    "total", clans.size()
            ));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Failed to get clans: " + e.getMessage()));
        }
    }

    /**
     * GET /api/clans/{name} - Get clan by name
     */
    public void getClan(Context ctx) {
        String clanName = ctx.pathParam("name");
        Object clanManager = getClanManager();
        
        if (clanManager == null) {
            ctx.status(503).json(Map.of("error", "Clans plugin not available"));
            return;
        }

        try {
            Method getClanMethod = clanManager.getClass().getMethod("getClan", String.class);
            Object clan = getClanMethod.invoke(clanManager, clanName);
            
            if (clan == null) {
                ctx.status(404).json(Map.of("error", "Clan not found"));
                return;
            }

            ctx.json(clanToMap(clan));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Failed to get clan: " + e.getMessage()));
        }
    }

    /**
     * GET /api/clans/player/{uuid} - Get clan by player UUID
     */
    public void getClanByPlayer(Context ctx) {
        String uuidStr = ctx.pathParam("uuid");
        Object clanManager = getClanManager();
        
        if (clanManager == null) {
            ctx.status(503).json(Map.of("error", "Clans plugin not available"));
            return;
        }

        try {
            UUID uuid = UUID.fromString(uuidStr);
            Method getClanByMemberMethod = clanManager.getClass().getMethod("getClanByMember", UUID.class);
            Object clan = getClanByMemberMethod.invoke(clanManager, uuid);
            
            if (clan == null) {
                ctx.status(404).json(Map.of("error", "Player not in any clan"));
                return;
            }

            ctx.json(clanToMap(clan));
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(Map.of("error", "Invalid UUID format"));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Failed to get clan: " + e.getMessage()));
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

        Object clanManager = getClanManager();
        if (clanManager == null) {
            ctx.status(503).json(Map.of("error", "Clans plugin not available"));
            return;
        }

        try {
            Method getAllClansMethod = clanManager.getClass().getMethod("getAllClans");
            Collection<?> clans = (Collection<?>) getAllClansMethod.invoke(clanManager);

            // Sort by power and limit
            List<?> topClans = clans.stream()
                    .sorted((c1, c2) -> {
                        try {
                            Method getPowerMethod = c1.getClass().getMethod("getPower");
                            double power1 = (double) getPowerMethod.invoke(c1);
                            double power2 = (double) getPowerMethod.invoke(c2);
                            return Double.compare(power2, power1); // Descending
                        } catch (Exception e) {
                            return 0;
                        }
                    })
                    .limit(limit)
                    .collect(Collectors.toList());

            List<Map<String, Object>> leaderboard = new ArrayList<>();
            int position = 1;
            for (Object clan : topClans) {
                Map<String, Object> entry = new HashMap<>(clanToMap(clan));
                entry.put("position", position++);
                leaderboard.add(entry);
            }

            ctx.json(Map.of(
                    "leaderboard", leaderboard,
                    "total", topClans.size()
            ));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Failed to get leaderboard: " + e.getMessage()));
        }
    }

    /**
     * Convert Clan object to Map using reflection
     */
    private Map<String, Object> clanToMap(Object clan) {
        Map<String, Object> data = new HashMap<>();
        
        try {
            Method getIdMethod = clan.getClass().getMethod("getId");
            Method getNameMethod = clan.getClass().getMethod("getName");
            Method getTagMethod = clan.getClass().getMethod("getTag");
            Method getLeaderMethod = clan.getClass().getMethod("getLeader");
            Method getMemberCountMethod = clan.getClass().getMethod("getMemberCount");
            Method getPowerMethod = clan.getClass().getMethod("getPower");
            Method getLevelMethod = clan.getClass().getMethod("getLevel");
            Method getExperienceMethod = clan.getClass().getMethod("getExperience");
            Method getDescriptionMethod = clan.getClass().getMethod("getDescription");
            Method getCreatedAtMethod = clan.getClass().getMethod("getCreatedAt");
            Method getMembersMethod = clan.getClass().getMethod("getMembers");
            Method getMemberRankMethod = clan.getClass().getMethod("getMemberRank", UUID.class);

            UUID leaderUuid = (UUID) getLeaderMethod.invoke(clan);
            OfflinePlayer leader = Bukkit.getOfflinePlayer(leaderUuid);
            
            data.put("id", getIdMethod.invoke(clan));
            data.put("name", getNameMethod.invoke(clan));
            data.put("tag", getTagMethod.invoke(clan));
            data.put("leader", Map.of(
                    "uuid", leaderUuid.toString(),
                    "name", leader.getName() != null ? leader.getName() : "Unknown"
            ));
            data.put("memberCount", getMemberCountMethod.invoke(clan));
            data.put("power", getPowerMethod.invoke(clan));
            data.put("level", getLevelMethod.invoke(clan));
            data.put("experience", getExperienceMethod.invoke(clan));
            data.put("description", getDescriptionMethod.invoke(clan));
            data.put("createdAt", getCreatedAtMethod.invoke(clan));

            // Add member list with details
            Collection<?> members = (Collection<?>) getMembersMethod.invoke(clan);
            List<Map<String, Object>> memberList = new ArrayList<>();
            for (Object memberUuid : members) {
                UUID uuid = (UUID) memberUuid;
                OfflinePlayer member = Bukkit.getOfflinePlayer(uuid);
                String rank = (String) getMemberRankMethod.invoke(clan, uuid);
                
                memberList.add(Map.of(
                        "uuid", uuid.toString(),
                        "name", member.getName() != null ? member.getName() : "Unknown",
                        "rank", rank != null ? rank : "MEMBER",
                        "online", member.isOnline()
                ));
            }
            data.put("members", memberList);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    /**
     * Get ClanManager from Clans plugin using reflection
     */
    private Object getClanManager() {
        Plugin clansPlugin = Bukkit.getPluginManager().getPlugin("Clans");
        if (clansPlugin == null || !clansPlugin.isEnabled()) {
            return null;
        }

        try {
            Class<?> clansClass = clansPlugin.getClass();
            Method method = clansClass.getMethod("getClanManager");
            return method.invoke(clansPlugin);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
