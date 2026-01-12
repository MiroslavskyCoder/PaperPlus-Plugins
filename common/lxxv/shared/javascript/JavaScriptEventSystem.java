package lxxv.shared.javascript.advanced;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * Система событий для JavaScript движка.
 * Позволяет регистрировать и запускать события из JavaScript кода.
 */
public class JavaScriptEventSystem {
    private final Map<String, List<JavaScriptEventListener>> listeners = new ConcurrentHashMap<>();
    private final ExecutorService eventExecutor = Executors.newFixedThreadPool(4);
    private final Map<String, Object> eventMetadata = new ConcurrentHashMap<>();

    /**
     * Регистрирует слушателя события
     */
    public void addEventListener(String eventName, JavaScriptEventListener listener) {
        listeners.computeIfAbsent(eventName, k -> new CopyOnWriteArrayList<>()).add(listener);
    }

    /**
     * Удаляет слушателя события
     */
    public void removeEventListener(String eventName, JavaScriptEventListener listener) {
        List<JavaScriptEventListener> list = listeners.get(eventName);
        if (list != null) {
            list.remove(listener);
        }
    }

    /**
     * Удаляет всех слушателей события
     */
    public void removeAllListeners(String eventName) {
        listeners.remove(eventName);
    }

    /**
     * Запускает событие синхронно
     */
    public void emit(String eventName, Object... args) {
        List<JavaScriptEventListener> list = listeners.get(eventName);
        if (list != null) {
            for (JavaScriptEventListener listener : list) {
                try {
                    listener.onEvent(new JavaScriptEvent(eventName, args, eventMetadata));
                } catch (Exception e) {
                    System.err.println("Error in event listener for '" + eventName + "': " + e.getMessage());
                }
            }
        }
    }

    /**
     * Запускает событие асинхронно
     */
    public void emitAsync(String eventName, Object... args) {
        eventExecutor.execute(() -> emit(eventName, args));
    }

    /**
     * Запускает событие с ожиданием завершения всех слушателей
     */
    public void emitWait(String eventName, Object... args) {
        List<JavaScriptEventListener> list = listeners.get(eventName);
        if (list != null) {
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            for (JavaScriptEventListener listener : list) {
                futures.add(CompletableFuture.runAsync(() -> {
                    try {
                        listener.onEvent(new JavaScriptEvent(eventName, args, eventMetadata));
                    } catch (Exception e) {
                        System.err.println("Error in event listener for '" + eventName + "': " + e.getMessage());
                    }
                }, eventExecutor));
            }
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        }
    }

    /**
     * Устанавливает метаданные события
     */
    public void setEventMetadata(String key, Object value) {
        eventMetadata.put(key, value);
    }

    /**
     * Получает метаданные события
     */
    public Object getEventMetadata(String key) {
        return eventMetadata.get(key);
    }

    /**
     * Возвращает количество слушателей события
     */
    public int getListenerCount(String eventName) {
        List<JavaScriptEventListener> list = listeners.get(eventName);
        return list != null ? list.size() : 0;
    }

    /**
     * Возвращает список всех событий с слушателями
     */
    public List<String> getRegisteredEvents() {
        return new ArrayList<>(listeners.keySet());
    }

    /**
     * Очищает все события и слушателей
     */
    public void clear() {
        listeners.clear();
        eventMetadata.clear();
    }

    /**
     * Завершает работу системы
     */
    public void shutdown() {
        eventExecutor.shutdown();
        try {
            if (!eventExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                eventExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            eventExecutor.shutdownNow();
        }
    }

    /**
     * Функциональный интерфейс для слушателя события
     */
    @FunctionalInterface
    public interface JavaScriptEventListener {
        void onEvent(JavaScriptEvent event) throws Exception;
    }

    /**
     * Класс события
     */
    public static class JavaScriptEvent {
        private final String name;
        private final Object[] args;
        private final Map<String, Object> metadata;
        private final long timestamp;

        public JavaScriptEvent(String name, Object[] args, Map<String, Object> metadata) {
            this.name = name;
            this.args = args;
            this.metadata = new HashMap<>(metadata);
            this.timestamp = System.currentTimeMillis();
        }

        public String getName() {
            return name;
        }

        public Object[] getArgs() {
            return args;
        }

        public Object getArg(int index) {
            return index >= 0 && index < args.length ? args[index] : null;
        }

        public int getArgCount() {
            return args.length;
        }

        public Map<String, Object> getMetadata() {
            return metadata;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}
