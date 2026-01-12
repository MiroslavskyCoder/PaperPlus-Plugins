package lxxv.shared.server.script;

import io.javalin.Javalin;
import io.javalin.http.Context;
import com.google.gson.Gson;
import lxxv.shared.javascript.JavaScriptEngine;
import lxxv.shared.javascript.JavaScriptException;
import lxxv.shared.javascript.advanced.JavaScriptEventSystem;
import lxxv.shared.javascript.advanced.JavaScriptScheduler;
import lxxv.shared.javascript.advanced.JavaScriptModuleManager;
import lxxv.shared.server.LXXVServer;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * REST API Controller for JavaScript execution
 * Provides HTTP endpoints for WebX Dashboard integration
 */
public class ServerScriptController {
    private final Gson gson = new Gson();
    private final JavaScriptEngine engine;
    private final JavaScriptEventSystem eventSystem;
    private final JavaScriptScheduler scheduler;
    private final JavaScriptModuleManager moduleManager;

    public ServerScriptController(JavaScriptEngine engine,
                                 JavaScriptEventSystem eventSystem,
                                 JavaScriptScheduler scheduler,
                                 JavaScriptModuleManager moduleManager) {
        this.engine = engine;
        this.eventSystem = eventSystem;
        this.scheduler = scheduler;
        this.moduleManager = moduleManager;
    }

    /**
     * Register all routes
     */
    public void register(Javalin app) {
        // Execute JavaScript code
        app.post("/api/script/execute", this::executeScript);
        
        // Execute JavaScript asynchronously
        app.post("/api/script/execute-async", this::executeScriptAsync);
        
        // Transpile TypeScript/JSX
        app.post("/api/script/transpile", this::transpileCode);
        
        // Event system endpoints
        app.post("/api/script/event/:eventName", this::emitEvent);
        app.get("/api/script/events", this::getEvents);
        app.get("/api/script/listeners/:eventName", this::getListeners);
        
        // Scheduler endpoints
        app.post("/api/script/timeout", this::createTimeout);
        app.post("/api/script/interval", this::createInterval);
        app.delete("/api/script/task/:taskId", this::cancelTask);
        app.get("/api/script/tasks", this::getActiveTasks);
        
        // Module endpoints
        app.post("/api/script/module/:name", this::registerModule);
        app.post("/api/script/module/:name/load", this::loadModule);
        app.get("/api/script/modules", this::getModules);
        
        // Info endpoints
        app.get("/api/script/info", this::getScriptInfo);
    }

    /**
     * Execute JavaScript code
     */
    private void executeScript(Context ctx) {
        try {
            ExecuteRequest req = gson.fromJson(ctx.body(), ExecuteRequest.class);
            
            Map<String, Object> context = req.context != null ? req.context : new HashMap<>();
            Object result = engine.execute(req.code, context);
            
            ctx.json(new ExecuteResponse(true, result, null));
        } catch (Exception e) {
            ctx.status(400).json(new ExecuteResponse(false, null, e.getMessage()));
        }
    }

