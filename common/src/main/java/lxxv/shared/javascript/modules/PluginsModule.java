package lxxv.shared.javascript.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import lxxv.shared.javascript.JavaScriptEngine;

/**
 * Plugins helper namespace backed by Bukkit/Paper PluginManager with YAML config helpers.
 */
public class PluginsModule {
    private final JavaScriptEngine engine;

    public PluginsModule(JavaScriptEngine engine) {
        this.engine = Objects.requireNonNull(engine, "engine");
    }

    public void register() {
        engine.registerFunction("pluginsList", args -> {
            List<Map<String, Object>> list = new ArrayList<>();
            for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                list.add(toInfo(plugin));
            }
            return list;
        });

        engine.registerFunction("pluginsInfo", args -> {
            String name = args.length > 0 && args[0] != null ? args[0].toString() : null;
            if (name == null) return Map.of();
            Plugin plugin = Bukkit.getPluginManager().getPlugin(name);
            return plugin != null ? toInfo(plugin) : Map.of();
        });

        engine.registerFunction("pluginConfigGet", args -> {
            String name = args.length > 0 && args[0] != null ? args[0].toString() : null;
            String path = args.length > 1 && args[1] != null ? args[1].toString() : null;
            Object defVal = args.length > 2 ? args[2] : null;
            if (name == null || path == null) return defVal;
            JavaPlugin jp = asJavaPlugin(name);
            if (jp == null) return defVal;
            FileConfiguration cfg = jp.getConfig();
            return cfg.contains(path) ? cfg.get(path) : defVal;
        });

        engine.registerFunction("pluginConfigSet", args -> {
            String name = args.length > 0 && args[0] != null ? args[0].toString() : null;
            String path = args.length > 1 && args[1] != null ? args[1].toString() : null;
            Object value = args.length > 2 ? args[2] : null;
            if (name == null || path == null) return false;
            JavaPlugin jp = asJavaPlugin(name);
            if (jp == null) return false;
            FileConfiguration cfg = jp.getConfig();
            cfg.set(path, value);
            jp.saveConfig();
            return true;
        });
    }

    private Map<String, Object> toInfo(Plugin plugin) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", plugin.getName());
        map.put("enabled", plugin.isEnabled());
        map.put("version", plugin.getDescription().getVersion());
        map.put("description", plugin.getDescription().getDescription());
        map.put("authors", plugin.getDescription().getAuthors());
        return map;
    }

    private JavaPlugin asJavaPlugin(String name) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(name);
        if (plugin instanceof JavaPlugin jp) {
            return jp;
        }
        return null;
    }
}
