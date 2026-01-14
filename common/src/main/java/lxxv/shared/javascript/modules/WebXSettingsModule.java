package lxxv.shared.javascript.modules;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import lxxv.shared.javascript.JavaScriptEngine;

/**
 * WebX settings helper namespace exposed via engine-registered functions.
 * Stores values in-memory and can be swapped to a persistent store later.
 */
public class WebXSettingsModule {
    private final JavaScriptEngine engine;
    private final Map<String, Object> settings = new ConcurrentHashMap<>();

    public WebXSettingsModule(JavaScriptEngine engine) {
        this.engine = Objects.requireNonNull(engine, "engine");
    }

    /**
     * Register functions directly into the JS runtime.
     * Exposes: webxSettingsGet(key, default?), webxSettingsSet(key, value), webxSettingsAll().
     */
    public void register() {
        engine.registerFunction("webxSettingsGet", args -> {
            String key = args.length > 0 && args[0] != null ? args[0].toString() : null;
            Object defVal = args.length > 1 ? args[1] : null;
            if (key == null || key.isBlank()) return defVal;
            return settings.getOrDefault(key, defVal);
        });

        engine.registerFunction("webxSettingsSet", args -> {
            String key = args.length > 0 && args[0] != null ? args[0].toString() : null;
            Object value = args.length > 1 ? args[1] : null;
            if (key == null || key.isBlank()) return null;
            settings.put(key, value);
            return value;
        });

        engine.registerFunction("webxSettingsAll", args -> {
            return Map.copyOf(settings);
        });
    }
}
