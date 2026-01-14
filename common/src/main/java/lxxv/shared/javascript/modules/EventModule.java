package lxxv.shared.javascript.modules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import lxxv.shared.javascript.JavaScriptEngine;
import lxxv.shared.javascript.JavaScriptException;
import lxxv.shared.javascript.JavaScriptFunction;
import lxxv.shared.javascript.advanced.JavaScriptEventSystem;

/**
 * EventModule bridges the shared JavaScript event bus between Java and V8 scripts.
 * It exposes an EventEmitter-like API (on, once, off, emit, length, etc.) to scripts
 * while delegating the actual dispatching work to {@link JavaScriptEventSystem}.
 */
public class EventModule {
    private static final Logger LOGGER = Logger.getLogger(EventModule.class.getName());

    private static final String REGISTER_FN = "__EventBridge_register";
    private static final String REMOVE_FN = "__EventBridge_remove";
    private static final String EMIT_FN = "__EventBridge_emit";
    private static final String LENGTH_FN = "__EventBridge_length";
    private static final String CLEAR_FN = "__EventBridge_clear";
    private static final String CALLBACK_ID_CTX = "__eventBridgeCallbackId";
    private static final String ARGS_CTX = "__eventBridgeArgs";

    private static final String BOOTSTRAP_SCRIPT = """
        (function bootstrapEventModule(){
            const g = typeof globalThis !== 'undefined' ? globalThis : this;
            if (g.EventModule) {
                return;
            }
            if (typeof g.__EventBridge_register !== 'function') {
                throw new Error('EventModule bridge is not ready');
            }
            const callbacks = new Map();
            let counter = 0;
            const ensureEventName = (eventName) => {
                if (typeof eventName !== 'string' || eventName.trim().length === 0) {
                    throw new TypeError('Event name must be a non-empty string');
                }
                return eventName;
            };
            const ensureHandler = (handler) => {
                if (typeof handler !== 'function') {
                    throw new TypeError('Event handler must be a function');
                }
                return handler;
            };
            const createId = () => 'evt_' + Date.now().toString(36) + '_' + (++counter);
            const register = (eventName, handler, options) => {
                const name = ensureEventName(eventName);
                const fn = ensureHandler(handler);
                const opts = options || {};
                const id = createId();
                callbacks.set(id, { handler: fn, event: name, once: !!opts.once });
                g.__EventBridge_register(name, id, !!opts.once);
                return id;
            };
            const on = (eventName, handler, options) => register(eventName, handler, options);
            const once = (eventName, handler) => register(eventName, handler, { once: true });
            const off = (eventName, handlerOrId) => {
                ensureEventName(eventName);
                if (!handlerOrId) {
                    return false;
                }
                const ids = [];
                if (typeof handlerOrId === 'string') {
                    ids.push(handlerOrId);
                } else if (typeof handlerOrId === 'function') {
                    for (const [id, meta] of callbacks.entries()) {
                        if (meta.event === eventName && meta.handler === handlerOrId) {
                            ids.push(id);
                        }
                    }
                }
                if (!ids.length) {
                    return false;
                }
                ids.forEach((id) => {
                    callbacks.delete(id);
                    g.__EventBridge_remove(eventName, id);
                });
                return true;
            };
            const emit = (eventName, ...args) => {
                ensureEventName(eventName);
                return g.__EventBridge_emit(eventName, ...args) || 0;
            };
            const emitAsync = (eventName, ...args) => Promise.resolve(emit(eventName, ...args));
            const length = (eventName) => g.__EventBridge_length(ensureEventName(eventName));
            const listeners = (eventName) => {
                ensureEventName(eventName);
                return Array.from(callbacks.entries())
                    .filter(([, meta]) => meta.event === eventName)
                    .map(([id, meta]) => ({ id, once: !!meta.once }));
            };
            const events = () => {
                const names = new Set();
                for (const meta of callbacks.values()) {
                    names.add(meta.event);
                }
                return Array.from(names);
            };
            const clear = (eventName) => {
                if (!eventName) {
                    callbacks.clear();
                    g.__EventBridge_clear();
                    return true;
                }
                ensureEventName(eventName);
                for (const [id, meta] of callbacks.entries()) {
                    if (meta.event === eventName) {
                        callbacks.delete(id);
                        g.__EventBridge_remove(meta.event, id);
                    }
                }
                return true;
            };
            g.__eventModule = { callbacks };
            g.EventModule = Object.freeze({
                on,
                once,
                off,
                emit,
                emitAsync,
                length,
                listeners,
                events,
                clear
            });
            g.addEventListener = on;
            g.removeEventListener = off;
            g.onceEventListener = once;
        })();
        """;

