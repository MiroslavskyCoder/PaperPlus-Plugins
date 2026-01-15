package lxxv.shared.javascript.api;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry for Java helper APIs exposed to JS.
 */
public class ApiRegistry {
    private final Map<String, Object> apis = new ConcurrentHashMap<>();

    public void register(String name, Object api) {
        if (name != null && api != null) {
            apis.put(name, api);
        }
    }

    public Object get(String name) {
        return apis.get(name);
    }

    public Map<String, Object> all() {
        return apis;
    }
}
