package lxxv.shared.javascript;

/**
 * JavaScript Exception
 * Thrown when JavaScript execution fails
 */
public class JavaScriptException extends Exception {
    public JavaScriptException(String message) {
        super(message);
    }

    public JavaScriptException(String message, Throwable cause) {
        super(message, cause);
    }
}