    private static final String INVOKE_SCRIPT = """
        (function(cbId, payload){
            const store = globalThis.__eventModule && globalThis.__eventModule.callbacks;
            if (!store) {
                return;
            }
            const entry = store.get(cbId);
            if (!entry || typeof entry.handler !== 'function') {
                return;
            }
            try {
                entry.handler(...(payload || []));
                if (entry.once) {
                    store.delete(cbId);
                }
            } catch (error) {
                if (globalThis.console && typeof globalThis.console.error === 'function') {
                    console.error('[EventModule] Listener failed for ' + cbId, error);
                }
            }
        })(__eventBridgeCallbackId, __eventBridgeArgs);
        """;

    private static final String CLEANUP_SCRIPT = """
        (function(cbId){
            const store = globalThis.__eventModule && globalThis.__eventModule.callbacks;
            if (!store) {
                return;
            }
            store.delete(cbId);
        })(__eventBridgeCallbackId);
        """;

    private final JavaScriptEngine engine;
    private final JavaScriptEventSystem eventSystem;
    private final Map<String, ListenerBinding> bindings = new ConcurrentHashMap<>();
    private final AtomicBoolean initialized = new AtomicBoolean(false);

    public EventModule(JavaScriptEngine engine, JavaScriptEventSystem eventSystem) {
        this.engine = Objects.requireNonNull(engine, "engine");
        this.eventSystem = Objects.requireNonNull(eventSystem, "eventSystem");
    }

    /**
     * @return true if the module has already been registered in the V8 runtime.
     */
    public boolean isInitialized() {
        return initialized.get();
    }

    /**
     * Registers the JS bridge (idempotent).
     */
    public void register() throws JavaScriptException {
        if (initialized.get()) {
            return;
        }
        synchronized (this) {
            if (initialized.get()) {
                return;
            }
            registerBridgeFunctions();
            engine.execute(BOOTSTRAP_SCRIPT);
            initialized.set(true);
            LOGGER.info("EventModule bridge registered in V8 runtime");
        }
    }

    /**
     * Emit an event from Java.
     */
    public int emit(String eventName, Object... args) {
        String normalized = normalizeName(eventName);
        eventSystem.emit(normalized, args);
        return eventSystem.getListenerCount(normalized);
    }

    /**
     * Remove all listeners registered through this module.
     */
    public void clearAll() {
        clearBindings(null, true);
    }

    /**
     * Remove all listeners for a specific event.
     */
    public void clear(String eventName) {
        clearBindings(normalizeName(eventName), true);
    }

    public int listenerCount(String eventName) {
        return eventSystem.getListenerCount(normalizeName(eventName));
    }

    private void registerBridgeFunctions() {
        engine.registerFunction(REGISTER_FN, registerFunction(args -> {
            String eventName = normalizeName(safeName(args, 0));
            String callbackId = toNonEmptyString(args[1], "callbackId");
            boolean once = args.length > 2 && Boolean.parseBoolean(String.valueOf(args[2]));
            registerBinding(eventName, callbackId, once);
            return callbackId;
        }));

        engine.registerFunction(REMOVE_FN, registerFunction(args -> {
            String eventName = normalizeName(safeName(args, 0));
            String callbackId = toNonEmptyString(args[1], "callbackId");
            return removeBinding(eventName, callbackId, false);
        }));

        engine.registerFunction(EMIT_FN, registerFunction(args -> {
            String eventName = normalizeName(safeName(args, 0));
            Object[] payload = args.length > 1 ? Arrays.copyOfRange(args, 1, args.length) : new Object[0];
            eventSystem.emit(eventName, payload);
            return eventSystem.getListenerCount(eventName);
        }));

        engine.registerFunction(LENGTH_FN, registerFunction(args -> {
            String eventName = normalizeName(safeName(args, 0));
            return eventSystem.getListenerCount(eventName);
        }));

        engine.registerFunction(CLEAR_FN, registerFunction(args -> {
            String target = args.length == 0 || args[0] == null ? null : normalizeName(safeName(args, 0));
            clearBindings(target, false);
            return true;
        }));
    }

