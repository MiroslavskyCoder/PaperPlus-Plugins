package lxxv.shared.javascript;

import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.engine.IJavetEngine;
import com.caoccao.javet.interop.engine.JavetEnginePool;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8ValueObject;
import com.caoccao.javet.swc4j.Swc4j;
import com.caoccao.javet.swc4j.options.Swc4jTranspileOptions;
import com.caoccao.javet.swc4j.outputs.Swc4jTranspileOutput;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import lxxv.shared.javascript.runtime.Diagnostics;
import lxxv.shared.javascript.runtime.ExecutionOptions;
import lxxv.shared.javascript.runtime.ExecutionRequest;
import lxxv.shared.javascript.runtime.ExecutionResult;
import lxxv.shared.javascript.runtime.FunctionRegistry;
import lxxv.shared.javascript.runtime.JsValueConverter;
import lxxv.shared.javascript.metrics.RuntimeMetrics;
import lxxv.shared.javascript.sandbox.SandboxGuard;
import lxxv.shared.javascript.sandbox.SandboxPolicy;
import lxxv.shared.javascript.api.ServerApi;
import lxxv.shared.javascript.api.ApiRegistry;
import lxxv.shared.javascript.api.ApiFacade;
import lxxv.shared.javascript.api.MetricsApi;
import lxxv.shared.javascript.api.ScriptApi;
import lxxv.shared.javascript.api.LoggerApi;
import lxxv.shared.javascript.api.HeapApi;
import lxxv.shared.javascript.api.ProcessApi;
import lxxv.shared.javascript.heap.HeapManager;
import lxxv.shared.javascript.util.AsyncPool;
import lxxv.shared.javascript.util.IoLoop;

/**
 * Advanced JavaScript Engine with Javet + swc4j
 * Supports TypeScript/JSX transpilation, V8 execution, and Java integration
 */
public class JavaScriptEngine {
    private static JavaScriptEngine instance;
    private final JavetEnginePool<V8Runtime> enginePool;
    private final Swc4j swc4j;
    private final Map<String, Object> globalContext;
    private final Map<String, JavaScriptFunction> registeredFunctions;
    private final Map<String, Map<String, JavaScriptFunction>> nativeModules;
    private final FunctionRegistry functionRegistry;
    private final RuntimeMetrics runtimeMetrics;
    private final SandboxGuard sandboxGuard;
    private final SandboxPolicy sandboxPolicy;
    private final AsyncPool asyncPool;
    private final IoLoop ioLoop;
    private final ServerApi serverApi;
    private final ApiRegistry apiRegistry;
    private final ApiFacade apiFacade;
    private final ExecutorService executionPool;
    private final HeapManager heapManager;
    private final MetricsApi metricsApi;
    private final ScriptApi scriptApi;
    private final LoggerApi loggerApi;
    private final HeapApi heapApi;
    private final ProcessApi processApi;
    private final ThreadLocal<String> activeModule;
    private boolean enabled;

    private JavaScriptEngine() {
        this.globalContext = new ConcurrentHashMap<>();
        this.registeredFunctions = new ConcurrentHashMap<>();
        this.nativeModules = new ConcurrentHashMap<>();
        this.functionRegistry = new FunctionRegistry();
        this.runtimeMetrics = new RuntimeMetrics();
        this.sandboxGuard = new SandboxGuard();
        this.sandboxPolicy = SandboxPolicy.strictDefaults();
        this.asyncPool = new AsyncPool(4, "js-async");
        this.ioLoop = new IoLoop();
        this.serverApi = new ServerApi(runtimeMetrics, asyncPool, ioLoop);
        this.apiRegistry = new ApiRegistry();
        this.apiFacade = new ApiFacade(apiRegistry);
        this.executionPool = Executors.newCachedThreadPool(new lxxv.shared.javascript.util.NamedThreadFactory("js-exec", true));
        this.heapManager = new HeapManager(64 * 1024 * 1024);
        this.metricsApi = new MetricsApi(runtimeMetrics, new lxxv.shared.javascript.metrics.SystemMetrics(), heapManager);
        this.scriptApi = new ScriptApi(this, new lxxv.shared.javascript.runtime.ScriptRegistry());
        this.loggerApi = new LoggerApi(java.util.logging.Logger.getLogger("JS"));
        this.heapApi = new HeapApi(heapManager);
        this.processApi = new ProcessApi(heapApi);
        this.activeModule = new ThreadLocal<>();
        this.swc4j = new Swc4j();
        this.enginePool = initializeEngine();
        this.enabled = (enginePool != null);

        // expose a stable server object to JS/TS to host all registered functions and values
        this.globalContext.put("server", functionRegistry.getServerBindings());
        this.globalContext.put("serverApi", serverApi);
        this.globalContext.put("api", apiFacade);
        this.globalContext.put("metrics", metricsApi);
        this.globalContext.put("scripts", scriptApi);
        this.globalContext.put("logger", loggerApi);
        this.globalContext.put("heap", heapApi);
        this.globalContext.put("process", processApi);
        this.globalContext.put("requireNativeModule", (JavaScriptFunction) args -> requireNativeModule(args != null && args.length > 0 ? args[0] : null));
    }

