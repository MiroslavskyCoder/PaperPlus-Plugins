package lxxv.shared.javascript.modules;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.bukkit.Bukkit;

import lxxv.shared.javascript.JavaScriptEngine;

/**
 * Utility helpers exposed to JS runtime.
 */
public class UtilModule {
    private final JavaScriptEngine engine;

    public UtilModule(JavaScriptEngine engine) {
        this.engine = Objects.requireNonNull(engine, "engine");
    }

    public void register() {
        engine.registerFunction("log", args -> {
            Bukkit.getLogger().info("[JS] " + join(args));
            return null;
        });

        engine.registerFunction("warn", args -> {
            Bukkit.getLogger().warning("[JS] " + join(args));
            return null;
        });

        engine.registerFunction("error", args -> {
            Bukkit.getLogger().severe("[JS] " + join(args));
            return null;
        });

        engine.registerFunction("now", args -> System.currentTimeMillis());

        engine.registerFunction("getMemoryInfo", args -> memory());
    }

    private Map<String, Object> memory() {
        Runtime runtime = Runtime.getRuntime();
        Map<String, Object> memory = new HashMap<>();
        memory.put("used", (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024);
        memory.put("free", runtime.freeMemory() / 1024 / 1024);
        memory.put("total", runtime.totalMemory() / 1024 / 1024);
        memory.put("max", runtime.maxMemory() / 1024 / 1024);
        return memory;
    }

    private String join(Object[] args) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            if (i > 0) sb.append(' ');
            sb.append(args[i]);
        }
        return sb.toString();
    }
}