    private String safeName(Object[] args, int idx) {
        if (args == null || args.length <= idx || args[idx] == null) return "";
        return args[idx].toString();
    }

    private JavaScriptFunction registerFunction(JavaScriptFunction function) {
        return args -> {
            try {
                return function.call(args);
            } catch (Exception ex) {
                LOGGER.log(Level.WARNING, "EventModule bridge error: " + ex.getMessage(), ex);
                throw ex;
            }
        };
    }

    private void registerBinding(String eventName, String callbackId, boolean once) {
        String key = buildKey(eventName, callbackId);
        ListenerBinding existing = bindings.remove(key);
        if (existing != null) {
            eventSystem.removeEventListener(eventName, existing.listener);
        }
        JavaScriptEventSystem.Listener listener = payload -> {
            invokeCallback(callbackId, payload);
            if (once) {
                removeBinding(eventName, callbackId, true);
            }
        };
        bindings.put(key, new ListenerBinding(eventName, callbackId, once, listener));
        eventSystem.addEventListener(eventName, listener);
    }

    private boolean removeBinding(String eventName, String callbackId, boolean cleanupJs) {
        String key = buildKey(eventName, callbackId);
        ListenerBinding binding = bindings.remove(key);
        if (binding == null) {
            return false;
        }
        eventSystem.removeEventListener(eventName, binding.listener);
        if (cleanupJs) {
            cleanupCallback(callbackId);
        }
        return true;
    }

    private void clearBindings(String eventName, boolean cleanupJs) {
        List<String> keysToRemove = new ArrayList<>();
        for (Map.Entry<String, ListenerBinding> entry : bindings.entrySet()) {
            ListenerBinding binding = entry.getValue();
            boolean matches = eventName == null || binding.eventName.equals(eventName);
            if (matches) {
                eventSystem.removeEventListener(binding.eventName, binding.listener);
                keysToRemove.add(entry.getKey());
                if (cleanupJs) {
                    cleanupCallback(binding.callbackId);
                }
            }
        }
        for (String key : keysToRemove) {
            bindings.remove(key);
        }
    }

    private void invokeCallback(String callbackId, Object[] args) {
        if (!initialized.get()) {
            return;
        }
        Map<String, Object> context = new ConcurrentHashMap<>();
        context.put(CALLBACK_ID_CTX, callbackId);
        context.put(ARGS_CTX, args != null ? args : new Object[0]);
        try {
            engine.execute(INVOKE_SCRIPT, context);
        } catch (JavaScriptException e) {
            LOGGER.log(Level.WARNING, "Failed to invoke event listener '" + callbackId + "': " + e.getMessage(), e);
        }
    }

    private void cleanupCallback(String callbackId) {
        Map<String, Object> context = new ConcurrentHashMap<>();
        context.put(CALLBACK_ID_CTX, callbackId);
        try {
            engine.execute(CLEANUP_SCRIPT, context);
        } catch (JavaScriptException e) {
            LOGGER.log(Level.FINE, "Failed to cleanup callback '" + callbackId + "': " + e.getMessage(), e);
        }
    }

    private String normalizeName(String eventName) {
        String value = Objects.requireNonNull(eventName, "eventName").trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException("Event name must not be empty");
        }
        return value;
    }

    private String toNonEmptyString(Object value, String label) {
        if (value == null) {
            throw new IllegalArgumentException(label + " must not be null");
        }
        String text = value.toString().trim();
        if (text.isEmpty()) {
            throw new IllegalArgumentException(label + " must not be empty");
        }
        return text;
    }

    private String buildKey(String eventName, String callbackId) {
        return eventName + "#" + callbackId;
    }

    private record ListenerBinding(String eventName,
                                   String callbackId,
                                   boolean once,
                                   JavaScriptEventSystem.Listener listener) { }
}
