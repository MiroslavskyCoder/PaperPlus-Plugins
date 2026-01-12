package lxxv.shared.javascript;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * V8 JavaScript Engine Wrapper
 * Provides safe execution of JavaScript code with Java integration
 */
public class JavaScriptEngine {
    private static JavaScriptEngine instance;
    private final Map<String, Object> globalContext;
    private final Map<String, JavaScriptFunction> registeredFunctions;
    private boolean enabled;

    private JavaScriptEngine() {
        this.globalContext = new HashMap<>();
        this.registeredFunctions = new HashMap<>();
        this.enabled = initializeEngine();
    }

    public static synchronized JavaScriptEngine getInstance() {
        if (instance == null) {
            instance = new JavaScriptEngine();
        }
        return instance;
    }

    /**
     * Initialize the JavaScript engine
     */
    private boolean initializeEngine() {
        try {
            // Try to load V8 engine first
            Class.forName("com.eclipsesource.v8.V8");
            return true;
        } catch (ClassNotFoundException e) {
            try {
                // Fallback to GraalVM
                Class.forName("org.graalvm.polyglot.Context");
                return true;
            } catch (ClassNotFoundException ex) {
                System.err.println("[JS Engine] Failed to initialize JavaScript engine");
                return false;
            }
        }
    }

    /**
     * Execute JavaScript code synchronously
     */
    public Object execute(String code) throws JavaScriptException {
        if (!enabled) {
            throw new JavaScriptException("JavaScript engine not initialized");
        }

        try {
            org.graalvm.polyglot.Context context = org.graalvm.polyglot.Context.newBuilder("js")
                .allowAllAccess(true)
                .build();

            try {
                org.graalvm.polyglot.Value result = context.eval("js", code);
                return convertValue(result);
            } finally {
                context.close();
            }
        } catch (Exception e) {
            throw new JavaScriptException("Error executing JavaScript: " + e.getMessage(), e);
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
     * Execute JavaScript with context variables
     */
    public Object execute(String code, Map<String, Object> variables) throws JavaScriptException {
        if (!enabled) {
            throw new JavaScriptException("JavaScript engine not initialized");
        }

        try {
            org.graalvm.polyglot.Context context = org.graalvm.polyglot.Context.newBuilder("js")
                .allowAllAccess(true)
                .build();

            try {
                // Bind variables to context
                for (Map.Entry<String, Object> entry : variables.entrySet()) {
                    context.getBindings("js").putMember(entry.getKey(), entry.getValue());
                }

                org.graalvm.polyglot.Value result = context.eval("js", code);
                return convertValue(result);
            } finally {
                context.close();
            }
        } catch (Exception e) {
            throw new JavaScriptException("Error executing JavaScript: " + e.getMessage(), e);
        }
    }

    /**
     * Call a JavaScript function
     */
    public Object callFunction(String functionCode, Object... args) throws JavaScriptException {
        if (!enabled) {
            throw new JavaScriptException("JavaScript engine not initialized");
        }

        try {
            org.graalvm.polyglot.Context context = org.graalvm.polyglot.Context.newBuilder("js")
                .allowAllAccess(true)
                .build();

            try {
                context.eval("js", functionCode);
                
                // Extract function name from code (simple approach)
                String functionName = extractFunctionName(functionCode);
                if (functionName == null) {
                    throw new JavaScriptException("Could not determine function name");
                }

                org.graalvm.polyglot.Value function = context.getBindings("js").getMember(functionName);
                if (function == null || !function.canExecute()) {
                    throw new JavaScriptException("Function not found or not callable: " + functionName);
                }

                org.graalvm.polyglot.Value result = function.execute(args);
                return convertValue(result);
            } finally {
                context.close();
            }
        } catch (Exception e) {
            throw new JavaScriptException("Error calling JavaScript function: " + e.getMessage(), e);
        }
    }

    /**
     * Register a custom Java function accessible from JavaScript
     */
    public void registerFunction(String name, JavaScriptFunction function) {
        registeredFunctions.put(name, function);
        globalContext.put(name, function);
    }

    /**
     * Register a simple Java function (lambda)
     */
    public void registerFunction(String name, Function<Object[], Object> function) {
        registerFunction(name, new JavaScriptFunction() {
            @Override
            public Object call(Object... args) {
                return function.apply(args);
            }
        });
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
     * Eval JSON string and return object
     */
    public Object parseJSON(String json) throws JavaScriptException {
        try {
            return execute("JSON.parse('" + escapeString(json) + "')");
        } catch (JavaScriptException e) {
            throw new JavaScriptException("Error parsing JSON: " + e.getMessage(), e);
        }
    }

    /**
     * Convert JavaScript object to JSON string
     */
    public String toJSON(Object obj) throws JavaScriptException {
        try {
            return execute("JSON.stringify(" + obj.toString() + ")").toString();
        } catch (JavaScriptException e) {
            throw new JavaScriptException("Error converting to JSON: " + e.getMessage(), e);
        }
    }

    /**
     * Evaluate mathematical expression
     */
    public double evaluateMath(String expression) throws JavaScriptException {
        Object result = execute(expression);
        if (result instanceof Number) {
            return ((Number) result).doubleValue();
        }
        throw new JavaScriptException("Result is not a number: " + result);
    }

    /**
     * Check if JavaScript engine is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Convert GraalVM Value to Java Object
     */
    private Object convertValue(org.graalvm.polyglot.Value value) {
        if (value == null || value.isNull()) {
            return null;
        } else if (value.isString()) {
            return value.asString();
        } else if (value.isNumber()) {
            return value.asDouble();
        } else if (value.isBoolean()) {
            return value.asBoolean();
        } else if (value.isHostObject()) {
            return value.asHostObject();
        } else if (value.hasMembers()) {
            Map<String, Object> map = new HashMap<>();
            for (String key : value.getMemberKeys()) {
                map.put(key, convertValue(value.getMember(key)));
            }
            return map;
        } else if (value.hasArrayElements()) {
            int length = (int) value.getArraySize();
            Object[] array = new Object[length];
            for (int i = 0; i < length; i++) {
                array[i] = convertValue(value.getArrayElement(i));
            }
            return array;
        }
        return value;
    }

    /**
     * Extract function name from JavaScript code
     */
    private String extractFunctionName(String code) {
        String trimmed = code.trim();
        
        // Match: function name() or const name = () or let name = () or var name = ()
        String[] patterns = {
            "function\\s+(\\w+)",
            "(?:const|let|var)\\s+(\\w+)\\s*="
        };

        for (String pattern : patterns) {
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher m = p.matcher(trimmed);
            if (m.find()) {
                return m.group(1);
            }
        }

        return null;
    }

    /**
     * Escape string for JavaScript
     */
    private String escapeString(String str) {
        return str.replace("\\", "\\\\")
                 .replace("'", "\\'")
                 .replace("\n", "\\n")
                 .replace("\r", "\\r")
                 .replace("\t", "\\t");
    }

    /**
     * Shutdown the engine
     */
    public void shutdown() {
        globalContext.clear();
        registeredFunctions.clear();
        enabled = false;
    }
}
