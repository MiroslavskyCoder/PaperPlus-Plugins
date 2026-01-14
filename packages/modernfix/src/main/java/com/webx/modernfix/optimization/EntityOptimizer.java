package com.webx.modernfix.optimization;

import org.bukkit.World;
import org.bukkit.entity.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Entity optimization manager inspired by faster_item_rendering and ticking_chunk_alloc
 * Provides intelligent entity cleanup and tracking
 */
public class EntityOptimizer {
    private final int maxItemAge;
    private final Map<EntityType, EntityStats> entityStats = new HashMap<>();
    
    public EntityOptimizer(int maxItemAge) {
        this.maxItemAge = maxItemAge;
    }
    
    /**
     * Optimize entities in a world
     * @param world World to optimize
     * @return Optimization result
     */
    public OptimizationResult optimize(World world) {
        long startTime = System.nanoTime();
        AtomicInteger removed = new AtomicInteger(0);
        AtomicInteger scanned = new AtomicInteger(0);
        
        List<Entity> entities = world.getEntities();
        scanned.set(entities.size());
        
        for (Entity entity : entities) {
            scanned.incrementAndGet();
            
            // Track entity types
            EntityType type = entity.getType();
            entityStats.computeIfAbsent(type, k -> new EntityStats()).increment();
            
            // Remove old items
            if (entity instanceof Item) {
                Item item = (Item) entity;
                if (item.getTicksLived() > maxItemAge) {
                    item.remove();
                    removed.incrementAndGet();
                    entityStats.get(type).incrementRemoved();
                }
            }
            
            // Remove old arrows
            else if (entity instanceof Arrow) {
                Arrow arrow = (Arrow) entity;
                if (arrow.getTicksLived() > maxItemAge / 2) {
                    arrow.remove();
                    removed.incrementAndGet();
                    entityStats.get(type).incrementRemoved();
                }
            }
            
            // Remove old experience orbs
            else if (entity instanceof ExperienceOrb) {
                ExperienceOrb orb = (ExperienceOrb) entity;
                if (orb.getTicksLived() > maxItemAge) {
                    orb.remove();
                    removed.incrementAndGet();
                    entityStats.get(type).incrementRemoved();
                }
            }
        }
        
        long duration = (System.nanoTime() - startTime) / 1_000_000; // ms
        return new OptimizationResult(scanned.get(), removed.get(), duration);
    }
    
    /**
     * Get entity type statistics
     */
    public Map<EntityType, EntityStats> getEntityStats() {
        return Collections.unmodifiableMap(entityStats);
    }
    
    /**
     * Reset statistics
     */
    public void resetStats() {
        entityStats.clear();
    }
    
    /**
     * Entity statistics holder
     */
    public static class EntityStats {
        private int count = 0;
        private int removed = 0;
        
        public void increment() { count++; }
        public void incrementRemoved() { removed++; }
        public int getCount() { return count; }
        public int getRemoved() { return removed; }
        
        @Override
        public String toString() {
            return String.format("Total: %d, Removed: %d", count, removed);
        }
    }
    
    /**
     * Optimization result holder
     */
    public static class OptimizationResult {
        public final int scanned;
        public final int removed;
        public final long durationMs;
        
        public OptimizationResult(int scanned, int removed, long durationMs) {
            this.scanned = scanned;
            this.removed = removed;
            this.durationMs = durationMs;
        }
        
        @Override
        public String toString() {
            return String.format("Scanned: %d, Removed: %d, Duration: %dms",
                scanned, removed, durationMs);
        }
    }
}
