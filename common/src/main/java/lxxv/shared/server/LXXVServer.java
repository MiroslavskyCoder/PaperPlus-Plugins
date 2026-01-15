package lxxv.shared.server;

import java.util.Objects;

import lxxv.shared.javascript.JavaScriptEngine;
import lxxv.shared.javascript.JavaScriptException;
import lxxv.shared.javascript.advanced.JavaScriptEventSystem;
import lxxv.shared.javascript.advanced.JavaScriptScheduler;
import lxxv.shared.javascript.modules.EventModule;

/**
 * Minimal JavaScript bridge entry point. Legacy registerFunctionLambda API removed.
 */
public class LXXVServer {
    private static org.bukkit.Server server;
    private static JavaScriptEngine jsEngine;
    private static JavaScriptEventSystem eventSystem;
    private static JavaScriptScheduler scheduler;
    private static EventModule eventModule;

    private static lxxv.shared.javascript.modules.PlayerModule playerModule;
    private static lxxv.shared.javascript.modules.MathModule mathModule;
    private static lxxv.shared.javascript.modules.ConsoleModule consoleModule;
    private static lxxv.shared.javascript.modules.WorldModule worldModule;
    private static lxxv.shared.javascript.modules.RankModule rankModule;
    private static lxxv.shared.javascript.modules.ClansModule clansModule;
    private static lxxv.shared.javascript.modules.ConfigModule configModule;
    private static lxxv.shared.javascript.modules.PluginsModule pluginsModule;
    private static lxxv.shared.javascript.modules.WebXSettingsModule webXSettingsModule;
    private static lxxv.shared.javascript.modules.UtilModule utilModule;
    private static lxxv.shared.javascript.modules.FsModule fsModule;

    /**
     * Initialize the bridge with server and JS engine instances.
     */
    public static void initialize(org.bukkit.Server serverInstance, JavaScriptEngine engine) throws JavaScriptException {
        server = Objects.requireNonNull(serverInstance, "server");
        jsEngine = Objects.requireNonNull(engine, "engine");
        eventSystem = new JavaScriptEventSystem();
        scheduler = new JavaScriptScheduler(server);
        eventModule = new EventModule(jsEngine, eventSystem);
        eventModule.register();
        registerJavaScriptModules();
    }

    // ===== MODULE REGISTRATION =====

    private static void registerJavaScriptModules() {
        playerModule = new lxxv.shared.javascript.modules.PlayerModule(jsEngine);
        mathModule = new lxxv.shared.javascript.modules.MathModule(jsEngine);
        consoleModule = new lxxv.shared.javascript.modules.ConsoleModule(jsEngine);
        worldModule = new lxxv.shared.javascript.modules.WorldModule(jsEngine);
        rankModule = new lxxv.shared.javascript.modules.RankModule(jsEngine);
        clansModule = new lxxv.shared.javascript.modules.ClansModule(jsEngine);
        configModule = new lxxv.shared.javascript.modules.ConfigModule(jsEngine);
        pluginsModule = new lxxv.shared.javascript.modules.PluginsModule(jsEngine);
        webXSettingsModule = new lxxv.shared.javascript.modules.WebXSettingsModule(jsEngine);
        utilModule = new lxxv.shared.javascript.modules.UtilModule(jsEngine);
        fsModule = new lxxv.shared.javascript.modules.FsModule(jsEngine);

        registerNativeModule("player", playerModule::register);
        registerNativeModule("math", mathModule::register);
        registerNativeModule("console", consoleModule::register);
        registerNativeModule("world", worldModule::register);
        registerNativeModule("rank", rankModule::register);
        registerNativeModule("clans", clansModule::register);
        registerNativeModule("config", configModule::register);
        registerNativeModule("plugins", pluginsModule::register);
        registerNativeModule("webxSettings", webXSettingsModule::register);
        registerNativeModule("util", utilModule::register);
        registerNativeModule("fs", fsModule::register);
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
    public static EventModule getEventModule() { return eventModule; }
    public static JavaScriptScheduler getScheduler() { return scheduler; }

    public static lxxv.shared.javascript.modules.PlayerModule getPlayerModule() { return playerModule; }
    public static lxxv.shared.javascript.modules.MathModule getMathModule() { return mathModule; }
    public static lxxv.shared.javascript.modules.ConsoleModule getConsoleModule() { return consoleModule; }
    public static lxxv.shared.javascript.modules.WorldModule getWorldModule() { return worldModule; }
    public static lxxv.shared.javascript.modules.RankModule getRankModule() { return rankModule; }
    public static lxxv.shared.javascript.modules.ClansModule getClansModule() { return clansModule; }
    public static lxxv.shared.javascript.modules.ConfigModule getConfigModule() { return configModule; }
    public static lxxv.shared.javascript.modules.PluginsModule getPluginsModule() { return pluginsModule; }
    public static lxxv.shared.javascript.modules.WebXSettingsModule getWebXSettingsModule() { return webXSettingsModule; }
    public static lxxv.shared.javascript.modules.UtilModule getUtilModule() { return utilModule; }
    public static lxxv.shared.javascript.modules.FsModule getFsModule() { return fsModule; }
}
