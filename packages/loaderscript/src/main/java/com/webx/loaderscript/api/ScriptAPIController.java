package com.webx.loaderscript.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.webx.loaderscript.manager.ScriptManager;
import com.webx.loaderscript.models.ScriptInfo;
import io.javalin.Javalin;
import io.javalin.http.Context;
import lxxv.shared.javascript.JavaScriptEngine;

import java.util.*;
import java.util.stream.Collectors;

/**
 * REST API Controller for script management from web dashboard
 */
public class ScriptAPIController {
    
    private final Gson gson = new Gson();
    private final ScriptManager scriptManager;
    private final JavaScriptEngine jsEngine;
    
    public ScriptAPIController(ScriptManager scriptManager, JavaScriptEngine jsEngine) {
        this.scriptManager = scriptManager;
        this.jsEngine = jsEngine;
    }
    
    /**
     * Register all API routes on Javalin app
     * Called by WebX Dashboard when initializing
     */
    public void registerRoutes(Javalin app) {
        // Script management
        app.get("/api/loaderscript/scripts", this::listScripts);
        app.get("/api/loaderscript/scripts/:name", this::getScript);
        app.post("/api/loaderscript/scripts", this::createScript);
        app.put("/api/loaderscript/scripts/:name", this::updateScript);
        app.delete("/api/loaderscript/scripts/:name", this::deleteScript);
        
        // Script control
        app.post("/api/loaderscript/scripts/:name/load", this::loadScript);
        app.post("/api/loaderscript/scripts/:name/reload", this::reloadScript);
        app.post("/api/loaderscript/scripts/:name/unload", this::unloadScript);
        app.post("/api/loaderscript/reload-all", this::reloadAll);
        
        // Script execution
        app.post("/api/loaderscript/execute", this::executeCode);
        app.post("/api/loaderscript/transpile", this::transpileCode);
        
        // Info
        app.get("/api/loaderscript/info", this::getInfo);
    }
    
    /**
     * GET /api/loaderscript/scripts - List all scripts
     */
    private void listScripts(Context ctx) {
        Map<String, ScriptInfo> loaded = scriptManager.getLoadedScripts();
        List<String> all = scriptManager.listAllScripts();
        
        List<Map<String, Object>> scripts = all.stream().map(name -> {
            Map<String, Object> script = new HashMap<>();
            script.put("name", name);
            
            ScriptInfo info = loaded.get(name);
            if (info != null) {
                script.put("loaded", true);
                script.put("success", info.isSuccess());
                script.put("size", info.getSize());
                script.put("lastModified", info.getLastModified());
                script.put("loadedAt", info.getLoadedAt());
                script.put("error", info.getError());
            } else {
                script.put("loaded", false);
                script.put("success", false);
            }
            
            return script;
        }).collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("scripts", scripts);
        response.put("total", scripts.size());
        response.put("loaded", loaded.size());
        
        ctx.json(response);
    }
    
    /**
     * GET /api/loaderscript/scripts/:name - Get script content
     */
    private void getScript(Context ctx) {
        String name = ctx.pathParam("name");
        String content = scriptManager.readScript(name);
        
        if (content == null) {
            ctx.status(404).json(Map.of("error", "Script not found"));
            return;
        }
        
        ScriptInfo info = scriptManager.getScriptInfo(name);
        
        Map<String, Object> response = new HashMap<>();
        response.put("name", name);
        response.put("content", content);
        response.put("loaded", info != null);
        if (info != null) {
            response.put("success", info.isSuccess());
            response.put("error", info.getError());
        }
        
        ctx.json(response);
    }
    
