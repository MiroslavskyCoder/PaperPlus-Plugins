package lxxv.shared.server.script;

import io.javalin.Javalin;
import io.javalin.http.Context;
import com.google.gson.Gson;
import lxxv.shared.javascript.JavaScriptEngine;
import lxxv.shared.javascript.JavaScriptException;
import lxxv.shared.javascript.advanced.*;
import lxxv.shared.server.LXXVServer;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * REST API контроллер для выполнения JavaScript скриптов на сервере.
 * Доступно по адресу /api/script
 */
public class ServerScriptController {
    private final Gson gson = new Gson();
    private final JavaScriptEngine engine;
    private final JavaScriptScriptManager scriptManager;
    private final JavaScriptEventSystem eventSystem;
    private final JavaScriptScheduler scheduler;
    private final JavaScriptModuleManager moduleManager;

    public ServerScriptController(JavaScriptEngine engine,
                                 JavaScriptScriptManager scriptManager,
                                 JavaScriptEventSystem eventSystem,
                                 JavaScriptScheduler scheduler,
                                 JavaScriptModuleManager moduleManager) {
        this.engine = engine;
        this.scriptManager = scriptManager;
        this.eventSystem = eventSystem;
        this.scheduler = scheduler;
        this.moduleManager = moduleManager;
    }

    /**
     * Регистрирует маршруты контроллера
     */
    public void register(Javalin app) {
        // Выполнить JavaScript код
        app.post("/api/script/execute", this::executeScript);

        // Выполнить JavaScript асинхронно
        app.post("/api/script/execute-async", this::executeScriptAsync);

        // Загрузить и выполнить сохранённый скрипт
        app.post("/api/script/run/:name", this::runScript);

        // Загрузить все доступные скрипты
        app.get("/api/script/load-all", this::loadAllScripts);

        // Загрузить один скрипт
        app.post("/api/script/load/:name", this::loadScript);

        // Выполнить функцию из загруженного скрипта
        app.post("/api/script/function/:scriptName/:functionName", this::executeFunction);

        // Работа с событиями
        app.post("/api/script/event/:eventName", this::emitEvent);
        app.get("/api/script/events", this::getEvents);
        app.get("/api/script/listeners/:eventName", this::getListeners);

        // Работа с планировщиком
        app.post("/api/script/timeout", this::setTimeout);
        app.post("/api/script/interval", this::setInterval);
        app.delete("/api/script/task/:taskId", this::cancelTask);
        app.get("/api/script/tasks", this::getActiveTasks);

        // Работа с модулями
        app.post("/api/script/module/:moduleName", this::registerModule);
        app.post("/api/script/module/:moduleName/load", this::loadModule);
        app.get("/api/script/modules", this::getModules);

        // Утилиты
        app.get("/api/script/info", this::getScriptInfo);
        app.get("/api/script/globals", this::getGlobals);
    }

    /**
     * Выполнить JavaScript код синхронно
     * POST /api/script/execute
     * Body: { "code": "2 + 2", "context": { "x": 10 } }
     */
    private void executeScript(Context ctx) {
        try {
            ScriptExecutionRequest req = gson.fromJson(ctx.body(), ScriptExecutionRequest.class);

            Map<String, Object> context = req.context != null ? req.context : new HashMap<>();
            Object result = engine.execute(req.code, context);

            ctx.json(new ScriptExecutionResponse(
                    true,
                    result,
                    null,
                    System.currentTimeMillis()
            ));
        } catch (JavaScriptException e) {
            ctx.status(400).json(new ScriptExecutionResponse(
                    false,
                    null,
                    e.getMessage(),
                    System.currentTimeMillis()
            ));
        } catch (Exception e) {
            ctx.status(500).json(new ScriptExecutionResponse(
                    false,
                    null,
                    "Error: " + e.getMessage(),
                    System.currentTimeMillis()
            ));
        }
    }

    /**
     * Выполнить JavaScript код асинхронно
     * POST /api/script/execute-async
     */
    private void executeScriptAsync(Context ctx) {
        try {
            ScriptExecutionRequest req = gson.fromJson(ctx.body(), ScriptExecutionRequest.class);

            Map<String, Object> context = req.context != null ? req.context : new HashMap<>();

            engine.executeAsync(req.code, context)
                    .thenAccept(result -> {
                        Map<String, Object> response = new HashMap<>();
                        response.put("success", true);
                        response.put("result", result);
                        response.put("timestamp", System.currentTimeMillis());
                        ctx.json(response);
                    })
                    .exceptionally(e -> {
                        Map<String, Object> response = new HashMap<>();
                        response.put("success", false);
                        response.put("error", e.getMessage());
                        response.put("timestamp", System.currentTimeMillis());
                        ctx.status(400).json(response);
                        return null;
                    });
        } catch (Exception e) {
            ctx.status(500).json(new ScriptExecutionResponse(
                    false,
                    null,
                    "Error: " + e.getMessage(),
                    System.currentTimeMillis()
            ));
        }
    }

