package lxxv.shared.javascript.runtime;

import java.util.Collections;
import java.util.Map;

/**
 * Immutable execution request.
 */
public class ExecutionRequest {
    private final String code;
    private final String filename;
    private final Map<String, Object> variables;
    private final ExecutionOptions options;

    public ExecutionRequest(String code, String filename, Map<String, Object> variables, ExecutionOptions options) {
        this.code = code;
        this.filename = filename;
        this.variables = variables == null ? Collections.emptyMap() : Collections.unmodifiableMap(variables);
        this.options = options == null ? ExecutionOptions.defaults() : options;
    }

    public String code() {
        return code;
    }

    public String filename() {
        return filename;
    }

    public Map<String, Object> variables() {
        return variables;
    }

    public ExecutionOptions options() {
        return options;
    }
}
