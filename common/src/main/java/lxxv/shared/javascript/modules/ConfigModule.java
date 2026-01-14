package lxxv.shared.javascript.modules;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import lxxv.shared.javascript.JavaScriptEngine;

/**
 * Config helper: in-memory KV + plugin YAML editor (get/set/list/save/reload) via Bukkit configs.
 */
public class ConfigModule {
    private final JavaScriptEngine engine;
    private final Map<String, Object> kv = new ConcurrentHashMap<>();

    public ConfigModule(JavaScriptEngine engine) {
        this.engine = Objects.requireNonNull(engine, "engine");
    }

    public void register() {
        // In-memory KV helpers
        engine.registerFunction("configGet", args -> {
            String key = str(args, 0);
            Object defVal = args.length > 1 ? args[1] : null;
            if (key == null) return defVal;
            return kv.getOrDefault(key, defVal);
        });

        engine.registerFunction("configSet", args -> {
            String key = str(args, 0);
            Object val = args.length > 1 ? args[1] : null;
            if (key == null) return null;
            kv.put(key, val);
            return val;
        });

        engine.registerFunction("configHas", args -> {
            String key = str(args, 0);
            if (key == null) return false;
            return kv.containsKey(key);
        });

        engine.registerFunction("configRemove", args -> {
            String key = str(args, 0);
            if (key == null) return null;
            return kv.remove(key);
        });

        engine.registerFunction("configClear", args -> {
            kv.clear();
            return null;
        });

        // Plugin YAML helpers
        engine.registerFunction("pluginConfigGet", args -> {
            String pluginName = str(args, 0);
            String path = str(args, 1);
            Object defVal = args.length > 2 ? args[2] : null;
            FileConfiguration cfg = cfg(pluginName);
            if (cfg == null || path == null) return defVal;
            return cfg.contains(path) ? cfg.get(path) : defVal;
        });

        engine.registerFunction("pluginConfigSet", args -> {
            String pluginName = str(args, 0);
            String path = str(args, 1);
            Object value = args.length > 2 ? args[2] : null;
            FileConfiguration cfg = cfg(pluginName);
            if (cfg == null || path == null) return false;
            cfg.set(path, value);
            save(pluginName, cfg);
            return true;
        });

        engine.registerFunction("pluginConfigList", args -> {
            String pluginName = str(args, 0);
            String path = str(args, 1);
            FileConfiguration cfg = cfg(pluginName);
            if (cfg == null) return Map.of();
            ConfigurationSection section = path == null ? cfg : cfg.getConfigurationSection(path);
            if (section == null) return Map.of();
            return section.getValues(true);
        });

        engine.registerFunction("pluginConfigReload", args -> {
            String pluginName = str(args, 0);
            JavaPlugin jp = asJavaPlugin(pluginName);
            if (jp == null) return false;
            jp.reloadConfig();
            return true;
        });

        engine.registerFunction("pluginConfigSave", args -> {
            String pluginName = str(args, 0);
            JavaPlugin jp = asJavaPlugin(pluginName);
            if (jp == null) return false;
            jp.saveConfig();
            return true;
        });
    }

    private String str(Object[] args, int idx) {
        return args.length > idx && args[idx] != null ? args[idx].toString() : null;
    }

    private FileConfiguration cfg(String pluginName) {
        JavaPlugin jp = asJavaPlugin(pluginName);
        return jp != null ? jp.getConfig() : null;
    }

    private void save(String pluginName, FileConfiguration cfg) {
        JavaPlugin jp = asJavaPlugin(pluginName);
        if (jp != null) {
            jp.saveConfig();
        }
    }

    private JavaPlugin asJavaPlugin(String name) {
        if (name == null) return null;
        Plugin plugin = Bukkit.getPluginManager().getPlugin(name);
        if (plugin instanceof JavaPlugin jp) {
            return jp;
        }
        return null;
    }
}
