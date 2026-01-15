package lxxv.shared.server;

import java.util.Objects;

import lxxv.shared.javascript.JavaScriptEngine;
import lxxv.shared.javascript.JavaScriptException;
import lxxv.shared.javascript.advanced.JavaScriptEventSystem;
import lxxv.shared.javascript.advanced.JavaScriptScheduler; 

/**
 * Minimal JavaScript bridge entry point. Legacy registerFunctionLambda API removed.
 */
public class LXXVServer {
    private static org.bukkit.Server server;
    private static JavaScriptEngine jsEngine;
    private static JavaScriptEventSystem eventSystem;
    private static JavaScriptScheduler scheduler; 
 
    /**
     * Initialize the bridge with server and JS engine instances.
     */
    public static void initialize(org.bukkit.Server serverInstance, JavaScriptEngine engine) throws JavaScriptException {
        server = Objects.requireNonNull(serverInstance, "server");
        jsEngine = Objects.requireNonNull(engine, "engine");
        eventSystem = new JavaScriptEventSystem();
        scheduler = new JavaScriptScheduler(); 
    }
 
    private static void registerNativeModule(String name, Runnable registrar) {
        try {
            jsEngine.beginModuleRegistration(name);
            registrar.run();
            server.getLogger().info("Native module registered: " + name);
        } catch (Exception e) {
            server.getLogger().warning("Native module init failed for " + name + ": " + e.getMessage());
        } finally {
            jsEngine.endModuleRegistration();
        }
    }

    // ===== GETTERS =====

    public static org.bukkit.Server getServer() { return server; }
    public static JavaScriptEngine getJsEngine() { return jsEngine; }
    public static JavaScriptEventSystem getEventSystem() { return eventSystem; } 
    public static JavaScriptScheduler getScheduler() { return scheduler; }

}
