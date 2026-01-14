package lxxv.shared.javascript.modules;

import java.lang.management.ManagementFactory;
import java.util.Objects;

import org.bukkit.Bukkit;

import lxxv.shared.javascript.JavaScriptEngine;

/**
 * Provides lightweight world/server info helpers to V8 via registered functions, backed by Bukkit APIs.
 */
public class WorldModule {
    private final JavaScriptEngine engine;

    public WorldModule(JavaScriptEngine engine) {
        this.engine = Objects.requireNonNull(engine, "engine");
    }

    public void register() {
        engine.registerFunction("worldMotd", args -> Bukkit.getMotd());

        engine.registerFunction("worldSetMotd", args -> {
            if (args.length == 0 || args[0] == null) return null;
            String motd = args[0].toString();
            Bukkit.getServer().setMotd(motd);
            return motd;
        });

        engine.registerFunction("worldUptime", args -> ManagementFactory.getRuntimeMXBean().getUptime());

        engine.registerFunction("worldMaxPlayers", args -> Bukkit.getMaxPlayers());

        engine.registerFunction("worldOnlineCount", args -> Bukkit.getOnlinePlayers().size());

        engine.registerFunction("worldBroadcast", args -> {
            String msg = args.length > 0 && args[0] != null ? args[0].toString() : "";
            Bukkit.broadcastMessage(msg);
            return null;
        });
    }
}
