package lxxv.shared.javascript.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import lxxv.shared.javascript.JavaScriptEngine;

/**
 * Provides player helper functions directly registered into the JS runtime using Bukkit APIs.
 */
public class PlayerModule {
    private final JavaScriptEngine engine;

    public PlayerModule(JavaScriptEngine engine) {
        this.engine = Objects.requireNonNull(engine, "engine");
    }

    public void register() {
        engine.registerFunction("playerOnline", args -> listOnline());

        engine.registerFunction("playerCount", args -> Bukkit.getOnlinePlayers().size());

        engine.registerFunction("playerGet", args -> {
            String id = args.length > 0 && args[0] != null ? args[0].toString() : null;
            if (id == null) return null;
            Player p = findPlayer(id);
            return p != null ? toMap(p) : null;
        });

        engine.registerFunction("playerSend", args -> {
            if (args.length < 2 || args[0] == null || args[1] == null) return false;
            Player p = findPlayer(args[0].toString());
            if (p == null) return false;
            p.sendMessage(args[1].toString());
            return true;
        });

        engine.registerFunction("playerGive", args -> {
            if (args.length < 2 || args[0] == null || args[1] == null) return false;
            Player p = findPlayer(args[0].toString());
            if (p == null) return false;
            String materialName = args[1].toString();
            int amount = args.length > 2 && args[2] != null ? parseInt(args[2].toString(), 1) : 1;
            Material mat = Material.matchMaterial(materialName);
            if (mat == null) return false;
            p.getInventory().addItem(new ItemStack(mat, Math.max(1, amount)));
            return true;
        });

        engine.registerFunction("playerTeleport", args -> {
            if (args.length < 4 || args[0] == null) return false;
            Player p = findPlayer(args[0].toString());
            if (p == null) return false;
            double x = parseDouble(args[1], p.getLocation().getX());
            double y = parseDouble(args[2], p.getLocation().getY());
            double z = parseDouble(args[3], p.getLocation().getZ());
            Location dest = new Location(p.getWorld(), x, y, z, p.getLocation().getYaw(), p.getLocation().getPitch());
            return p.teleport(dest);
        });

        engine.registerFunction("playerHealth", args -> {
            Player p = args.length > 0 && args[0] != null ? findPlayer(args[0].toString()) : null;
            return p != null ? p.getHealth() : null;
        });

        engine.registerFunction("playerSetHealth", args -> {
            if (args.length < 2 || args[0] == null || args[1] == null) return false;
            Player p = findPlayer(args[0].toString());
            if (p == null) return false;
            double health = parseDouble(args[1], p.getHealth());
            p.setHealth(Math.max(0.0, Math.min(health, p.getMaxHealth())));
            return true;
        });

        engine.registerFunction("playerFood", args -> {
            Player p = args.length > 0 && args[0] != null ? findPlayer(args[0].toString()) : null;
            return p != null ? p.getFoodLevel() : null;
        });

        engine.registerFunction("playerSetFood", args -> {
            if (args.length < 2 || args[0] == null || args[1] == null) return false;
            Player p = findPlayer(args[0].toString());
            if (p == null) return false;
            int food = (int) Math.round(parseDouble(args[1], p.getFoodLevel()));
            p.setFoodLevel(Math.max(0, Math.min(food, 20)));
            return true;
        });
    }

    private List<Map<String, Object>> listOnline() {
        List<Map<String, Object>> out = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            out.add(toMap(p));
        }
        return out;
    }

    private Map<String, Object> toMap(Player p) {
        Map<String, Object> m = new HashMap<>();
        m.put("uuid", p.getUniqueId().toString());
        m.put("name", p.getName());
        m.put("health", p.getHealth());
        m.put("maxHealth", p.getMaxHealth());
        m.put("food", p.getFoodLevel());
        m.put("world", p.getWorld().getName());
        m.put("location", Map.of(
            "x", p.getLocation().getX(),
            "y", p.getLocation().getY(),
            "z", p.getLocation().getZ()
        ));
        return m;
    }

    private Player findPlayer(String idOrName) {
        try {
            UUID uuid = UUID.fromString(idOrName);
            Player byUuid = Bukkit.getPlayer(uuid);
            if (byUuid != null) return byUuid;
        } catch (IllegalArgumentException ignored) { }
        return Bukkit.getPlayer(idOrName);
    }

    private int parseInt(String s, int def) {
        try { return Integer.parseInt(s); } catch (NumberFormatException e) { return def; }
    }

    private double parseDouble(Object val, double def) {
        if (val == null) return def;
        try { return Double.parseDouble(val.toString()); } catch (NumberFormatException e) { return def; }
    }
}
