package lxxv.shared.javascript.runtime;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Holds functions and values exposed to JS under the global `server` object.
 */
public class ServerContext {
    private final Map<String, Object> bindings = new ConcurrentHashMap<>();

    public void setFunction(String name, Object fn) {
        if (name != null && fn != null) {
            bindings.put(name, fn);
        }
    }

    public void setValue(String name, Object value) {
        if (name != null && value != null) {
            bindings.put(name, value);
        }
    }

    public Object get(String name) {
        return bindings.get(name);
    }

    public Map<String, Object> asMutableMap() {
        return bindings;
    }

    public Map<String, Object> asReadOnlyView() {
        return Collections.unmodifiableMap(bindings);
    }

    public void clear() {
        bindings.clear();
    }
}
