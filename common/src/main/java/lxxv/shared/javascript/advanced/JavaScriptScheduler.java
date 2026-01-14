package lxxv.shared.javascript.advanced;

import java.time.LocalTime;
import java.time.DayOfWeek;
import java.util.*;
import java.util.concurrent.*;

/**
 * JavaScript Scheduler
 * Provides setTimeout, setInterval, and advanced scheduling capabilities
 */
public class JavaScriptScheduler {
    private final ScheduledExecutorService scheduler;
    private final Map<String, ScheduledFuture<?>> scheduledTasks;
    private final Map<String, TaskInfo> taskInfo;

    public JavaScriptScheduler() {
        this.scheduler = Executors.newScheduledThreadPool(4);
        this.scheduledTasks = new ConcurrentHashMap<>();
        this.taskInfo = new ConcurrentHashMap<>();
    }

    /**
     * Task information
     */
    public static class TaskInfo {
        public final String id;
        public final String type;
        public final long delay;
        public final long createdAt;

        public TaskInfo(String id, String type, long delay) {
            this.id = id;
            this.type = type;
            this.delay = delay;
            this.createdAt = System.currentTimeMillis();
        }
    }

    /**
     * Schedule task after delay (setTimeout)
     */
    public String setTimeout(Runnable task, long delayMs) {
        String taskId = UUID.randomUUID().toString();
        ScheduledFuture<?> future = scheduler.schedule(() -> {
            try {
                task.run();
            } finally {
                scheduledTasks.remove(taskId);
                taskInfo.remove(taskId);
            }
        }, delayMs, TimeUnit.MILLISECONDS);
        
        scheduledTasks.put(taskId, future);
        taskInfo.put(taskId, new TaskInfo(taskId, "timeout", delayMs));
        return taskId;
    }

    /**
     * Schedule repeating task (setInterval)
     */
    public String setInterval(Runnable task, long intervalMs) {
        String taskId = UUID.randomUUID().toString();
        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(
            task, intervalMs, intervalMs, TimeUnit.MILLISECONDS);
        
        scheduledTasks.put(taskId, future);
        taskInfo.put(taskId, new TaskInfo(taskId, "interval", intervalMs));
        return taskId;
    }

    /**
     * Schedule daily task at specific time
     */
    public String scheduleDailyTask(Runnable task, int hour, int minute) {
        LocalTime now = LocalTime.now();
        LocalTime scheduledTime = LocalTime.of(hour, minute);
        
        long initialDelay = calculateInitialDelay(now, scheduledTime);
        long period = TimeUnit.DAYS.toMillis(1);
        
        String taskId = UUID.randomUUID().toString();
        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(
            task, initialDelay, period, TimeUnit.MILLISECONDS);
        
        scheduledTasks.put(taskId, future);
        taskInfo.put(taskId, new TaskInfo(taskId, "daily", period));
        return taskId;
    }

    /**
     * Schedule weekly task
     */
    public String scheduleWeeklyTask(Runnable task, DayOfWeek dayOfWeek, int hour, int minute) {
        long initialDelay = calculateWeeklyDelay(dayOfWeek, hour, minute);
        long period = TimeUnit.DAYS.toMillis(7);
        
        String taskId = UUID.randomUUID().toString();
        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(
            task, initialDelay, period, TimeUnit.MILLISECONDS);
        
        scheduledTasks.put(taskId, future);
        taskInfo.put(taskId, new TaskInfo(taskId, "weekly", period));
        return taskId;
    }

    /**
     * Clear timeout/interval
     */
    public boolean clearTimeout(String taskId) {
        ScheduledFuture<?> future = scheduledTasks.remove(taskId);
        taskInfo.remove(taskId);
        if (future != null) {
            future.cancel(false);
            return true;
        }
        return false;
    }

    /**
     * Clear interval (alias for clearTimeout)
     */
    public boolean clearInterval(String taskId) {
        return clearTimeout(taskId);
    }

    /**
     * Get remaining time until task execution
     */
    public long getRemainingTime(String taskId) {
        ScheduledFuture<?> future = scheduledTasks.get(taskId);
        if (future != null) {
            return future.getDelay(TimeUnit.MILLISECONDS);
        }
        return -1;
    }

    /**
     * Get all active tasks
     */
    public Map<String, TaskInfo> getActiveTasks() {
        return new HashMap<>(taskInfo);
    }

    /**
     * Calculate initial delay for daily task
     */
    private long calculateInitialDelay(LocalTime now, LocalTime scheduledTime) {
        long nowMs = now.toNanoOfDay() / 1_000_000;
        long scheduledMs = scheduledTime.toNanoOfDay() / 1_000_000;
        
        if (scheduledMs > nowMs) {
            return scheduledMs - nowMs;
        } else {
            return TimeUnit.DAYS.toMillis(1) - (nowMs - scheduledMs);
        }
    }

    /**
     * Calculate initial delay for weekly task
     */
    private long calculateWeeklyDelay(DayOfWeek dayOfWeek, int hour, int minute) {
        // Simplified calculation
        LocalTime scheduledTime = LocalTime.of(hour, minute);
        long dailyDelay = calculateInitialDelay(LocalTime.now(), scheduledTime);
        
        // Add days until target day of week
        int currentDay = java.time.LocalDate.now().getDayOfWeek().getValue();
        int targetDay = dayOfWeek.getValue();
        int daysUntil = (targetDay - currentDay + 7) % 7;
        
        return dailyDelay + TimeUnit.DAYS.toMillis(daysUntil);
    }

    /**
     * Shutdown scheduler
     */
    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
        scheduledTasks.clear();
        taskInfo.clear();
    }
}
