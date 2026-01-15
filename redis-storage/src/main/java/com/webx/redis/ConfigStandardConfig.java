package com.webx.redis;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.util.logging.Logger;

/**
 * Utility loader that reads a standard redisconfig.json (if present) and
 * returns a hydrated {@link RedisConfig} with sane defaults otherwise.
 */
public final class ConfigStandardConfig {
    private static final Gson GSON = new Gson();

    private ConfigStandardConfig() {
    }

    /**
     * Load RedisConfig from redisconfig.json located at the given root.
     * Falls back to the provided defaultConfig or a new RedisConfig if missing/invalid.
     */
    public static RedisConfig load(File serverRoot, RedisConfig defaultConfig, Logger logger) {
        Logger safeLogger = logger != null ? logger : Logger.getLogger(ConfigStandardConfig.class.getName());
        RedisConfig fallback = defaultConfig != null ? defaultConfig : new RedisConfig();

        if (serverRoot == null) {
            return fallback;
        }

        File cfgFile = new File(serverRoot, "redisconfig.json");
        if (!cfgFile.exists()) {
            return fallback;
        }

        try (FileReader reader = new FileReader(cfgFile)) {
            RedisConfig loaded = GSON.fromJson(reader, RedisConfig.class);
            return loaded != null ? loaded : fallback;
        } catch (Exception e) {
            safeLogger.warning("Failed to read redisconfig.json: " + e.getMessage());
            return fallback;
        }
    }

    /** Convenience overload: no logger provided. */
    public static RedisConfig load(File serverRoot) {
        return load(serverRoot, new RedisConfig(), null);
    }
}