    /**
     * POST /api/loaderscript/scripts - Create new script
     */
    private void createScript(Context ctx) {
        try {
            JsonObject body = gson.fromJson(ctx.body(), JsonObject.class);
            String name = body.get("name").getAsString();
            String content = body.has("content") ? body.get("content").getAsString() : null;
            
            if (scriptManager.createScript(name, content)) {
                ctx.json(Map.of("success", true, "message", "Script created", "name", name));
            } else {
                ctx.status(400).json(Map.of("success", false, "error", "Failed to create script"));
            }
        } catch (Exception e) {
            ctx.status(400).json(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * PUT /api/loaderscript/scripts/:name - Update script content
     */
    private void updateScript(Context ctx) {
        try {
            String name = ctx.pathParam("name");
            JsonObject body = gson.fromJson(ctx.body(), JsonObject.class);
            String content = body.get("content").getAsString();
            
            if (scriptManager.writeScript(name, content)) {
                // Auto-reload if loaded
                boolean autoReload = body.has("autoReload") && body.get("autoReload").getAsBoolean();
                if (autoReload && scriptManager.isScriptLoaded(name)) {
                    scriptManager.reloadScript(name);
                }
                
                ctx.json(Map.of("success", true, "message", "Script updated", "reloaded", autoReload));
            } else {
                ctx.status(400).json(Map.of("success", false, "error", "Failed to update script"));
            }
        } catch (Exception e) {
            ctx.status(400).json(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * DELETE /api/loaderscript/scripts/:name - Delete script
     */
    private void deleteScript(Context ctx) {
        String name = ctx.pathParam("name");
        
        if (scriptManager.deleteScript(name)) {
            ctx.json(Map.of("success", true, "message", "Script deleted"));
        } else {
            ctx.status(400).json(Map.of("success", false, "error", "Failed to delete script"));
        }
    }
    
    /**
     * POST /api/loaderscript/scripts/:name/load - Load script
     */
    private void loadScript(Context ctx) {
        String name = ctx.pathParam("name");
        
        if (scriptManager.loadScript(name)) {
            ScriptInfo info = scriptManager.getScriptInfo(name);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Script loaded");
            if (info != null) {
                response.put("scriptSuccess", info.isSuccess());
                response.put("error", info.getError());
            }
            ctx.json(response);
        } else {
            ctx.status(400).json(Map.of("success", false, "error", "Failed to load script"));
        }
    }
    
    /**
     * POST /api/loaderscript/scripts/:name/reload - Reload script
     */
    private void reloadScript(Context ctx) {
        String name = ctx.pathParam("name");
        
        if (scriptManager.reloadScript(name)) {
            ScriptInfo info = scriptManager.getScriptInfo(name);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Script reloaded");
            if (info != null) {
                response.put("scriptSuccess", info.isSuccess());
                response.put("error", info.getError());
            }
            ctx.json(response);
        } else {
            ctx.status(400).json(Map.of("success", false, "error", "Failed to reload script"));
        }
    }
    
    /**
     * POST /api/loaderscript/scripts/:name/unload - Unload script
     */
    private void unloadScript(Context ctx) {
        String name = ctx.pathParam("name");
        
        if (scriptManager.unloadScript(name)) {
            ctx.json(Map.of("success", true, "message", "Script unloaded"));
        } else {
            ctx.status(400).json(Map.of("success", false, "error", "Script not loaded"));
        }
    }
    
    /**
     * POST /api/loaderscript/reload-all - Reload all scripts
     */
    private void reloadAll(Context ctx) {
        scriptManager.reloadAllScripts();
        
        Map<String, ScriptInfo> loaded = scriptManager.getLoadedScripts();
        long successful = loaded.values().stream().filter(ScriptInfo::isSuccess).count();
        long failed = loaded.size() - successful;
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "All scripts reloaded");
        response.put("total", loaded.size());
        response.put("successful", successful);
        response.put("failed", failed);
        
        ctx.json(response);
    }
    
    /**
     * POST /api/loaderscript/execute - Execute JavaScript code
     */
    private void executeCode(Context ctx) {
        try {
            JsonObject body = gson.fromJson(ctx.body(), JsonObject.class);
            String code = body.get("code").getAsString();
            
            Object result = jsEngine.execute(code, new HashMap<>());
            
            ctx.json(Map.of(
                "success", true,
                "result", result != null ? result.toString() : "null"
            ));
        } catch (Exception e) {
            ctx.status(400).json(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    /**
     * POST /api/loaderscript/transpile - Transpile TypeScript code
     */
    private void transpileCode(Context ctx) {
        try {
            JsonObject body = gson.fromJson(ctx.body(), JsonObject.class);
            String code = body.get("code").getAsString();
            String filename = body.has("filename") ? body.get("filename").getAsString() : "temp.ts";
            
            String transpiled = jsEngine.transpile(code, filename);
            
            ctx.json(Map.of(
                "success", true,
                "transpiled", transpiled
            ));
        } catch (Exception e) {
            ctx.status(400).json(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    /**
     * GET /api/loaderscript/info - Get LoaderScript info
     */
    private void getInfo(Context ctx) {
        Map<String, ScriptInfo> loaded = scriptManager.getLoadedScripts();
        List<String> all = scriptManager.listAllScripts();
        
        Map<String, Object> info = new HashMap<>();
        info.put("version", "1.0.0");
        info.put("scriptsFolder", scriptManager.getScriptsFolder().getAbsolutePath());
        info.put("totalScripts", all.size());
        info.put("loadedScripts", loaded.size());
        info.put("successfulScripts", loaded.values().stream().filter(ScriptInfo::isSuccess).count());
        info.put("failedScripts", loaded.values().stream().filter(s -> !s.isSuccess()).count());
        
        ctx.json(info);
    }
}
