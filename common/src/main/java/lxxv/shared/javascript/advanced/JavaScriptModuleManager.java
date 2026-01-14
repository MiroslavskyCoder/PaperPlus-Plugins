package lxxv.shared.javascript.advanced;

import lxxv.shared.javascript.JavaScriptEngine;
import lxxv.shared.javascript.JavaScriptException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JavaScript Module Manager
 * Manages JavaScript modules with dependency resolution
 */
public class JavaScriptModuleManager {
    private final JavaScriptEngine engine;
    private final Map<String, ModuleInfo> registeredModules;
    private final Map<String, Object> loadedModules;

    public JavaScriptModuleManager(JavaScriptEngine engine) {
        this.engine = engine;
        this.registeredModules = new ConcurrentHashMap<>();
        this.loadedModules = new ConcurrentHashMap<>();
    }

    /**
     * Module information
     */
    public static class ModuleInfo {
        public final String name;
        public final String code;
        public final Set<String> dependencies;

        public ModuleInfo(String name, String code, Set<String> dependencies) {
            this.name = name;
            this.code = code;
            this.dependencies = dependencies;
        }
    }

    /**
     * Register module
     */
    public void registerModule(String name, String code, String... dependencies) {
        Set<String> deps = new HashSet<>(Arrays.asList(dependencies));
        registeredModules.put(name, new ModuleInfo(name, code, deps));
    }

    /**
     * Load module with dependencies
     */
    public Object loadModule(String name) throws JavaScriptException {
        if (loadedModules.containsKey(name)) {
            return loadedModules.get(name);
        }

        ModuleInfo module = registeredModules.get(name);
        if (module == null) {
            throw new JavaScriptException("Module not found: " + name);
        }

        // Load dependencies first
        for (String dep : module.dependencies) {
            if (!loadedModules.containsKey(dep)) {
                loadModule(dep);
            }
        }

        // Execute module code
        Object result = engine.execute(module.code);
        loadedModules.put(name, result);
        return result;
    }

    /**
     * Unload module
     */
    public void unloadModule(String name) {
        loadedModules.remove(name);
    }

    /**
     * Check if module is registered
     */
    public boolean hasModule(String name) {
        return registeredModules.containsKey(name);
    }

    /**
     * Get loaded modules
     */
    public Set<String> getLoadedModules() {
        return new HashSet<>(loadedModules.keySet());
    }

    /**
     * Get registered modules
     */
    public Set<String> getRegisteredModules() {
        return new HashSet<>(registeredModules.keySet());
    }

    /**
     * Clear all modules
     */
    public void clear() {
        registeredModules.clear();
        loadedModules.clear();
    }
}
