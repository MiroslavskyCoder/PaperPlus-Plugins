package lxxv.shared.javascript;

import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.engine.IJavetEngine;
import com.caoccao.javet.interop.engine.JavetEnginePool;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8ValueObject;
import com.caoccao.javet.swc4j.Swc4j;
import com.caoccao.javet.swc4j.options.Swc4jTranspileOptions;
import com.caoccao.javet.swc4j.outputs.Swc4jTranspileOutput;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

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
    private boolean enabled;

    private JavaScriptEngine() {
        this.globalContext = new HashMap<>();
        this.registeredFunctions = new HashMap<>();
        this.swc4j = new Swc4j();
        this.enginePool = initializeEngine();
        this.enabled = (enginePool != null);
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
            return output.getCode();
        } catch (Exception e) {
            throw new JavaScriptException("Failed to transpile code: " + e.getMessage(), e);
        }
    }

    /**
     * Execute JavaScript code synchronously
     */
    public Object execute(String code) throws JavaScriptException {
        if (!enabled) {
            throw new JavaScriptException("JavaScript engine not initialized");
        }

        IJavetEngine<V8Runtime> engine = null;
        try {
            engine = enginePool.getEngine();
            V8Runtime runtime = engine.getV8Runtime();
            
            // Inject global context
            V8ValueObject globalObject = runtime.getGlobalObject();
            for (Map.Entry<String, Object> entry : globalContext.entrySet()) {
                globalObject.set(entry.getKey(), entry.getValue());
            }
            
            V8Value result = runtime.getExecutor(code).execute();
            return convertV8Value(result);
        } catch (Exception e) {
            throw new JavaScriptException("Error executing JavaScript: " + e.getMessage(), e);
        } finally {
            enginePool.releaseEngine(engine);
        }
    }

    /**
     * Execute JavaScript code with context variables
     */
    public Object execute(String code, Map<String, Object> variables) throws JavaScriptException {
        if (!enabled) {
            throw new JavaScriptException("JavaScript engine not initialized");
        }

        IJavetEngine<V8Runtime> engine = null;
        try {
            engine = enginePool.getEngine();
            V8Runtime runtime = engine.getV8Runtime();
            
            V8ValueObject globalObject = runtime.getGlobalObject();
            
            // Inject global context
            for (Map.Entry<String, Object> entry : globalContext.entrySet()) {
                globalObject.set(entry.getKey(), entry.getValue());
            }
            
            // Inject variables
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                globalObject.set(entry.getKey(), entry.getValue());
            }
            
            V8Value result = runtime.getExecutor(code).execute();
            return convertV8Value(result);
        } catch (Exception e) {
            throw new JavaScriptException("Error executing JavaScript: " + e.getMessage(), e);
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
        registeredFunctions.put(name, function);
        globalContext.put(name, function);
    }

    /**
     * Register a simple Java function (lambda) - convenience method
     */
    public void registerFunctionLambda(String name, Function<Object[], Object> function) {
        JavaScriptFunction wrapped = args -> function.apply(args);
        registeredFunctions.put(name, wrapped);
        globalContext.put(name, wrapped);
    }

    /**
     * Set a global variable accessible from JavaScript
     */
    public void setGlobalVariable(String name, Object value) {
        globalContext.put(name, value);
    }

    /**
     * Get a global variable
     */
    public Object getGlobalVariable(String name) {
        return globalContext.get(name);
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
        if (value == null || value.isNullOrUndefined()) {
            return null;
        }
        
        try {
            if (value instanceof com.caoccao.javet.values.primitive.V8ValueString) {
                return value.toString();
            } else if (value instanceof com.caoccao.javet.values.primitive.V8ValueInteger) {
                return ((com.caoccao.javet.values.primitive.V8ValueInteger) value).getValue();
            } else if (value instanceof com.caoccao.javet.values.primitive.V8ValueDouble) {
                return ((com.caoccao.javet.values.primitive.V8ValueDouble) value).getValue();
            } else if (value instanceof com.caoccao.javet.values.primitive.V8ValueBoolean) {
                return ((com.caoccao.javet.values.primitive.V8ValueBoolean) value).getValue();
            } else {
                // For complex objects, return as string representation
                return value.toString();
            }
        } catch (Exception e) {
            System.err.println("Error converting V8 value: " + e.getMessage());
        }
        
        return value;
    }

    /**
     * Shutdown the engine
     */
    public void shutdown() {
        try {
            if (enginePool != null) {
                enginePool.close();
            }
            globalContext.clear();
            registeredFunctions.clear();
            enabled = false;
        } catch (Exception e) {
            System.err.println("Error shutting down engine: " + e.getMessage());
        }
    }
}
