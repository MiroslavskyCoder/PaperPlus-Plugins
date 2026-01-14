package lxxv.shared.javascript.modules;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import lxxv.shared.javascript.JavaScriptEngine;

/**
 * Clans helper namespace backed by the Clans plugin when available, with an in-memory fallback.
 */
public class ClansModule {
    private final JavaScriptEngine engine;
    private final Map<String, Object> fallbackClans = new ConcurrentHashMap<>();

    public ClansModule(JavaScriptEngine engine) {
        this.engine = Objects.requireNonNull(engine, "engine");
    }

    public void register() {
        // Get clan for player UUID (prefers real plugin, falls back to local map)
        engine.registerFunction("clanGet", args -> {
            String playerId = args.length > 0 && args[0] != null ? args[0].toString() : null;
            if (playerId == null) return null;
            Map<String, Object> clan = fetchClanByPlayer(playerId);
            if (clan != null) return clan;
            return fallbackClans.get(playerId);
        });

        // Set fallback clan mapping only (no-op when real plugin is available)
        engine.registerFunction("clanSet", args -> {
            String playerId = args.length > 0 && args[0] != null ? args[0].toString() : null;
            Object clan = args.length > 1 ? args[1] : null;
            if (playerId == null) return null;
            if (isClansPluginAvailable()) {
                // Avoid mutating real clan data implicitly; expose explicit APIs instead.
                return null;
            }
            fallbackClans.put(playerId, clan);
            return clan;
        });

        // List all clans (real plugin if available, otherwise fallback snapshot)
        engine.registerFunction("clanAll", args -> {
            List<Map<String, Object>> clans = fetchAllClans();
            if (!clans.isEmpty()) return clans;
            return new ArrayList<>(fallbackClans.values());
        });

        // Get clan by name (real plugin only)
        engine.registerFunction("clanByName", args -> {
            String name = args.length > 0 && args[0] != null ? args[0].toString() : null;
            if (name == null) return null;
            return fetchClanByName(name);
        });

        // Get clan members with ranks (real plugin only)
        engine.registerFunction("clanMembers", args -> {
            String name = args.length > 0 && args[0] != null ? args[0].toString() : null;
            if (name == null) return List.of();
            return fetchMembers(name);
        });
    }

    private boolean isClansPluginAvailable() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("clans");
        return plugin != null && plugin.isEnabled();
    }

    private Map<String, Object> fetchClanByPlayer(String playerId) {
        try {
            Plugin plugin = Bukkit.getPluginManager().getPlugin("clans");
            if (plugin == null || !plugin.isEnabled()) return null;

            Object clanManager = call(plugin, "getClanManager");
            if (clanManager == null) return null;

            UUID uuid = UUID.fromString(playerId);
            Object clan = call(clanManager, "getClanByMember", UUID.class, uuid);
            return toClanMap(clan);
        } catch (Exception e) {
            return null;
        }
    }

    private Map<String, Object> fetchClanByName(String name) {
        try {
            Plugin plugin = Bukkit.getPluginManager().getPlugin("clans");
            if (plugin == null || !plugin.isEnabled()) return null;

            Object clanManager = call(plugin, "getClanManager");
            if (clanManager == null) return null;

            Object clan = call(clanManager, "getClan", String.class, name);
            return toClanMap(clan);
        } catch (Exception e) {
            return null;
        }
    }

    private List<Map<String, Object>> fetchAllClans() {
        try {
            Plugin plugin = Bukkit.getPluginManager().getPlugin("clans");
            if (plugin == null || !plugin.isEnabled()) return List.of();

            Object clanManager = call(plugin, "getClanManager");
            if (clanManager == null) return List.of();

            Object result = call(clanManager, "getAllClans");
            if (!(result instanceof Collection<?> collection)) return List.of();

            List<Map<String, Object>> mapped = new ArrayList<>();
            for (Object clan : collection) {
                Map<String, Object> map = toClanMap(clan);
                if (map != null) mapped.add(map);
            }
            return mapped;
        } catch (Exception e) {
            return List.of();
        }
    }

    private List<Map<String, Object>> fetchMembers(String clanName) {
        try {
            Plugin plugin = Bukkit.getPluginManager().getPlugin("clans");
            if (plugin == null || !plugin.isEnabled()) return List.of();

            Object clanManager = call(plugin, "getClanManager");
            if (clanManager == null) return List.of();

            Object clan = call(clanManager, "getClan", String.class, clanName);
            if (clan == null) return List.of();

            Object membersObj = call(clan, "getMembers");
            if (!(membersObj instanceof Collection<?> members)) return List.of();

            List<Map<String, Object>> output = new ArrayList<>();
            for (Object member : members) {
                if (!(member instanceof UUID uuid)) continue;
                Map<String, Object> row = new HashMap<>();
                row.put("uuid", uuid.toString());
                Object rank = call(clan, "getMemberRank", UUID.class, uuid);
                row.put("rank", rank != null ? rank.toString() : null);
                row.put("leader", Boolean.TRUE.equals(callBoolean(clan, "isLeader", UUID.class, uuid)));
                row.put("deputy", Boolean.TRUE.equals(callBoolean(clan, "isDeputy", UUID.class, uuid)));
                output.add(row);
            }
            return output;
        } catch (Exception e) {
            return List.of();
        }
    }

    private Map<String, Object> toClanMap(Object clan) {
        if (clan == null) return null;
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("name", call(clan, "getName"));
            map.put("tag", call(clan, "getTag"));

            Object leader = call(clan, "getLeader");
            map.put("leader", leader != null ? leader.toString() : null);

            Object deputies = call(clan, "getDeputies");
            if (deputies instanceof Collection<?> depColl) {
                Set<String> deps = new HashSet<>();
                for (Object d : depColl) deps.add(String.valueOf(d));
                map.put("deputies", deps);
            }

            Object members = call(clan, "getMembers");
            if (members instanceof Collection<?> mems) {
                Set<String> set = new HashSet<>();
                for (Object m : mems) set.add(String.valueOf(m));
                map.put("members", set);
            }

            map.put("power", call(clan, "getPower"));
            map.put("level", call(clan, "getLevel"));
            map.put("description", call(clan, "getDescription"));
            map.put("createdAt", call(clan, "getCreatedAt"));
            return map;
        } catch (Exception e) {
            return null;
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

    private Boolean callBoolean(Object target, String method, Class<?> p1, Object a1) throws Exception {
        Object result = call(target, method, p1, a1);
        return result instanceof Boolean b ? b : null;
    }
}