    /**
     * Execute JavaScript asynchronously
     */
    private void executeScriptAsync(Context ctx) {
        try {
            ExecuteRequest req = gson.fromJson(ctx.body(), ExecuteRequest.class);
            
            Map<String, Object> context = req.context != null ? req.context : new HashMap<>();
            
            CompletableFuture<Object> future = engine.executeAsync(req.code, context);
            future.thenAccept(result -> {
                // Result will be processed asynchronously
            }).exceptionally(ex -> {
                System.err.println("Async execution failed: " + ex.getMessage());
                return null;
            });
            
            ctx.json(Map.of("success", true, "message", "Script executing asynchronously"));
        } catch (Exception e) {
            ctx.status(400).json(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * Transpile TypeScript/JSX code
     */
    private void transpileCode(Context ctx) {
        try {
            TranspileRequest req = gson.fromJson(ctx.body(), TranspileRequest.class);
            String transpiled = engine.transpile(req.code, req.filename);
            ctx.json(new TranspileResponse(true, transpiled, null));
        } catch (Exception e) {
            ctx.status(400).json(new TranspileResponse(false, null, e.getMessage()));
        }
    }

    /**
     * Emit event
     */
    private void emitEvent(Context ctx) {
        try {
            String eventName = ctx.pathParam("eventName");
            EventRequest req = gson.fromJson(ctx.body(), EventRequest.class);
            
            Object[] args = req.args != null ? req.args : new Object[0];
            eventSystem.emit(eventName, args);
            
            ctx.json(Map.of("success", true, "event", eventName));
        } catch (Exception e) {
            ctx.status(400).json(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * Get all registered events
     */
    private void getEvents(Context ctx) {
        Set<String> events = eventSystem.getRegisteredEvents();
        ctx.json(Map.of("success", true, "events", events));
    }

    /**
     * Get listener count for event
     */
    private void getListeners(Context ctx) {
        String eventName = ctx.pathParam("eventName");
        int count = eventSystem.getListenerCount(eventName);
        ctx.json(Map.of("success", true, "event", eventName, "listeners", count));
    }

    /**
     * Create setTimeout task
     */
    private void createTimeout(Context ctx) {
        try {
            TimeoutRequest req = gson.fromJson(ctx.body(), TimeoutRequest.class);
            
            String taskId = scheduler.setTimeout(() -> {
                try {
                    engine.execute(req.code);
                } catch (JavaScriptException e) {
                    System.err.println("Timeout task failed: " + e.getMessage());
                }
            }, req.delay);
            
            ctx.json(Map.of("success", true, "taskId", taskId));
        } catch (Exception e) {
            ctx.status(400).json(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * Create setInterval task
     */
    private void createInterval(Context ctx) {
        try {
            IntervalRequest req = gson.fromJson(ctx.body(), IntervalRequest.class);
            
            String taskId = scheduler.setInterval(() -> {
                try {
                    engine.execute(req.code);
                } catch (JavaScriptException e) {
                    System.err.println("Interval task failed: " + e.getMessage());
                }
            }, req.interval);
            
            ctx.json(Map.of("success", true, "taskId", taskId));
        } catch (Exception e) {
            ctx.status(400).json(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * Cancel task
     */
    private void cancelTask(Context ctx) {
        String taskId = ctx.pathParam("taskId");
        boolean cancelled = scheduler.clearTimeout(taskId);
        ctx.json(Map.of("success", cancelled, "taskId", taskId));
    }

    /**
     * Get active tasks
     */
    private void getActiveTasks(Context ctx) {
        Map<String, JavaScriptScheduler.TaskInfo> tasks = scheduler.getActiveTasks();
        ctx.json(Map.of("success", true, "tasks", tasks));
    }

    /**
     * Register module
     */
    private void registerModule(Context ctx) {
        try {
            String moduleName = ctx.pathParam("name");
            ModuleRequest req = gson.fromJson(ctx.body(), ModuleRequest.class);
            
            String[] deps = req.dependencies != null ? 
                req.dependencies.toArray(new String[0]) : new String[0];
            
            moduleManager.registerModule(moduleName, req.code, deps);
            ctx.json(Map.of("success", true, "module", moduleName));
        } catch (Exception e) {
            ctx.status(400).json(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * Load module
     */
    private void loadModule(Context ctx) {
        try {
            String moduleName = ctx.pathParam("name");
            Object result = moduleManager.loadModule(moduleName);
            ctx.json(Map.of("success", true, "module", moduleName, "result", result));
        } catch (Exception e) {
            ctx.status(400).json(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * Get modules info
     */
    private void getModules(Context ctx) {
        Map<String, Object> modules = new HashMap<>();
        modules.put("registered", moduleManager.getRegisteredModules());
        modules.put("loaded", moduleManager.getLoadedModules());
        ctx.json(Map.of("success", true, "modules", modules));
    }

    /**
     * Get script system info
     */
    private void getScriptInfo(Context ctx) {
        Map<String, Object> info = new HashMap<>();
        info.put("engine", "Javet V8");
        info.put("transpiler", "swc4j");
        info.put("enabled", engine.isEnabled());
        info.put("events", eventSystem.getRegisteredEvents().size());
        info.put("tasks", scheduler.getActiveTasks().size());
        info.put("modules", moduleManager.getRegisteredModules().size());
        
        ctx.json(Map.of("success", true, "info", info));
    }

    // ===== REQUEST/RESPONSE CLASSES =====

    private static class ExecuteRequest {
        String code;
        Map<String, Object> context;
    }

    private static class ExecuteResponse {
        boolean success;
        Object result;
        String error;

        ExecuteResponse(boolean success, Object result, String error) {
            this.success = success;
            this.result = result;
            this.error = error;
        }
    }

    private static class TranspileRequest {
        String code;
        String filename;
    }

    private static class TranspileResponse {
        boolean success;
        String code;
        String error;

        TranspileResponse(boolean success, String code, String error) {
            this.success = success;
            this.code = code;
            this.error = error;
        }
    }

    private static class EventRequest {
        Object[] args;
    }

    private static class TimeoutRequest {
        String code;
        long delay;
    }

    private static class IntervalRequest {
        String code;
        long interval;
    }

    private static class ModuleRequest {
        String code;
        List<String> dependencies;
    }
}
