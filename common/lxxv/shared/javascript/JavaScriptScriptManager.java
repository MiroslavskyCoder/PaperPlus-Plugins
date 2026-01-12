package lxxv.shared.javascript;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

/**
 * JavaScript Script Manager
 * Loads and manages JavaScript scripts from files
 */
public class JavaScriptScriptManager {
    private final JavaScriptEngine engine;
    private final Map<String, String> loadedScripts;
    private final File scriptsDirectory;
    private final Logger logger;

    public JavaScriptScriptManager(File scriptsDirectory) {
        this.engine = JavaScriptEngine.getInstance();
        this.loadedScripts = new HashMap<>();
        this.scriptsDirectory = scriptsDirectory;
        this.logger = Logger.getLogger("JavaScriptScriptManager");

        if (!scriptsDirectory.exists()) {
            scriptsDirectory.mkdirs();
        }
    }

    /**
     * Load script from file
     */
    public void loadScript(String name, File file) throws IOException, JavaScriptException {
        String code = new String(Files.readAllBytes(file.toPath()));
        loadedScripts.put(name, code);
        logger.info("Loaded script: " + name + " from " + file.getAbsolutePath());
    }

    /**
     * Load all scripts from directory
     */
    public void loadAllScripts() throws IOException, JavaScriptException {
        File[] files = scriptsDirectory.listFiles((dir, name) -> name.endsWith(".js"));
        if (files != null) {
            for (File file : files) {
                String scriptName = file.getName().replace(".js", "");
                loadScript(scriptName, file);
            }
        }
        logger.info("Loaded " + loadedScripts.size() + " scripts from " + scriptsDirectory.getAbsolutePath());
    }

    /**
     * Get loaded script code
     */
    public String getScript(String name) {
        return loadedScripts.get(name);
    }

    /**
     * Execute loaded script
     */
    public Object executeScript(String name) throws JavaScriptException {
        String code = loadedScripts.get(name);
        if (code == null) {
            throw new JavaScriptException("Script not found: " + name);
        }
        return engine.execute(code);
    }

    /**
     * Execute loaded script with context
     */
    public Object executeScript(String name, Map<String, Object> context) throws JavaScriptException {
        String code = loadedScripts.get(name);
        if (code == null) {
            throw new JavaScriptException("Script not found: " + name);
        }
        return engine.execute(code, context);
    }

    /**
     * Execute script asynchronously
     */
    public CompletableFuture<Object> executeScriptAsync(String name) {
        return engine.executeAsync(loadedScripts.get(name));
    }

    /**
     * Execute script function
     */
    public Object executeScriptFunction(String scriptName, String functionName, Object... args) throws JavaScriptException {
        String code = loadedScripts.get(scriptName);
        if (code == null) {
            throw new JavaScriptException("Script not found: " + scriptName);
        }

        // Combine script loading with function call
        String combinedCode = code + "\n" + functionName + "(" + argsToString(args) + ")";
        return engine.execute(combinedCode);
    }

    /**
     * Reload script from file
     */
    public void reloadScript(String name) throws IOException, JavaScriptException {
        File file = new File(scriptsDirectory, name + ".js");
        if (!file.exists()) {
            throw new JavaScriptException("Script file not found: " + file.getAbsolutePath());
        }
        loadScript(name, file);
    }

    /**
     * Remove script from memory
     */
    public void removeScript(String name) {
        loadedScripts.remove(name);
        logger.info("Removed script: " + name);
    }

    /**
     * Clear all scripts
     */
    public void clearAllScripts() {
        loadedScripts.clear();
        logger.info("Cleared all scripts");
    }

    /**
     * Get list of loaded scripts
     */
    public String[] getLoadedScripts() {
        return loadedScripts.keySet().toArray(new String[0]);
    }

    /**
     * Convert arguments to JavaScript string
     */
    private String argsToString(Object[] args) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            if (i > 0) sb.append(", ");
            
            Object arg = args[i];
            if (arg == null) {
                sb.append("null");
            } else if (arg instanceof String) {
                sb.append("'").append(((String) arg).replace("'", "\\'")).append("'");
            } else if (arg instanceof Number || arg instanceof Boolean) {
                sb.append(arg);
            } else {
                sb.append(arg.toString());
            }
        }
        return sb.toString();
    }
}
