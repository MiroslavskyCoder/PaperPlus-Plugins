package lxxv.shared.javascript.api;

import java.util.Map;

import lxxv.shared.javascript.JavaScriptException;
import lxxv.shared.javascript.JavaScriptEngine;
import lxxv.shared.javascript.runtime.ScriptRegistry;

/**
 * API to store and run registered scripts.
 */
public class ScriptApi {
    private final JavaScriptEngine engine;
    private final ScriptRegistry registry;

    public ScriptApi(JavaScriptEngine engine, ScriptRegistry registry) {
        this.engine = engine;
        this.registry = registry;
    }

    public String register(String description, String code) {
        return registry.add(description, code).id();
    }

    public Object run(String id) throws JavaScriptException {
        String code = registry.get(id);
        if (code == null) {
            throw new JavaScriptException("Script not found: " + id);
        }
        return engine.execute(code);
    }

    public Map<String, String> list() {
        return registry.all();
    }
}
