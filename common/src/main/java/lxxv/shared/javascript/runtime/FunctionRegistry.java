package lxxv.shared.javascript.runtime;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lxxv.shared.javascript.JavaScriptFunction;

/**
 * Central registry for JS/TS callable functions exposed on the `server` object.
 */
public class FunctionRegistry {
    private final Map<String, JavaScriptFunction> functions = new ConcurrentHashMap<>();
    private final ServerContext serverContext = new ServerContext();

    public void register(String name, JavaScriptFunction function) {
        if (name == null || function == null) {
            return;
        }
        functions.put(name, function);
        serverContext.setFunction(name, function);
    }

    public JavaScriptFunction get(String name) {
        return functions.get(name);
    }

    public Map<String, JavaScriptFunction> getAll() {
        return functions;
    }

    public void setValue(String name, Object value) {
        serverContext.setValue(name, value);
    }

    public Object getValue(String name) {
        return serverContext.get(name);
    }

    public Map<String, Object> getServerBindings() {
        return serverContext.asMutableMap();
    }

    public void clear() {
        functions.clear();
        serverContext.clear();
    }
}
