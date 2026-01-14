package lxxv.shared.javascript.advanced;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * JavaScript Event System
 * Provides event-driven architecture for JavaScript scripts
 */
public class JavaScriptEventSystem {
    private final Map<String, List<Listener>> eventListeners;
    private final ExecutorService asyncExecutor;

    public JavaScriptEventSystem() {
        this.eventListeners = new ConcurrentHashMap<>();
        this.asyncExecutor = Executors.newCachedThreadPool();
    }

    /**
     * Event listener interface
     */
    @FunctionalInterface
    public interface Listener {
        void onEvent(Object... args) throws Exception;
    }

    /**
     * Register event listener
     */
    public void addEventListener(String eventName, Listener listener) {
        eventListeners.computeIfAbsent(eventName, k -> new CopyOnWriteArrayList<>()).add(listener);
    }

    /**
     * Emit event synchronously
     */
    public void emit(String eventName, Object... args) {
        List<Listener> listeners = eventListeners.get(eventName);
        if (listeners != null) {
            for (Listener listener : listeners) {
                try {
                    listener.onEvent(args);
                } catch (Exception e) {
                    System.err.println("Error in event listener for '" + eventName + "': " + e.getMessage());
                }
            }
        }
    }

    /**
     * Emit event asynchronously
     */
    public CompletableFuture<Void> emitAsync(String eventName, Object... args) {
        return CompletableFuture.runAsync(() -> emit(eventName, args), asyncExecutor);
    }

    /**
     * Emit event and wait for all listeners
     */
    public void emitWait(String eventName, Object... args) throws InterruptedException, ExecutionException {
        List<Listener> listeners = eventListeners.get(eventName);
        if (listeners != null) {
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            for (Listener listener : listeners) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        listener.onEvent(args);
                    } catch (Exception e) {
                        throw new CompletionException(e);
                    }
                }, asyncExecutor);
                futures.add(future);
            }
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
        }
    }

    /**
     * Remove event listener
     */
    public void removeEventListener(String eventName, Listener listener) {
        List<Listener> listeners = eventListeners.get(eventName);
        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    /**
     * Remove all listeners for event
     */
    public void removeAllListeners(String eventName) {
        eventListeners.remove(eventName);
    }

    /**
     * Get listener count for event
     */
    public int getListenerCount(String eventName) {
        List<Listener> listeners = eventListeners.get(eventName);
        return listeners != null ? listeners.size() : 0;
    }

    /**
     * Get all registered event names
     */
    public Set<String> getRegisteredEvents() {
        return new HashSet<>(eventListeners.keySet());
    }

    /**
     * Shutdown event system
     */
    public void shutdown() {
        asyncExecutor.shutdown();
        try {
            if (!asyncExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                asyncExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            asyncExecutor.shutdownNow();
        }
        eventListeners.clear();
    }
}
