package com.webx.modernfix.cache;

import org.bukkit.Chunk;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Advanced chunk cache manager inspired by PackResourcesCacheEngine
 * Uses WeakReference for automatic memory management
 */
public class ChunkCacheManager {
    private final Map<String, WeakReference<Chunk>> cache = new ConcurrentHashMap<>();
    private final AtomicLong hits = new AtomicLong(0);
    private final AtomicLong misses = new AtomicLong(0);
    private final AtomicLong evictions = new AtomicLong(0);
    
    /**
     * Cache a chunk with weak reference
     * @param world World name
     * @param x Chunk X coordinate
     * @param z Chunk Z coordinate
     * @param chunk Chunk to cache
     */
    public void put(String world, int x, int z, Chunk chunk) {
        String key = generateKey(world, x, z);
        cache.put(key, new WeakReference<>(chunk));
    }
    
    /**
     * Get cached chunk
     * @param world World name
     * @param x Chunk X coordinate
     * @param z Chunk Z coordinate
     * @return Cached chunk or null if not found/evicted
     */
    public Chunk get(String world, int x, int z) {
        String key = generateKey(world, x, z);
        WeakReference<Chunk> ref = cache.get(key);
        
        if (ref == null) {
            misses.incrementAndGet();
            return null;
        }
        
        Chunk chunk = ref.get();
        if (chunk == null) {
            misses.incrementAndGet();
            cache.remove(key);
            evictions.incrementAndGet();
            return null;
        }
        
        hits.incrementAndGet();
        return chunk;
    }
    
    /**
     * Remove chunk from cache
     */
    public void remove(String world, int x, int z) {
        String key = generateKey(world, x, z);
        cache.remove(key);
    }
    
    /**
     * Clean up evicted references
     * @return Number of cleaned entries
     */
    public int cleanup() {
        int cleaned = 0;
        var iterator = cache.entrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            if (entry.getValue().get() == null) {
                iterator.remove();
                cleaned++;
                evictions.incrementAndGet();
            }
        }
        return cleaned;
    }
    
    /**
     * Clear entire cache
     */
    public void clear() {
        cache.clear();
        hits.set(0);
        misses.set(0);
        evictions.set(0);
    }
    
    /**
     * Get cache size (excluding evicted entries)
     */
    public int size() {
        cleanup();
        return cache.size();
    }
    
    /**
     * Get cache hit rate
     */
    public double getHitRate() {
        long totalRequests = hits.get() + misses.get();
        return totalRequests > 0 ? (double) hits.get() / totalRequests * 100 : 0;
    }
    
    /**
     * Get cache statistics
     */
    public CacheStats getStats() {
        return new CacheStats(
            cache.size(),
            hits.get(),
            misses.get(),
            evictions.get(),
            getHitRate()
        );
    }
    
    private String generateKey(String world, int x, int z) {
        return world + ":" + x + ":" + z;
    }
    
    /**
     * Cache statistics holder
     */
    public static class CacheStats {
        public final int size;
        public final long hits;
        public final long misses;
        public final long evictions;
        public final double hitRate;
        
        public CacheStats(int size, long hits, long misses, long evictions, double hitRate) {
            this.size = size;
            this.hits = hits;
            this.misses = misses;
            this.evictions = evictions;
            this.hitRate = hitRate;
        }
        
        @Override
        public String toString() {
            return String.format("Size: %d, Hits: %d, Misses: %d, Evictions: %d, Hit Rate: %.2f%%",
                size, hits, misses, evictions, hitRate);
        }
    }
}
