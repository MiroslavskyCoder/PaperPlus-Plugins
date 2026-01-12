package lxxv.shared.javascript;

import java.util.HashMap;
import java.util.Map;

/**
 * JavaScript utilities for common operations
 */
public class JavaScriptUtils {
    private static final JavaScriptEngine engine = JavaScriptEngine.getInstance();

    /**
     * Evaluate simple mathematical expression
     * Example: "2 + 2", "Math.sqrt(16)", "Math.pow(2, 8)"
     */
    public static double math(String expression) throws JavaScriptException {
        return engine.evaluateMath(expression);
    }

    /**
     * Format string using JavaScript template
     */
    public static String format(String template, Map<String, Object> variables) throws JavaScriptException {
        String code = "let template = `" + template + "`; template";
        return engine.execute(code, variables).toString();
    }

    /**
     * Convert object to string using JSON
     */
    public static String stringify(Object obj) throws JavaScriptException {
        return engine.toJSON(obj);
    }

    /**
     * Parse JSON string
     */
    public static Object parse(String json) throws JavaScriptException {
        return engine.parseJSON(json);
    }

    /**
     * Execute condition (returns boolean)
     */
    public static boolean condition(String condition, Map<String, Object> context) throws JavaScriptException {
        Object result = engine.execute(condition, context);
        if (result instanceof Boolean) {
            return (Boolean) result;
        }
        return Boolean.parseBoolean(result.toString());
    }

    /**
     * Transform value using JavaScript function
     */
    public static Object transform(String functionCode, Object value) throws JavaScriptException {
        Map<String, Object> context = new HashMap<>();
        context.put("value", value);
        return engine.execute("(" + functionCode + ")(value)", context);
    }

    /**
     * Filter array using JavaScript condition
     */
    public static Object[] filter(Object[] array, String condition) throws JavaScriptException {
        Map<String, Object> context = new HashMap<>();
        context.put("array", array);
        Object result = engine.execute(
            "array.filter(" + condition + ")",
            context
        );
        if (result instanceof Object[]) {
            return (Object[]) result;
        }
        return new Object[0];
    }

    /**
     * Map array using JavaScript function
     */
    public static Object[] map(Object[] array, String mapFunction) throws JavaScriptException {
        Map<String, Object> context = new HashMap<>();
        context.put("array", array);
        Object result = engine.execute(
            "array.map(" + mapFunction + ")",
            context
        );
        if (result instanceof Object[]) {
            return (Object[]) result;
        }
        return new Object[0];
    }

    /**
     * Reduce array using JavaScript function
     */
    public static Object reduce(Object[] array, String reduceFunction, Object initial) throws JavaScriptException {
        Map<String, Object> context = new HashMap<>();
        context.put("array", array);
        context.put("initial", initial);
        return engine.execute(
            "array.reduce(" + reduceFunction + ", initial)",
            context
        );
    }

    /**
     * Get type of value
     */
    public static String getType(Object value) throws JavaScriptException {
        Map<String, Object> context = new HashMap<>();
        context.put("value", value);
        return engine.execute("typeof value", context).toString();
    }

    /**
     * Check if value is instance of type
     */
    public static boolean isInstanceOf(Object value, String type) throws JavaScriptException {
        Map<String, Object> context = new HashMap<>();
        context.put("value", value);
        Object result = engine.execute("value instanceof " + type, context);
        if (result instanceof Boolean) {
            return (Boolean) result;
        }
        return false;
    }

    /**
     * Validate object against schema (simple JSON schema check)
     */
    public static boolean validateSchema(Object object, String schema) throws JavaScriptException {
        Map<String, Object> context = new HashMap<>();
        context.put("obj", object);
        context.put("schema", schema);
        
        String code = "(" + schema + ").validate ? (" + schema + ").validate(obj) : true";
        Object result = engine.execute(code, context);
        
        if (result instanceof Boolean) {
            return (Boolean) result;
        }
        return true;
    }

    /**
     * Deep clone object
     */
    public static Object deepClone(Object object) throws JavaScriptException {
        Map<String, Object> context = new HashMap<>();
        context.put("obj", object);
        return engine.execute("JSON.parse(JSON.stringify(obj))", context);
    }

    /**
     * Merge objects
     */
    public static Object merge(Object obj1, Object obj2) throws JavaScriptException {
        Map<String, Object> context = new HashMap<>();
        context.put("obj1", obj1);
        context.put("obj2", obj2);
        return engine.execute("Object.assign(obj1, obj2)", context);
    }
}
