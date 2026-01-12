package lxxv.shared.javascript;

/**
 * Exception thrown when JavaScript execution fails
 */
public class JavaScriptException extends Exception {
    public JavaScriptException(String message) {
        super(message);
    }

    public JavaScriptException(String message, Throwable cause) {
        super(message, cause);
    }

    public JavaScriptException(Throwable cause) {
        super(cause);
    }
}
