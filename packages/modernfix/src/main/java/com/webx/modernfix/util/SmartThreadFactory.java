package com.webx.modernfix.util;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Smart thread factory with priority management
 * Inspired by UtilMixin thread_priorities
 */
public class SmartThreadFactory implements ThreadFactory {
    private final String namePrefix;
    private final int priority;
    private final boolean daemon;
    private final ThreadGroup threadGroup;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    
    public SmartThreadFactory(String namePrefix) {
        this(namePrefix, Thread.MIN_PRIORITY + 1, true);
    }
    
    public SmartThreadFactory(String namePrefix, int priority, boolean daemon) {
        this.namePrefix = namePrefix;
        this.priority = Math.max(Thread.MIN_PRIORITY, Math.min(Thread.MAX_PRIORITY, priority));
        this.daemon = daemon;
        this.threadGroup = new ThreadGroup(namePrefix + "-Group");
    }
    
    @Override
    public Thread newThread(Runnable r) {
        String threadName = namePrefix + "-" + threadNumber.getAndIncrement();
        Thread thread = new Thread(threadGroup, r, threadName);
        thread.setDaemon(daemon);
        thread.setPriority(priority);
        
        // Set uncaught exception handler
        thread.setUncaughtExceptionHandler((t, e) -> {
            System.err.println("Uncaught exception in thread " + t.getName() + ": " + e.getMessage());
            e.printStackTrace();
        });
        
        return thread;
    }
    
    /**
     * Get number of active threads in this factory's group
     */
    public int getActiveThreadCount() {
        return threadGroup.activeCount();
    }
    
    /**
     * Get thread group
     */
    public ThreadGroup getThreadGroup() {
        return threadGroup;
    }
    
    /**
     * Create optimized executor service
     * @param poolSize Thread pool size
     * @return Configured ExecutorService
     */
    public static ExecutorService createOptimizedExecutor(String name, int poolSize) {
        return new ThreadPoolExecutor(
            poolSize,
            poolSize,
            60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            new SmartThreadFactory(name),
            new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
    
    /**
     * Create auto-sized executor based on CPU cores
     * @param name Thread name prefix
     * @return Configured ExecutorService
     */
    public static ExecutorService createAutoSizedExecutor(String name) {
        int processors = Runtime.getRuntime().availableProcessors();
        int poolSize = Math.max(2, processors / 4);
        return createOptimizedExecutor(name, poolSize);
    }
}
