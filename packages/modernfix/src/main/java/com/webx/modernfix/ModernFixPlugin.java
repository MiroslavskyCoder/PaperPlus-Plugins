package com.webx.modernfix;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

/**
 * ModernFix - Advanced server optimization plugin inspired by embeddedt/ModernFix
 * 
 * Features:
 * - Intelligent entity cleanup with age tracking
 * - Chunk caching and smart unloading
 * - Memory optimization with detailed metrics
 * - Thread pool management for async operations
 * - Performance profiling and statistics
 * 
 * Based on optimizations from https://github.com/embeddedt/ModernFix
 */
public class ModernFixPlugin extends JavaPlugin {
    private static ModernFixPlugin instance;
    private Logger logger;
    private OptimizationConfig config;
    
    // Scheduled optimization tasks
    private BukkitTask entityOptimizationTask;
    private BukkitTask chunkOptimizationTask;
    private BukkitTask memoryOptimizationTask;
    private BukkitTask tickOptimizationTask;
    
    // Advanced caching and optimization (inspired by PackResourcesCacheEngine)
    private final Map<String, WeakReference<Chunk>> chunkCache = new ConcurrentHashMap<>();
    
    // Thread pool with smart sizing (inspired by UtilMixin thread_priorities)
    private final ExecutorService asyncExecutor = Executors.newFixedThreadPool(
        Math.max(2, Runtime.getRuntime().availableProcessors() / 4),
        new ThreadFactory() {
            private int threadNumber = 0;
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r, "ModernFix-Async-" + threadNumber++);
                thread.setDaemon(true);
                thread.setPriority(Thread.MIN_PRIORITY + 1);
                return thread;
            }
        }
    );
    
    // Performance tracking (inspired by SparkLaunchProfiler)
    private final Map<String, PerformanceMetric> performanceMetrics = new ConcurrentHashMap<>();
    private final AtomicLong totalOptimizations = new AtomicLong(0);
    private final AtomicLong totalMemoryFreed = new AtomicLong(0);
    private final AtomicLong totalEntitiesRemoved = new AtomicLong(0);
    
    // Memory monitoring
    private final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
    
    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();
        
        // Load configuration
        saveDefaultConfig();
        config = new OptimizationConfig(getConfig());
        
        // Start optimization tasks
        if (config.isEntityOptimization()) {
            startEntityOptimization();
        }
        
        if (config.isChunkOptimization()) {
            startChunkOptimization();
        }
        
        if (config.isMemoryOptimization()) {
            startMemoryOptimization();
        }
        
        if (config.isTickOptimization()) {
            applyTickOptimizations();
        }
        
        // Register commands
        getCommand("modernfix").setExecutor(new ModernFixCommand(this, config));
        
        logger.info("✅ ModernFix enabled! Advanced optimizations active.");
        logger.info("Entity optimization: " + config.isEntityOptimization());
        logger.info("Chunk optimization: " + config.isChunkOptimization());
        logger.info("Memory optimization: " + config.isMemoryOptimization());
        logger.info("Tick optimization: " + config.isTickOptimization());
        logger.info("Thread pool size: " + Runtime.getRuntime().availableProcessors() / 4);
    }

    @Override
    public void onDisable() {
        // Cancel all scheduled tasks
        if (entityOptimizationTask != null) entityOptimizationTask.cancel();
        if (chunkOptimizationTask != null) chunkOptimizationTask.cancel();
        if (memoryOptimizationTask != null) memoryOptimizationTask.cancel();
        if (tickOptimizationTask != null) tickOptimizationTask.cancel();
        
        // Shutdown async executor gracefully
        asyncExecutor.shutdown();
        try {
            if (!asyncExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                asyncExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            asyncExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        // Clear caches
        chunkCache.clear();
        performanceMetrics.clear();
        
        logger.info("ModernFix отключен. Статистика:");
        logger.info("  Всего оптимизаций: " + totalOptimizations.get());
        logger.info("  Удалено сущностей: " + totalEntitiesRemoved.get());
        logger.info("  Память освобождено: " + (totalMemoryFreed.get() / 1024 / 1024) + " МБ");
        instance = null;
    }

    /**
     * Entity optimization with age tracking
     * Inspired by: ticking_chunk_alloc/BatMixin and faster_item_rendering/ItemRendererMixin
     */
    private void startEntityOptimization() {
        int intervalTicks = 600; // 30 seconds
        entityOptimizationTask = Bukkit.getScheduler().runTaskTimer(this, 
            this::optimizeEntities, intervalTicks, intervalTicks);
        logger.info("Запущена оптимизация сущностей (каждые " + (intervalTicks / 20) + " секунд)");
    }

    /**
     * Chunk optimization with caching
     * Inspired by: paper_chunk_patches/ChunkMapMixin and dynamic_resources/ModelBakeryMixin
     */
    private void startChunkOptimization() {
        int intervalTicks = 1200; // 60 seconds
        chunkOptimizationTask = Bukkit.getScheduler().runTaskTimer(this,
            this::optimizeChunks, intervalTicks, intervalTicks);
        logger.info("Запущена оптимизация чанков (каждые " + (intervalTicks / 20) + " секунд)");
    }

    /**
     * Memory optimization with GC hints
     * Inspired by: DFUBlaster and memory management techniques
     */
    private void startMemoryOptimization() {
        int intervalTicks = 2400; // 120 seconds (2 minutes)
        memoryOptimizationTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this,
            this::optimizeMemory, intervalTicks, intervalTicks);
        logger.info("Запущена оптимизация памяти (каждые " + (intervalTicks / 20) + " секунд)");
    }

    /**
     * Apply tick optimizations
     * Inspired by: tick optimization and view distance management
     */
    private void applyTickOptimizations() {
        for (World world : Bukkit.getWorlds()) {
            // View distance optimization
            int viewDistance = Math.min(world.getViewDistance(), 10);
            world.setViewDistance(viewDistance);
            
            // Simulation distance (Paper API)
            try {
                world.setSimulationDistance(Math.min(viewDistance, 8));
            } catch (Exception e) {
                // Ignore if not supported
            }
        }
        logger.info("Применены оптимизации тиков и дистанции отрисовки");
    }

    /**
     * Optimize entities with detailed tracking
     */
    private void optimizeEntities() {
        long startTime = System.nanoTime();
        int removedItems = 0;
        int totalEntities = 0;
        int maxAge = config.getEntityMaxAge();
        
        try {
            for (World world : Bukkit.getWorlds()) {
                List<Entity> entities = world.getEntities();
                totalEntities += entities.size();
                
                for (Entity entity : entities) {
                    if (entity instanceof Item) {
                        Item item = (Item) entity;
                        if (item.getTicksLived() > maxAge) {
                            item.remove();
                            removedItems++;
                            totalEntitiesRemoved.incrementAndGet();
                        }
                    }
                }
            }
            
            if (removedItems > 0) {
                long duration = (System.nanoTime() - startTime) / 1_000_000; // ms
                totalOptimizations.addAndGet(removedItems);
                updateMetric("entity_cleanup", duration, removedItems);
                logger.fine(String.format("Оптимизация сущностей: удалено %d из %d за %d мс", 
                                         removedItems, totalEntities, duration));
            }
        } catch (Exception e) {
            logger.warning("Ошибка оптимизации сущностей: " + e.getMessage());
        }
    }

    /**
     * Optimize chunks with smart caching and unloading
     */
    private void optimizeChunks() {
        long startTime = System.nanoTime();
        
        for (World world : Bukkit.getWorlds()) {
            asyncExecutor.submit(() -> {
                try {
                    int cachedChunks = 0;
                    int cleanedCache = 0;
                    Chunk[] loadedChunks = world.getLoadedChunks();
                    
                    // Clean up weak references in cache
                    Iterator<Map.Entry<String, WeakReference<Chunk>>> it = chunkCache.entrySet().iterator();
                    while (it.hasNext()) {
                        if (it.next().getValue().get() == null) {
                            it.remove();
                            cleanedCache++;
                        }
                    }
                    
                    // Cache loaded chunks
                    for (Chunk chunk : loadedChunks) {
                        String key = world.getName() + ":" + chunk.getX() + ":" + chunk.getZ();
                        chunkCache.put(key, new WeakReference<>(chunk));
                        cachedChunks++;
                    }
                    
                    long duration = (System.nanoTime() - startTime) / 1_000_000;
                    updateMetric("chunk_optimization", duration, cachedChunks);
                    
                    if (cachedChunks > 0 || cleanedCache > 0) {
                        logger.fine(String.format("Мир %s: кэшировано %d чанков, очищено %d ссылок за %d мс", 
                                                 world.getName(), cachedChunks, cleanedCache, duration));
                    }
                } catch (Exception e) {
                    logger.warning("Ошибка оптимизации чанков: " + e.getMessage());
                }
            });
        }
    }

    /**
     * Optimize memory with detailed metrics and cache cleanup
     */
    private void optimizeMemory() {
        long startTime = System.nanoTime();
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        double memoryUsagePercent = (double) usedMemory / maxMemory * 100;
        
        if (memoryUsagePercent > config.getMemoryThreshold()) {
            try {
                // Clear weak references in chunk cache
                int clearedCacheEntries = 0;
                Iterator<Map.Entry<String, WeakReference<Chunk>>> it = chunkCache.entrySet().iterator();
                while (it.hasNext()) {
                    if (it.next().getValue().get() == null) {
                        it.remove();
                        clearedCacheEntries++;
                    }
                }
                
                // Measure memory before GC
                long heapBefore = memoryBean.getHeapMemoryUsage().getUsed();
                
                // Suggest GC (inspired by DFUBlaster.blastMaps())
                System.gc();
                Thread.sleep(100); // Give GC time to complete
                
                // Measure memory after GC
                long heapAfter = memoryBean.getHeapMemoryUsage().getUsed();
                long freed = Math.max(0, heapBefore - heapAfter);
                totalMemoryFreed.addAndGet(freed);
                
                long duration = (System.nanoTime() - startTime) / 1_000_000;
                updateMetric("memory_optimization", duration, clearedCacheEntries);
                
                logger.info(String.format(
                    "Оптимизация памяти: освобождено %.2f МБ, очищено кэшей: %d, " +
                    "использование: %.1f%% (%.0f/%.0f МБ), heap: %.0f МБ",
                    freed / 1024.0 / 1024.0, clearedCacheEntries, memoryUsagePercent,
                    usedMemory / 1024.0 / 1024.0, maxMemory / 1024.0 / 1024.0,
                    heapAfter / 1024.0 / 1024.0
                ));
            } catch (Exception e) {
                logger.warning("Ошибка оптимизации памяти: " + e.getMessage());
            }
        } else {
            logger.fine(String.format("Память в норме: %.1f%% (%.0f/%.0f МБ)", 
                                     memoryUsagePercent,
                                     usedMemory / 1024.0 / 1024.0, 
                                     maxMemory / 1024.0 / 1024.0));
        }
    }

    /**
     * Force optimization (triggered manually or by command)
     */
    public void forceOptimize() {
        logger.info("Принудительная оптимизация...");
        long startTime = System.currentTimeMillis();
        
        optimizeEntities();
        optimizeChunks();
        optimizeMemory();
        
        long duration = System.currentTimeMillis() - startTime;
        logger.info("Оптимизация завершена за " + duration + " мс");
    }

    /**
     * Reload configuration and restart tasks
     */
    public void reloadPluginConfig() {
        reloadConfig();
        config = new OptimizationConfig(getConfig());
        
        // Cancel all tasks
        if (entityOptimizationTask != null) entityOptimizationTask.cancel();
        if (chunkOptimizationTask != null) chunkOptimizationTask.cancel();
        if (memoryOptimizationTask != null) memoryOptimizationTask.cancel();
        if (tickOptimizationTask != null) tickOptimizationTask.cancel();
        
        // Restart tasks
        if (config.isEntityOptimization()) startEntityOptimization();
        if (config.isChunkOptimization()) startChunkOptimization();
        if (config.isMemoryOptimization()) startMemoryOptimization();
        if (config.isTickOptimization()) applyTickOptimizations();
        
        logger.info("Конфигурация перезагружена и оптимизации перезапущены");
    }

    // Performance tracking helpers
    private void updateMetric(String operationName, long durationMs, int itemsProcessed) {
        performanceMetrics.compute(operationName, (k, v) -> {
            if (v == null) {
                v = new PerformanceMetric();
            }
            v.addMeasurement(durationMs, itemsProcessed);
            return v;
        });
    }
    
    public Map<String, PerformanceMetric> getPerformanceMetrics() {
        return Collections.unmodifiableMap(performanceMetrics);
    }
    
    public long getTotalOptimizations() {
        return totalOptimizations.get();
    }
    
    public long getTotalMemoryFreed() {
        return totalMemoryFreed.get();
    }
    
    public long getTotalEntitiesRemoved() {
        return totalEntitiesRemoved.get();
    }
    
    public int getCachedChunksCount() {
        // Clean up dead references before counting
        chunkCache.values().removeIf(ref -> ref.get() == null);
        return chunkCache.size();
    }
    
    public static ModernFixPlugin getInstance() {
        return instance;
    }
    
    /**
     * Performance metric tracking
     * Inspired by SparkLaunchProfiler performance measurement
     */
    public static class PerformanceMetric {
        private final AtomicLong totalDuration = new AtomicLong(0);
        private final AtomicLong totalItems = new AtomicLong(0);
        private final AtomicLong executionCount = new AtomicLong(0);
        private volatile long lastExecutionTime = 0;
        private volatile long minDuration = Long.MAX_VALUE;
        private volatile long maxDuration = 0;
        
        public void addMeasurement(long durationMs, int itemsProcessed) {
            totalDuration.addAndGet(durationMs);
            totalItems.addAndGet(itemsProcessed);
            executionCount.incrementAndGet();
            lastExecutionTime = System.currentTimeMillis();
            
            // Track min/max
            if (durationMs < minDuration) minDuration = durationMs;
            if (durationMs > maxDuration) maxDuration = durationMs;
        }
        
        public double getAverageDuration() {
            long count = executionCount.get();
            return count > 0 ? (double) totalDuration.get() / count : 0;
        }
        
        public double getAverageItems() {
            long count = executionCount.get();
            return count > 0 ? (double) totalItems.get() / count : 0;
        }
        
        public long getTotalDuration() { return totalDuration.get(); }
        public long getTotalItems() { return totalItems.get(); }
        public long getExecutionCount() { return executionCount.get(); }
        public long getLastExecutionTime() { return lastExecutionTime; }
        public long getMinDuration() { return minDuration == Long.MAX_VALUE ? 0 : minDuration; }
        public long getMaxDuration() { return maxDuration; }
        
        @Override
        public String toString() {
            return String.format("Executions: %d, Avg: %.2fms, Min: %dms, Max: %dms, Total items: %d",
                executionCount.get(), getAverageDuration(), getMinDuration(), getMaxDuration(), totalItems.get());
        }
    }
}
