package lxxv.shared.javascript;

/**
 * Custom JavaScript function interface
 * Implement this to create Java functions callable from JavaScript
 */
@FunctionalInterface
public interface JavaScriptFunction {
    /**
     * Execute the function with given arguments
     */
    Object call(Object... args) throws Exception;
}