    public static synchronized JavaScriptEngine getInstance() {
        if (instance == null) {
            instance = new JavaScriptEngine();
        }
        return instance;
    }

    /**
     * Initialize Javet V8 engine pool
     */
    private JavetEnginePool<V8Runtime> initializeEngine() {
        try {
            return new JavetEnginePool<>();
        } catch (Exception e) {
            System.err.println("[JS Engine] Failed to initialize Javet V8 engine: " + e.getMessage());
            return null;
        }
    }

    /**
     * Transpile TypeScript/JSX to JavaScript using swc4j
     */
    public String transpile(String code, String filename) throws JavaScriptException {
        try {
            Swc4jTranspileOptions options = new Swc4jTranspileOptions();
            Swc4jTranspileOutput output = swc4j.transpile(code, options);
            runtimeMetrics.markTranspiled();
            return output.getCode();
        } catch (Exception e) {
            runtimeMetrics.markFailed();
            throw new JavaScriptException("Failed to transpile code: " + e.getMessage(), e);
        }
    }

    /**
     * Execute JavaScript code synchronously
     */
    public Object execute(String code) throws JavaScriptException {
        return execute(code, Map.of());
    }

    /**
     * Execute JavaScript code with context variables
     */
    public Object execute(String code, Map<String, Object> variables) throws JavaScriptException {
        ExecutionRequest request = new ExecutionRequest(code, null, variables, ExecutionOptions.defaults());
        ExecutionResult result = execute(request);
        if (!result.isSuccess()) {
            throw new JavaScriptException(result.error());
        }
        return result.value();
    }

    /**
     * Execute JavaScript code with extended options and diagnostics
     */
    public ExecutionResult execute(ExecutionRequest request) {
        if (!enabled) {
            return ExecutionResult.failure("JavaScript engine not initialized", 0);
        }

        Diagnostics diagnostics = new Diagnostics();
        long started = System.nanoTime();
        try {
            sandboxGuard.validate(request.options(), sandboxPolicy);
            Object value;
            if (request.options().timeoutMs() > 0) {
                value = executeWithTimeout(request);
            } else {
                value = executeRaw(request.code(), request.variables());
            }
            long durationMs = (System.nanoTime() - started) / 1_000_000;
            runtimeMetrics.markExecuted();
            return ExecutionResult.success(value, durationMs);
        } catch (Exception e) {
            long durationMs = (System.nanoTime() - started) / 1_000_000;
            runtimeMetrics.markFailed();
            diagnostics.error(e.getMessage());
            return ExecutionResult.failure(e.getMessage(), durationMs);
        }
    }

