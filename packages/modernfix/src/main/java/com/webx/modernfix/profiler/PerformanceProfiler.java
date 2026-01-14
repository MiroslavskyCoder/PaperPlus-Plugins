package com.webx.modernfix.profiler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Performance profiler inspired by SparkLaunchProfiler
 * Tracks operation timings and provides detailed statistics
 */
public class PerformanceProfiler {
    private final Map<String, ProfilerEntry> entries = new ConcurrentHashMap<>();
    private final AtomicLong totalMeasurements = new AtomicLong(0);
    
    /**
     * Start profiling an operation
     * @param operationName Operation name
     * @return Start time in nanoseconds
     */
    public long start(String operationName) {
        return System.nanoTime();
    }
    
    /**
     * Stop profiling and record measurement
     * @param operationName Operation name
     * @param startTime Start time from start() method
     * @param itemsProcessed Number of items processed (optional, use 0 if N/A)
     */
    public void stop(String operationName, long startTime, int itemsProcessed) {
        long duration = (System.nanoTime() - startTime) / 1_000_000; // ms
        record(operationName, duration, itemsProcessed);
    }
    
    /**
     * Record a measurement manually
     * @param operationName Operation name
     * @param durationMs Duration in milliseconds
     * @param itemsProcessed Number of items processed
     */
    public void record(String operationName, long durationMs, int itemsProcessed) {
        entries.computeIfAbsent(operationName, k -> new ProfilerEntry(k))
               .addMeasurement(durationMs, itemsProcessed);
        totalMeasurements.incrementAndGet();
    }
    
    /**
     * Get profiler entry for an operation
     */
    public ProfilerEntry getEntry(String operationName) {
        return entries.get(operationName);
    }
    
    /**
     * Get all profiler entries
     */
    public Map<String, ProfilerEntry> getAllEntries() {
        return new ConcurrentHashMap<>(entries);
    }
    
    /**
     * Reset all statistics
     */
    public void reset() {
        entries.clear();
        totalMeasurements.set(0);
    }
    
    /**
     * Reset specific operation statistics
     */
    public void reset(String operationName) {
        entries.remove(operationName);
    }
    
    /**
     * Get total number of measurements
     */
    public long getTotalMeasurements() {
        return totalMeasurements.get();
    }
    
    /**
     * Get profiling summary
     */
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Performance Profile ===\n");
        sb.append(String.format("Total measurements: %d\n", totalMeasurements.get()));
        sb.append("\nOperations:\n");
        
        entries.values().stream()
            .sorted((a, b) -> Long.compare(b.getTotalDuration(), a.getTotalDuration()))
            .forEach(entry -> sb.append("  ").append(entry.toString()).append("\n"));
        
        return sb.toString();
    }
    
    /**
     * Profiler entry for a single operation
     */
    public static class ProfilerEntry {
        private final String operationName;
        private final AtomicLong totalDuration = new AtomicLong(0);
        private final AtomicLong totalItems = new AtomicLong(0);
        private final AtomicLong executionCount = new AtomicLong(0);
        private volatile long minDuration = Long.MAX_VALUE;
        private volatile long maxDuration = 0;
        private volatile long lastExecutionTime = 0;
        private volatile long firstExecutionTime = 0;
        
        public ProfilerEntry(String operationName) {
            this.operationName = operationName;
        }
        
        public void addMeasurement(long durationMs, int itemsProcessed) {
            totalDuration.addAndGet(durationMs);
            totalItems.addAndGet(itemsProcessed);
            long count = executionCount.incrementAndGet();
            lastExecutionTime = System.currentTimeMillis();
            
            if (count == 1) {
                firstExecutionTime = lastExecutionTime;
            }
            
            // Update min/max atomically
            synchronized (this) {
                if (durationMs < minDuration) minDuration = durationMs;
                if (durationMs > maxDuration) maxDuration = durationMs;
            }
        }
        
        public String getOperationName() { return operationName; }
        public long getTotalDuration() { return totalDuration.get(); }
        public long getTotalItems() { return totalItems.get(); }
        public long getExecutionCount() { return executionCount.get(); }
        public long getMinDuration() { return minDuration == Long.MAX_VALUE ? 0 : minDuration; }
        public long getMaxDuration() { return maxDuration; }
        public long getLastExecutionTime() { return lastExecutionTime; }
        public long getFirstExecutionTime() { return firstExecutionTime; }
        
        public double getAverageDuration() {
            long count = executionCount.get();
            return count > 0 ? (double) totalDuration.get() / count : 0;
        }
        
        public double getAverageItems() {
            long count = executionCount.get();
            return count > 0 ? (double) totalItems.get() / count : 0;
        }
        
        public double getItemsPerSecond() {
            if (firstExecutionTime == 0 || lastExecutionTime == 0) return 0;
            long timeSpanMs = lastExecutionTime - firstExecutionTime;
            if (timeSpanMs == 0) return 0;
            return (double) totalItems.get() / timeSpanMs * 1000;
        }
        
        @Override
        public String toString() {
            return String.format("%s: Exec=%d, Avg=%.2fms, Min=%dms, Max=%dms, Total=%dms, Items=%d (%.1f/s)",
                operationName, executionCount.get(), getAverageDuration(),
                getMinDuration(), getMaxDuration(), totalDuration.get(),
                totalItems.get(), getItemsPerSecond());
        }
    }
}