    /**
     * Выполнить загруженный скрипт
     * POST /api/script/run/:name
     */
    private void runScript(Context ctx) {
        try {
            String scriptName = ctx.pathParam("name");
            ScriptExecutionRequest req = ctx.body().isEmpty() ?
                    new ScriptExecutionRequest() :
                    gson.fromJson(ctx.body(), ScriptExecutionRequest.class);

            Map<String, Object> context = req.context != null ? req.context : new HashMap<>();
            Object result = scriptManager.executeScript(scriptName, context);

            ctx.json(new ScriptExecutionResponse(
                    true,
                    result,
                    null,
                    System.currentTimeMillis()
            ));
        } catch (Exception e) {
            ctx.status(400).json(new ScriptExecutionResponse(
                    false,
                    null,
                    e.getMessage(),
                    System.currentTimeMillis()
            ));
        }
    }

    /**
     * Загрузить все скрипты
     * GET /api/script/load-all
     */
    private void loadAllScripts(Context ctx) {
        try {
            scriptManager.loadAllScripts();
            String[] scripts = scriptManager.getLoadedScripts();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", scripts.length);
            response.put("scripts", scripts);
            ctx.json(response);
        } catch (Exception e) {
            ctx.status(400).json(new ErrorResponse(false, e.getMessage()));
        }
    }

    /**
     * Загрузить один скрипт
     * POST /api/script/load/:name
     */
    private void loadScript(Context ctx) {
        try {
            String scriptName = ctx.pathParam("name");
            scriptManager.executeScript(scriptName);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("scriptName", scriptName);
            response.put("timestamp", System.currentTimeMillis());
            ctx.json(response);
        } catch (Exception e) {
            ctx.status(400).json(new ErrorResponse(false, e.getMessage()));
        }
    }

    /**
     * Выполнить функцию из скрипта
     * POST /api/script/function/:scriptName/:functionName
     * Body: { "args": [10, 20] }
     */
    private void executeFunction(Context ctx) {
        try {
            String scriptName = ctx.pathParam("scriptName");
            String functionName = ctx.pathParam("functionName");

            FunctionCallRequest req = ctx.body().isEmpty() ?
                    new FunctionCallRequest() :
                    gson.fromJson(ctx.body(), FunctionCallRequest.class);

            Object[] args = req.args != null ? req.args : new Object[0];
            Object result = scriptManager.executeScriptFunction(scriptName, functionName, args);

            ctx.json(new ScriptExecutionResponse(
                    true,
                    result,
                    null,
                    System.currentTimeMillis()
            ));
        } catch (Exception e) {
            ctx.status(400).json(new ScriptExecutionResponse(
                    false,
                    null,
                    e.getMessage(),
                    System.currentTimeMillis()
            ));
        }
    }

