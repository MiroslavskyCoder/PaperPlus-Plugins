package lxxv.shared.javascript.modules;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;

import lxxv.shared.javascript.JavaScriptEngine;

/**
 * Exposes console helpers and broadcast helpers via registered functions using Bukkit APIs.
 */
public class ConsoleModule {
    private static final Logger LOGGER = Logger.getLogger(ConsoleModule.class.getName());
    private final JavaScriptEngine engine;

    public ConsoleModule(JavaScriptEngine engine) {
        this.engine = Objects.requireNonNull(engine, "engine");
    }

    public void register() { 
        // Aliases with dot notation for compatibility
        engine.registerFunction("log", args -> {
            LOGGER.info("[LXXV Engine]: " + join(args));
            return null;
        });

        engine.registerFunction("warn", args -> {
            LOGGER.warning("[LXXV Engine]: " + join(args));
            return null;
        });

        engine.registerFunction("error", args -> {
            LOGGER.log(Level.SEVERE, "[LXXV Engine]: " + join(args));
            return null;
        });

        engine.registerFunction("broadcast", args -> {
            String msg = args.length > 0 && args[0] != null ? args[0].toString() : "";
            Bukkit.broadcastMessage(msg);
            return null;
        });

        engine.registerFunction("exec", args -> {
            if (args.length == 0 || args[0] == null) return false;
            return Bukkit.dispatchCommand(Bukkit.getConsoleSender(), args[0].toString());
        });
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
