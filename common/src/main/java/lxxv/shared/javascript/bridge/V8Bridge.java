package lxxv.shared.javascript.bridge;

import java.util.Map;

import lxxv.shared.javascript.JavaScriptEngine;
import lxxv.shared.javascript.JavaScriptException;
import lxxv.shared.javascript.runtime.ExecutionRequest;

/**
 * Bridge helper to execute code through JavaScriptEngine with a consistent API.
 */
public class V8Bridge {
    private final JavaScriptEngine engine;

    public V8Bridge(JavaScriptEngine engine) {
        this.engine = engine;
    }

    public Object execute(String code) throws JavaScriptException {
        return engine.execute(code);
    }

    public Object execute(String code, Map<String, Object> vars) throws JavaScriptException {
        return engine.execute(code, vars);
    }

    public Object execute(ExecutionRequest request) throws JavaScriptException {
        return engine.execute(request.code(), request.variables());
    }
}