    /**
     * Запустить событие
     * POST /api/script/event/:eventName
     * Body: { "args": [...] }
     */
    private void emitEvent(Context ctx) {
        try {
            String eventName = ctx.pathParam("eventName");
            EventEmitRequest req = ctx.body().isEmpty() ?
                    new EventEmitRequest() :
                    gson.fromJson(ctx.body(), EventEmitRequest.class);

            Object[] args = req.args != null ? req.args : new Object[0];

            if (req.async) {
                eventSystem.emitAsync(eventName, args);
            } else {
                eventSystem.emit(eventName, args);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("eventName", eventName);
            response.put("async", req.async);
            response.put("timestamp", System.currentTimeMillis());
            ctx.json(response);
        } catch (Exception e) {
            ctx.status(400).json(new ErrorResponse(false, e.getMessage()));
        }
    }

    /**
     * Получить все события
     * GET /api/script/events
     */
    private void getEvents(Context ctx) {
        List<String> events = eventSystem.getRegisteredEvents();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", events.size());
        response.put("events", events);
        ctx.json(response);
    }

    /**
     * Получить слушателей события
     * GET /api/script/listeners/:eventName
     */
    private void getListeners(Context ctx) {
        String eventName = ctx.pathParam("eventName");
        int count = eventSystem.getListenerCount(eventName);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("eventName", eventName);
        response.put("listenerCount", count);
        ctx.json(response);
    }

    /**
     * Установить setTimeout
     * POST /api/script/timeout
     * Body: { "code": "console.log('Hello')", "delayMs": 1000 }
     */
    private void setTimeout(Context ctx) {
        try {
            TimerRequest req = gson.fromJson(ctx.body(), TimerRequest.class);
            String taskId = scheduler.setTimeout(
                    () -> {
                        try {
                            engine.execute(req.code);
                        } catch (JavaScriptException e) {
                            System.err.println("Timeout script error: " + e.getMessage());
                        }
                    },
                    req.delayMs
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("taskId", taskId);
            response.put("delayMs", req.delayMs);
            ctx.json(response);
        } catch (Exception e) {
            ctx.status(400).json(new ErrorResponse(false, e.getMessage()));
        }
    }

    /**
     * Установить setInterval
     * POST /api/script/interval
     * Body: { "code": "console.log('Hello')", "intervalMs": 1000 }
     */
    private void setInterval(Context ctx) {
        try {
            TimerRequest req = gson.fromJson(ctx.body(), TimerRequest.class);
            String taskId = scheduler.setInterval(
                    () -> {
                        try {
                            engine.execute(req.code);
                        } catch (JavaScriptException e) {
                            System.err.println("Interval script error: " + e.getMessage());
                        }
                    },
                    req.intervalMs
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("taskId", taskId);
            response.put("intervalMs", req.intervalMs);
            ctx.json(response);
        } catch (Exception e) {
            ctx.status(400).json(new ErrorResponse(false, e.getMessage()));
        }
    }

    /**
     * Отменить задачу
     * DELETE /api/script/task/:taskId
     */
    private void cancelTask(Context ctx) {
        try {
            String taskId = ctx.pathParam("taskId");
            boolean success = scheduler.clearTimeout(taskId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("taskId", taskId);
            ctx.json(response);
        } catch (Exception e) {
            ctx.status(400).json(new ErrorResponse(false, e.getMessage()));
        }
    }

    /**
     * Получить активные задачи
     * GET /api/script/tasks
     */
    private void getActiveTasks(Context ctx) {
        List<String> tasks = scheduler.getActiveTasks();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", tasks.size());
        response.put("tasks", tasks);
        ctx.json(response);
    }

    /**
     * Регистрировать модуль
     * POST /api/script/module/:moduleName
     * Body: { "code": "exports.foo = ...", "dependencies": [...] }
     */
    private void registerModule(Context ctx) {
        try {
            String moduleName = ctx.pathParam("moduleName");
            ModuleRequest req = gson.fromJson(ctx.body(), ModuleRequest.class);

            String[] deps = req.dependencies != null ? req.dependencies : new String[0];
            moduleManager.registerModule(moduleName, req.code, deps);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("moduleName", moduleName);
            response.put("dependencies", deps.length);
            ctx.json(response);
        } catch (Exception e) {
            ctx.status(400).json(new ErrorResponse(false, e.getMessage()));
        }
    }

    /**
     * Загрузить модуль
     * POST /api/script/module/:moduleName/load
     */
    private void loadModule(Context ctx) {
        try {
            String moduleName = ctx.pathParam("moduleName");
            Object moduleExports = moduleManager.loadModule(moduleName);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("moduleName", moduleName);
            response.put("exports", moduleExports != null ? moduleExports.toString() : "null");
            ctx.json(response);
        } catch (Exception e) {
            ctx.status(400).json(new ErrorResponse(false, e.getMessage()));
        }
    }

    /**
     * Получить список модулей
     * GET /api/script/modules
     */
    private void getModules(Context ctx) {
        List<String> registered = moduleManager.getRegisteredModules();
        List<String> loaded = moduleManager.getLoadedModules();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("registered", registered);
        response.put("loaded", loaded);
        ctx.json(response);
    }

    /**
     * Получить информацию о скриптинге
     * GET /api/script/info
     */
    private void getScriptInfo(Context ctx) {
        Map<String, Object> info = new HashMap<>();
        info.put("engine", "GraalVM JavaScript 22.3.0");
        info.put("events", eventSystem.getRegisteredEvents().size());
        info.put("tasks", scheduler.getActiveTasks().size());
        info.put("loadedScripts", scriptManager.getLoadedScripts().length);
        info.put("modules", new HashMap<String, Object>() {{
            put("registered", moduleManager.getRegisteredModules().size());
            put("loaded", moduleManager.getLoadedModules().size());
        }});

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("info", info);
        ctx.json(response);
    }

    /**
     * Получить список глобальных переменных
     * GET /api/script/globals
     */
    private void getGlobals(Context ctx) {
        List<String> globals = Arrays.asList(
                "LXXVServer", "broadcast", "getPlayer", "getPlayers", "getWorld",
                "getWorlds", "log", "warn", "error", "now", "getMemoryInfo",
                "addEventListener", "emit", "emitAsync", "setTimeout", "setInterval",
                "clearTimeout", "clearInterval"
        );

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", globals.size());
        response.put("globals", globals);
        ctx.json(response);
    }

    // ===== Request/Response Classes =====

    public static class ScriptExecutionRequest {
        public String code;
        public Map<String, Object> context;
    }

    public static class ScriptExecutionResponse {
        public boolean success;
        public Object result;
        public String error;
        public long timestamp;

        public ScriptExecutionResponse(boolean success, Object result, String error, long timestamp) {
            this.success = success;
            this.result = result;
            this.error = error;
            this.timestamp = timestamp;
        }
    }

    public static class FunctionCallRequest {
        public Object[] args;
    }

    public static class EventEmitRequest {
        public Object[] args;
        public boolean async = false;
    }

    public static class TimerRequest {
        public String code;
        public long delayMs;
        public long intervalMs;
    }

    public static class ModuleRequest {
        public String code;
        public String[] dependencies;
    }

    public static class ErrorResponse {
        public boolean success;
        public String error;

        public ErrorResponse(boolean success, String error) {
            this.success = success;
            this.error = error;
        }
    }
}
