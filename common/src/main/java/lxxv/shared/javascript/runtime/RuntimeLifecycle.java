package lxxv.shared.javascript.runtime;

/**
 * Lifecycle hook for runtime components.
 */
public interface RuntimeLifecycle {
    default void start() {}
    default void stop() {}
}