    private Object executeWithTimeout(ExecutionRequest request) throws Exception {
        Future<Object> future = executionPool.submit(() -> executeRaw(request.code(), request.variables()));
        try {
            return future.get(request.options().timeoutMs(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException te) {
            future.cancel(true);
            throw new JavaScriptException("Execution timed out after " + request.options().timeoutMs() + "ms", te);
        }
    }

    private Object executeRaw(String code, Map<String, Object> variables) throws Exception {
        IJavetEngine<V8Runtime> engine = null;
        try {
            engine = enginePool.getEngine();
            V8Runtime runtime = engine.getV8Runtime();

            V8ValueObject globalObject = runtime.getGlobalObject();

            // Inject global context (includes `server` bindings)
            for (Map.Entry<String, Object> entry : globalContext.entrySet()) {
                globalObject.set(entry.getKey(), entry.getValue());
            }

            // Inject variables
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                globalObject.set(entry.getKey(), entry.getValue());
            }

            V8Value result = runtime.getExecutor(code).execute();
            return convertV8Value(result);
        } finally {
            enginePool.releaseEngine(engine);
        }
    }

    /**
     * Execute JavaScript code asynchronously
     */
    public CompletableFuture<Object> executeAsync(String code) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return execute(code);
            } catch (JavaScriptException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Execute JavaScript code asynchronously with context variables
     */
    public CompletableFuture<Object> executeAsync(String code, Map<String, Object> variables) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return execute(code, variables);
            } catch (JavaScriptException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Register a custom Java function accessible from JavaScript
     */
    public void registerFunction(String name, JavaScriptFunction function) {
        String moduleName = activeModule.get();
        if (moduleName != null) {
            nativeModules.computeIfAbsent(moduleName, k -> new ConcurrentHashMap<>()).put(name, function);
            return;
        }

        registeredFunctions.put(name, function);
        functionRegistry.register(name, function);
        globalContext.put("server", functionRegistry.getServerBindings());
        // keep legacy top-level reference for backward compatibility
        globalContext.put(name, function);
    }

    /**
     * Register a simple Java function (lambda) - convenience method
     */
    public void registerFunctionLambda(String name, Function<Object[], Object> function) {
        JavaScriptFunction wrapped = args -> function.apply(args);
        registerFunction(name, wrapped);
    }

    /**
     * Set a global variable accessible from JavaScript
     */
    public void setGlobalVariable(String name, Object value) {
        globalContext.put(name, value);
    }

    /**
     * Set value on the server object exposed to JS/TS
     */
    public void setServerValue(String name, Object value) {
        functionRegistry.setValue(name, value);
        globalContext.put("server", functionRegistry.getServerBindings());
    }

    /**
     * Expose a Java helper object directly onto the server namespace
     */
    public void registerServerHelper(String name, Object helper) {
        functionRegistry.setValue(name, helper);
        globalContext.put("server", functionRegistry.getServerBindings());
    }

    /**
     * Get a global variable
     */
    public Object getGlobalVariable(String name) {
        return globalContext.get(name);
    }

    /**
     * Get value from the server object exposed to JS/TS
     */
    public Object getServerValue(String name) {
        return functionRegistry.getValue(name);
    }

    /**
     * Check if JavaScript engine is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Convert V8Value to Java Object
     */
    private Object convertV8Value(V8Value value) {
        return JsValueConverter.toJava(value);
    }

    public HeapManager getHeapManager() {
        return heapManager;
    }

    public HeapApi getHeapApi() {
        return heapApi;
    }

    public ProcessApi getProcessApi() {
        return processApi;
    }

    public MetricsApi getMetricsApi() {
        return metricsApi;
    }

    public void beginModuleRegistration(String moduleName) {
        if (moduleName == null) return;
        nativeModules.computeIfAbsent(moduleName, k -> new ConcurrentHashMap<>());
        activeModule.set(moduleName);
    }

    public void endModuleRegistration() {
        activeModule.remove();
    }

    public Map<String, JavaScriptFunction> getNativeModule(String name) {
        if (name == null) return Map.of();
        Map<String, JavaScriptFunction> module = nativeModules.get(name.toString());
        return module != null ? Map.copyOf(module) : Map.of();
    }

    private Object requireNativeModule(Object name) {
        Map<String, JavaScriptFunction> module = getNativeModule(name != null ? name.toString() : null);
        if (module.isEmpty()) {
            return Map.of("__error", "Native module not found: " + name);
        }
        return module;
    }

    /**
     * Shutdown the engine
     */
    public void shutdown() {
        try {
            if (enginePool != null) {
                enginePool.close();
            }
            asyncPool.shutdown();
            ioLoop.shutdown();
            executionPool.shutdownNow();
            globalContext.clear();
            registeredFunctions.clear();
            functionRegistry.clear();
            heapApi.clear();
            enabled = false;
        } catch (Exception e) {
            System.err.println("Error shutting down engine: " + e.getMessage());
        }
    }
}
