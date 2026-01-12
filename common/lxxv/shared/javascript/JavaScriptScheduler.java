package lxxv.shared.javascript.advanced;

import java.util.*;
import java.util.concurrent.*;

/**
 * Система планирования для JavaScript.
 * Позволяет выполнять код с задержками и интервалами.
 */
public class JavaScriptScheduler {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
    private final Map<String, ScheduledFuture<?>> tasks = new ConcurrentHashMap<>();
    private final Map<String, Long> taskTimestamps = new ConcurrentHashMap<>();
    private long taskIdCounter = 0;

    /**
     * Выполняет код через указанную задержку
     */
    public String setTimeout(Runnable code, long delayMs) {
        String taskId = "timeout_" + (taskIdCounter++);
        ScheduledFuture<?> future = scheduler.schedule(code, delayMs, TimeUnit.MILLISECONDS);
        tasks.put(taskId, future);
        taskTimestamps.put(taskId, System.currentTimeMillis() + delayMs);
        return taskId;
    }

    /**
     * Выполняет код каждый интервал
     */
    public String setInterval(Runnable code, long intervalMs) {
        String taskId = "interval_" + (taskIdCounter++);
        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(
            code,
            intervalMs,
            intervalMs,
            TimeUnit.MILLISECONDS
        );
        tasks.put(taskId, future);
        taskTimestamps.put(taskId, System.currentTimeMillis() + intervalMs);
        return taskId;
    }

    /**
     * Выполняет код один раз в день в указанное время
     */
    public String scheduleDailyTask(Runnable code, int hour, int minute) {
        Calendar now = Calendar.getInstance();
        Calendar scheduledTime = Calendar.getInstance();
        scheduledTime.set(Calendar.HOUR_OF_DAY, hour);
        scheduledTime.set(Calendar.MINUTE, minute);
        scheduledTime.set(Calendar.SECOND, 0);

        if (scheduledTime.before(now)) {
            scheduledTime.add(Calendar.DAY_OF_MONTH, 1);
        }

        long initialDelay = scheduledTime.getTimeInMillis() - now.getTimeInMillis();
        String taskId = "daily_" + (taskIdCounter++);

        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(
            code,
            initialDelay,
            TimeUnit.DAYS.toMillis(1),
            TimeUnit.MILLISECONDS
        );

        tasks.put(taskId, future);
        taskTimestamps.put(taskId, System.currentTimeMillis() + initialDelay);
        return taskId;
    }

    /**
     * Выполняет код один раз в неделю
     */
    public String scheduleWeeklyTask(Runnable code, int dayOfWeek, int hour, int minute) {
        Calendar now = Calendar.getInstance();
        Calendar scheduledTime = Calendar.getInstance();
        scheduledTime.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        scheduledTime.set(Calendar.HOUR_OF_DAY, hour);
        scheduledTime.set(Calendar.MINUTE, minute);
        scheduledTime.set(Calendar.SECOND, 0);

        if (scheduledTime.before(now)) {
            scheduledTime.add(Calendar.WEEK_OF_YEAR, 1);
        }

        long initialDelay = scheduledTime.getTimeInMillis() - now.getTimeInMillis();
        String taskId = "weekly_" + (taskIdCounter++);

        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(
            code,
            initialDelay,
            TimeUnit.DAYS.toMillis(7),
            TimeUnit.MILLISECONDS
        );

        tasks.put(taskId, future);
        taskTimestamps.put(taskId, System.currentTimeMillis() + initialDelay);
        return taskId;
    }

    /**
     * Отменяет задачу по ID
     */
    public boolean clearTimeout(String taskId) {
        ScheduledFuture<?> future = tasks.remove(taskId);
        taskTimestamps.remove(taskId);
        if (future != null) {
            future.cancel(false);
            return true;
        }
        return false;
    }

    /**
     * Отменяет интервал по ID
     */
    public boolean clearInterval(String taskId) {
        return clearTimeout(taskId);
    }

    /**
     * Получает оставшееся время до выполнения задачи
     */
    public long getRemainingTime(String taskId) {
        Long timestamp = taskTimestamps.get(taskId);
        if (timestamp == null) {
            return -1;
        }
        long remaining = timestamp - System.currentTimeMillis();
        return remaining > 0 ? remaining : 0;
    }

    /**
     * Проверяет активна ли задача
     */
    public boolean isTaskActive(String taskId) {
        ScheduledFuture<?> future = tasks.get(taskId);
        return future != null && !future.isDone();
    }

    /**
     * Возвращает список всех активных задач
     */
    public List<String> getActiveTasks() {
        return new ArrayList<>(tasks.keySet());
    }

    /**
     * Возвращает информацию о задаче
     */
    public Map<String, Object> getTaskInfo(String taskId) {
        ScheduledFuture<?> future = tasks.get(taskId);
        Long timestamp = taskTimestamps.get(taskId);

        if (future == null) {
            return null;
        }

        Map<String, Object> info = new HashMap<>();
        info.put("id", taskId);
        info.put("active", !future.isDone());
        info.put("cancelled", future.isCancelled());
        info.put("remainingTime", getRemainingTime(taskId));
        if (timestamp != null) {
            info.put("scheduledTime", timestamp);
        }
        return info;
    }

    /**
     * Отменяет все задачи
     */
    public void clearAll() {
        for (ScheduledFuture<?> future : tasks.values()) {
            future.cancel(false);
        }
        tasks.clear();
        taskTimestamps.clear();
    }

    /**
     * Завершает работу планировщика
     */
    public void shutdown() {
        clearAll();
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }
}
