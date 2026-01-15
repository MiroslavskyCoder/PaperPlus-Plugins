package lxxv.shared.javascript.modules;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8ValueFunction;

import lxxv.shared.javascript.JavaScriptEngine;
import lxxv.shared.javascript.JavaScriptException;
import lxxv.shared.javascript.JavaScriptFunction;
import lxxv.shared.javascript.advanced.JavaScriptEventSystem;

/**
 * Native EventModule backed by JavaScriptEventSystem; exposed via requireNativeModule("events").
 */
public class EventModule {
    private static final Logger LOGGER = Logger.getLogger(EventModule.class.getName());

    private final JavaScriptEngine engine;
    private final JavaScriptEventSystem eventSystem;
    private final Map<String, ListenerBinding> bindings = new ConcurrentHashMap<>();

    public EventModule(JavaScriptEngine engine, JavaScriptEventSystem eventSystem) {
        this.engine = Objects.requireNonNull(engine, "engine");
        this.eventSystem = Objects.requireNonNull(eventSystem, "eventSystem");
    }

    /** Register native event helpers as module `events`. */
    public void register() throws JavaScriptException {
        engine.beginModuleRegistration("events");
        try {
            registerNativeApi();
            LOGGER.info("EventModule native API registered (events)");
        } finally {
            engine.endModuleRegistration();
        }
    }

    /** Emit an event from Java. */
    public int emit(String eventName, Object... args) {
        String normalized = normalizeName(eventName);
        eventSystem.emit(normalized, args);
        return eventSystem.getListenerCount(normalized);
    }

    /** Remove all listeners. */
    public void clearAll() {
        clearBindings(null);
    }

    /** Remove listeners for a specific event. */
    public void clear(String eventName) {
        clearBindings(normalizeName(eventName));
    }

    public int listenerCount(String eventName) {
        return eventSystem.getListenerCount(normalizeName(eventName));
    }

    private void registerNativeApi() {
        engine.registerFunction("on", registerFunction(args -> registerCallback(args, false)));
        engine.registerFunction("once", registerFunction(args -> registerCallback(args, true)));
        engine.registerFunction("off", registerFunction(this::unregisterCallback));
        engine.registerFunction("emit", registerFunction(args -> emitFromJs(args, false)));
        engine.registerFunction("emitAsync", registerFunction(args -> emitFromJs(args, true)));
        engine.registerFunction("length", registerFunction(args -> eventSystem.getListenerCount(normalizeName(safeName(args, 0)))));
        engine.registerFunction("listeners", registerFunction(this::listListeners));
        engine.registerFunction("events", registerFunction(args -> eventSystem.getRegisteredEvents().toArray(new String[0])));
        engine.registerFunction("clear", registerFunction(this::clearFromJs));
    }

    private String safeName(Object[] args, int idx) {
        if (args == null || args.length <= idx || args[idx] == null) return "";
        return args[idx].toString();
    }

    private Object registerCallback(Object[] args, boolean once) {
        String eventName = normalizeName(safeName(args, 0));
        if (args.length < 2 || !(args[1] instanceof V8ValueFunction)) {
            throw new IllegalArgumentException("handler must be a function");
        }
        V8ValueFunction fn = (V8ValueFunction) args[1];
        String id = nextId();
        JavaScriptEventSystem.Listener listener = payload -> invoke(fn, payload, once, eventName, id);
        ListenerBinding existing = bindings.remove(buildKey(eventName, id));
        if (existing != null) {
            eventSystem.removeEventListener(eventName, existing.listener);
        }
        bindings.put(buildKey(eventName, id), new ListenerBinding(eventName, id, once, listener, fn));
        eventSystem.addEventListener(eventName, listener);
        return id;
    }

    private Object unregisterCallback(Object[] args) {
        if (args.length < 2) return false;
        String eventName = normalizeName(safeName(args, 0));
        Object target = args[1];
        List<String> removed = new ArrayList<>();
        for (Map.Entry<String, ListenerBinding> entry : bindings.entrySet()) {
            ListenerBinding binding = entry.getValue();
            if (!binding.eventName.equals(eventName)) continue;
            boolean match = (target instanceof String && binding.callbackId.equals(target.toString())) ||
                (target instanceof V8ValueFunction && binding.function == target);
            if (match) {
                removed.add(entry.getKey());
                eventSystem.removeEventListener(binding.eventName, binding.listener);
            }
        }
        removed.forEach(bindings::remove);
        return !removed.isEmpty();
    }

    private Object emitFromJs(Object[] args, boolean async) {
        String eventName = normalizeName(safeName(args, 0));
        Object[] payload = args.length > 1 ? java.util.Arrays.copyOfRange(args, 1, args.length) : new Object[0];
        if (async) {
            eventSystem.emitAsync(eventName, payload);
        } else {
            eventSystem.emit(eventName, payload);
        }
        return eventSystem.getListenerCount(eventName);
    }

    private Object listListeners(Object[] args) {
        String eventName = normalizeName(safeName(args, 0));
        List<Map<String, Object>> result = new ArrayList<>();
        for (ListenerBinding binding : bindings.values()) {
            if (binding.eventName.equals(eventName)) {
                result.add(Map.of("id", binding.callbackId, "once", binding.once));
            }
        }
        return result.toArray(new Map[0]);
    }

    private Object clearFromJs(Object[] args) {
        String target = args.length == 0 || args[0] == null ? null : normalizeName(safeName(args, 0));
        clearBindings(target);
        return true;
    }

    private void invoke(V8ValueFunction fn, Object[] payload, boolean once, String eventName, String callbackId) {
        try {
            fn.callVoid((V8Value) null, payload);
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Event listener failed: " + ex.getMessage(), ex);
        } finally {
            if (once) {
                clearBinding(eventName, callbackId);
            }
        }
    }

    private void clearBinding(String eventName, String callbackId) {
        String key = buildKey(eventName, callbackId);
        ListenerBinding binding = bindings.remove(key);
        if (binding != null) {
            eventSystem.removeEventListener(binding.eventName, binding.listener);
        }
    }

    private void clearBindings(String eventName) {
        List<String> keys = new ArrayList<>();
        for (String key : bindings.keySet()) {
            if (eventName == null || key.startsWith(eventName + "#")) {
                keys.add(key);
            }
        }
        for (String key : keys) {
            ListenerBinding binding = bindings.remove(key);
            if (binding != null) {
                eventSystem.removeEventListener(binding.eventName, binding.listener);
            }
        }
    }

    private String nextId() {
        return "evt_" + Long.toString(System.currentTimeMillis(), 36) + "_" + Integer.toHexString(bindings.size() + 1);
    }

    private String buildKey(String eventName, String callbackId) {
        return eventName + "#" + callbackId;
    }

    private JavaScriptFunction registerFunction(JavaScriptFunction function) {
        return args -> {
            try {
                return function.call(args);
            } catch (Exception ex) {
                LOGGER.log(Level.WARNING, "EventModule error: " + ex.getMessage(), ex);
                throw ex;
            }
        };
    }

    private String normalizeName(String eventName) {
        String value = Objects.requireNonNull(eventName, "eventName").trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException("Event name must not be empty");
        }
        return value;
    }

    private static class ListenerBinding {
        final String eventName;
        final String callbackId;
        final boolean once;
        final JavaScriptEventSystem.Listener listener;
        final V8ValueFunction function;

        ListenerBinding(String eventName, String callbackId, boolean once, JavaScriptEventSystem.Listener listener, V8ValueFunction function) {
            this.eventName = eventName;
            this.callbackId = callbackId;
            this.once = once;
            this.listener = listener;
            this.function = function;
        }
    }
}
