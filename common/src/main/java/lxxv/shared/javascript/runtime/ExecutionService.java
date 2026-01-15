package lxxv.shared.javascript.runtime;

import java.util.Map;

import lxxv.shared.javascript.JavaScriptEngine;
import lxxv.shared.javascript.JavaScriptException;

/**
 * Orchestrates transpile + execute using JavaScriptEngine.
 */
public class ExecutionService {
    private final JavaScriptEngine engine;

    public ExecutionService(JavaScriptEngine engine) {
        this.engine = engine;
    }

    public ExecutionResult run(ExecutionRequest request) {
        return engine.execute(request);
    }

    public ExecutionResult transpileAndRun(String code, String filename, Map<String, Object> vars, ExecutionOptions options) {
        try {
            String jsCode = code;
            if (filename != null && (filename.endsWith(".ts") || filename.endsWith(".tsx"))) {
                jsCode = engine.transpile(code, filename);
            }
            ExecutionRequest request = new ExecutionRequest(jsCode, filename, vars, options);
            return engine.execute(request);
        } catch (JavaScriptException e) {
            return ExecutionResult.failure(e.getMessage(), 0);
        }
    }
}
