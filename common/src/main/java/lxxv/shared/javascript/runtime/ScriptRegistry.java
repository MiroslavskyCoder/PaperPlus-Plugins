package lxxv.shared.javascript.runtime;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import lxxv.shared.javascript.bridge.V8ScriptHandle;

/**
 * Registry for named/identified scripts.
 */
public class ScriptRegistry {
    private final Map<String, String> scripts = new ConcurrentHashMap<>();

    public V8ScriptHandle add(String description, String code) {
        String id = UUID.randomUUID().toString();
        scripts.put(id, code);
        return new V8ScriptHandle(id, description);
    }

    public String get(String id) {
        return scripts.get(id);
    }

    public void remove(String id) {
        scripts.remove(id);
    }

    public Map<String, String> all() {
        return scripts;
    }
}
