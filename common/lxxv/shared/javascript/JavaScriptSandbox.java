package lxxv.shared.javascript;

import java.util.HashMap;
import java.util.Map;

/**
 * Safe JavaScript Sandbox
 * Restricts access to certain operations for security
 */
public class JavaScriptSandbox {
    private final JavaScriptEngine engine;
    private final Map<String, Object> allowedGlobals;
    private final boolean allowFileAccess;
    private final boolean allowNetworkAccess;
    private final boolean allowProcessAccess;
    private final long executionTimeout;

    private JavaScriptSandbox(Builder builder) {
        this.engine = JavaScriptEngine.getInstance();
        this.allowedGlobals = new HashMap<>(builder.allowedGlobals);
        this.allowFileAccess = builder.allowFileAccess;
        this.allowNetworkAccess = builder.allowNetworkAccess;
        this.allowProcessAccess = builder.allowProcessAccess;
        this.executionTimeout = builder.executionTimeout;
    }

    /**
     * Execute code safely in sandbox
     */
    public Object execute(String code) throws JavaScriptException {
        if (!validateCode(code)) {
            throw new JavaScriptException("Code contains restricted operations");
        }

        Map<String, Object> variables = new HashMap<>(allowedGlobals);
        
        return engine.execute(code, variables);
    }

    /**
     * Execute code with timeout
     */
    public Object executeWithTimeout(String code) throws JavaScriptException {
        Thread executionThread = new Thread(() -> {
            try {
                execute(code);
            } catch (JavaScriptException e) {
                throw new RuntimeException(e);
            }
        });

        executionThread.start();

        try {
            executionThread.join(executionTimeout);
            if (executionThread.isAlive()) {
                executionThread.interrupt();
                throw new JavaScriptException("Execution timeout exceeded: " + executionTimeout + "ms");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new JavaScriptException("Execution interrupted", e);
        }

        return null;
    }

    /**
     * Validate code before execution
     */
    private boolean validateCode(String code) {
        // Check for dangerous operations
        String[] restrictedPatterns = {
            "require\\s*\\(",
            "import\\s+",
            "eval\\s*\\(",
            "Function\\s*\\(",
            "setTimeout\\s*\\(",
            "setInterval\\s*\\("
        };

        if (!allowFileAccess && code.matches("(?s).*(readFile|writeFile|open|close|fs\\..*|File.*).*")) {
            return false;
        }

        if (!allowNetworkAccess && code.matches("(?s).*(fetch|XMLHttpRequest|WebSocket|http.*).*")) {
            return false;
        }

        if (!allowProcessAccess && code.matches("(?s).*(exec|spawn|fork|process\\..*|child_process.*).*")) {
            return false;
        }

        for (String pattern : restrictedPatterns) {
            if (code.matches("(?s).*" + pattern + ".*")) {
                return false;
            }
        }

        return true;
    }

    /**
     * Builder for JavaScriptSandbox
     */
    public static class Builder {
        private final Map<String, Object> allowedGlobals = new HashMap<>();
        private boolean allowFileAccess = false;
        private boolean allowNetworkAccess = false;
        private boolean allowProcessAccess = false;
        private long executionTimeout = 5000; // 5 seconds

        public Builder allowGlobal(String name, Object value) {
            allowedGlobals.put(name, value);
            return this;
        }

        public Builder allowFileAccess(boolean allow) {
            this.allowFileAccess = allow;
            return this;
        }

        public Builder allowNetworkAccess(boolean allow) {
            this.allowNetworkAccess = allow;
            return this;
        }

        public Builder allowProcessAccess(boolean allow) {
            this.allowProcessAccess = allow;
            return this;
        }

        public Builder executionTimeout(long timeoutMs) {
            this.executionTimeout = timeoutMs;
            return this;
        }

        public JavaScriptSandbox build() {
            return new JavaScriptSandbox(this);
        }
    }
}
