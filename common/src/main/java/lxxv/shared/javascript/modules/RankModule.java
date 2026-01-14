package lxxv.shared.javascript.modules;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import lxxv.shared.javascript.JavaScriptEngine;

/**
 * Rank helper namespace backed by Ranks plugin when available, with in-memory fallback.
 */
public class RankModule {
    private final JavaScriptEngine engine;
    private final Map<String, Object> fallbackRanks = new ConcurrentHashMap<>();

    public RankModule(JavaScriptEngine engine) {
        this.engine = Objects.requireNonNull(engine, "engine");
    }

    public void register() {
        // Get player primary rank
        engine.registerFunction("rankGet", args -> {
            String playerId = args.length > 0 && args[0] != null ? args[0].toString() : null;
            if (playerId == null) return null;
            String rank = fetchPrimaryRank(playerId);
            if (rank != null) return rank;
            return (String) fallbackRanks.get(playerId);
        });

        // Set player primary rank (no-op if plugin missing; updates fallback otherwise)
        engine.registerFunction("rankSet", args -> {
            String playerId = args.length > 0 && args[0] != null ? args[0].toString() : null;
            String rankId = args.length > 1 && args[1] != null ? args[1].toString() : null;
            String assignedBy = args.length > 2 && args[2] != null ? args[2].toString() : "js";
            String reason = args.length > 3 && args[3] != null ? args[3].toString() : "script";
            if (playerId == null || rankId == null) return false;
            if (setPrimaryRank(playerId, rankId, assignedBy, reason)) return true;
            fallbackRanks.put(playerId, rankId);
            return true;
        });

        // Check if player has rank (plugin aware)
        engine.registerFunction("rankHas", args -> {
            String playerId = args.length > 0 && args[0] != null ? args[0].toString() : null;
            String rankId = args.length > 1 && args[1] != null ? args[1].toString() : null;
            if (playerId == null || rankId == null) return false;
            Boolean result = hasRank(playerId, rankId);
            if (result != null) return result;
            return rankId.equals(fallbackRanks.get(playerId));
        });

        // List all ranks (id + basic meta) from plugin if present
        engine.registerFunction("ranksAll", args -> fetchAllRanks());
    }

    private boolean isRanksPluginAvailable() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("ranks");
        return plugin != null && plugin.isEnabled();
    }

    private String fetchPrimaryRank(String playerId) {
        try {
            Plugin plugin = Bukkit.getPluginManager().getPlugin("ranks");
            if (plugin == null || !plugin.isEnabled()) return null;

            Object prm = call(plugin, "getPlayerRankManager");
            if (prm == null) return null;

            UUID uuid = UUID.fromString(playerId);
            Object rank = call(prm, "getPlayerPrimaryRank", UUID.class, uuid);
            return rank != null ? rank.toString() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private boolean setPrimaryRank(String playerId, String rankId, String assignedBy, String reason) {
        try {
            Plugin plugin = Bukkit.getPluginManager().getPlugin("ranks");
            if (plugin == null || !plugin.isEnabled()) return false;

            Object prm = call(plugin, "getPlayerRankManager");
            if (prm == null) return false;

            UUID uuid = UUID.fromString(playerId);
            call(prm, "setPlayerPrimaryRank", UUID.class, uuid, String.class, rankId, String.class, assignedBy, String.class, reason);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Boolean hasRank(String playerId, String rankId) {
        try {
            Plugin plugin = Bukkit.getPluginManager().getPlugin("ranks");
            if (plugin == null || !plugin.isEnabled()) return null;

            Object prm = call(plugin, "getPlayerRankManager");
            if (prm == null) return null;

            UUID uuid = UUID.fromString(playerId);
            Object res = call(prm, "hasRank", UUID.class, uuid, String.class, rankId);
            return res instanceof Boolean b ? b : null;
        } catch (Exception e) {
            return null;
        }
    }

    private List<Map<String, Object>> fetchAllRanks() {
        try {
            Plugin plugin = Bukkit.getPluginManager().getPlugin("ranks");
            if (plugin == null || !plugin.isEnabled()) return List.of();

            Object rm = call(plugin, "getRankManager");
            if (rm == null) return List.of();

            Object mapObj = call(rm, "getAllRanks");
            if (!(mapObj instanceof Map<?, ?> rankMap)) return List.of();

            List<Map<String, Object>> out = new ArrayList<>();
            for (Object val : rankMap.values()) {
                Map<String, Object> row = new HashMap<>();
                row.put("id", call(val, "getId"));
                row.put("name", call(val, "getDisplayName"));
                row.put("priority", call(val, "getPriority"));
                row.put("prefix", call(val, "getPrefix"));
                row.put("tab", call(val, "getTabListFormat"));
                row.put("purchasable", call(val, "isPurchasable"));
                row.put("price", call(val, "getPurchasePrice"));
                Object features = call(val, "getFeatures");
                if (features != null) row.put("features", features);
                out.add(row);
            }
            return out;
        } catch (Exception e) {
            return List.of();
        }
    }

    private Object call(Object target, String method) throws Exception {
        Method m = target.getClass().getMethod(method);
        return m.invoke(target);
    }

    private Object call(Object target, String method, Class<?> p1, Object a1) throws Exception {
        Method m = target.getClass().getMethod(method, p1);
        return m.invoke(target, a1);
    }

    private Object call(Object target, String method, Class<?> p1, Object a1, Class<?> p2, Object a2) throws Exception {
        Method m = target.getClass().getMethod(method, p1, p2);
        return m.invoke(target, a1, a2);
    }

    private Object call(Object target, String method, Class<?> p1, Object a1, Class<?> p2, Object a2, Class<?> p3, Object a3, Class<?> p4, Object a4) throws Exception {
        Method m = target.getClass().getMethod(method, p1, p2, p3, p4);
        return m.invoke(target, a1, a2, a3, a4);
    }
}
