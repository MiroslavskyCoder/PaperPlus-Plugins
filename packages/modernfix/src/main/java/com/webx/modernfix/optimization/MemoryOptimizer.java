package com.webx.modernfix.optimization;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Memory optimization manager inspired by DFUBlaster
 * Provides smart GC triggering and memory monitoring
 */
public class MemoryOptimizer {
    private final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
    private final double threshold;
    private final AtomicLong totalFreed = new AtomicLong(0);
    private final AtomicLong gcExecutions = new AtomicLong(0);
    
    public MemoryOptimizer(double thresholdPercent) {
        this.threshold = thresholdPercent;
    }
    
    /**
     * Check if optimization is needed
     */
    public boolean needsOptimization() {
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        long used = heapUsage.getUsed();
        long max = heapUsage.getMax();
        double usagePercent = (double) used / max * 100;
        return usagePercent > threshold;
    }
    
    /**
     * Perform memory optimization
     * @return Optimization result
     */
    public MemoryOptimizationResult optimize() {
        long startTime = System.nanoTime();
        
        MemoryUsage beforeHeap = memoryBean.getHeapMemoryUsage();
        MemoryUsage beforeNonHeap = memoryBean.getNonHeapMemoryUsage();
        
        long beforeUsed = beforeHeap.getUsed();
        
        // Suggest GC
        System.gc();
        
        // Wait for GC to complete
        try {
            Thread.sleep(100);
        } catch (InterruptedException ignored) {}
        
        MemoryUsage afterHeap = memoryBean.getHeapMemoryUsage();
        MemoryUsage afterNonHeap = memoryBean.getNonHeapMemoryUsage();
        
        long afterUsed = afterHeap.getUsed();
        long freed = Math.max(0, beforeUsed - afterUsed);
        
        totalFreed.addAndGet(freed);
        gcExecutions.incrementAndGet();
        
        long duration = (System.nanoTime() - startTime) / 1_000_000; // ms
        
        return new MemoryOptimizationResult(
            beforeHeap, afterHeap,
            beforeNonHeap, afterNonHeap,
            freed, duration
        );
    }
    
    /**
     * Get current memory info
     */
    public MemoryInfo getMemoryInfo() {
        MemoryUsage heap = memoryBean.getHeapMemoryUsage();
        MemoryUsage nonHeap = memoryBean.getNonHeapMemoryUsage();
        
        return new MemoryInfo(
            heap.getUsed(),
            heap.getCommitted(),
            heap.getMax(),
            nonHeap.getUsed(),
            nonHeap.getCommitted(),
            nonHeap.getMax(),
            totalFreed.get(),
            gcExecutions.get()
        );
    }
    
    /**
     * Reset statistics
     */
    public void resetStats() {
        totalFreed.set(0);
        gcExecutions.set(0);
    }
    
    /**
     * Memory information holder
     */
    public static class MemoryInfo {
        public final long heapUsed;
        public final long heapCommitted;
        public final long heapMax;
        public final long nonHeapUsed;
        public final long nonHeapCommitted;
        public final long nonHeapMax;
        public final long totalFreed;
        public final long gcExecutions;
        
        public MemoryInfo(long heapUsed, long heapCommitted, long heapMax,
                         long nonHeapUsed, long nonHeapCommitted, long nonHeapMax,
                         long totalFreed, long gcExecutions) {
            this.heapUsed = heapUsed;
            this.heapCommitted = heapCommitted;
            this.heapMax = heapMax;
            this.nonHeapUsed = nonHeapUsed;
            this.nonHeapCommitted = nonHeapCommitted;
            this.nonHeapMax = nonHeapMax;
            this.totalFreed = totalFreed;
            this.gcExecutions = gcExecutions;
        }
        
        public double getHeapUsagePercent() {
            return (double) heapUsed / heapMax * 100;
        }
        
        @Override
        public String toString() {
            return String.format("Heap: %.2f%% (%d/%d MB), NonHeap: %d MB, Freed: %d MB, GC: %d",
                getHeapUsagePercent(),
                heapUsed / 1024 / 1024, heapMax / 1024 / 1024,
                nonHeapUsed / 1024 / 1024,
                totalFreed / 1024 / 1024,
                gcExecutions);
        }
    }
    
    /**
     * Memory optimization result holder
     */
    public static class MemoryOptimizationResult {
        public final MemoryUsage beforeHeap;
        public final MemoryUsage afterHeap;
        public final MemoryUsage beforeNonHeap;
        public final MemoryUsage afterNonHeap;
        public final long freed;
        public final long durationMs;
        
        public MemoryOptimizationResult(MemoryUsage beforeHeap, MemoryUsage afterHeap,
                                       MemoryUsage beforeNonHeap, MemoryUsage afterNonHeap,
                                       long freed, long durationMs) {
            this.beforeHeap = beforeHeap;
            this.afterHeap = afterHeap;
            this.beforeNonHeap = beforeNonHeap;
            this.afterNonHeap = afterNonHeap;
            this.freed = freed;
            this.durationMs = durationMs;
        }
        
        public double getBeforeUsagePercent() {
            return (double) beforeHeap.getUsed() / beforeHeap.getMax() * 100;
        }
        
        public double getAfterUsagePercent() {
            return (double) afterHeap.getUsed() / afterHeap.getMax() * 100;
        }
        
        @Override
        public String toString() {
            return String.format("Freed: %d MB (%.2f%% -> %.2f%%), Duration: %dms",
                freed / 1024 / 1024,
                getBeforeUsagePercent(),
                getAfterUsagePercent(),
                durationMs);
        }
    }
}
