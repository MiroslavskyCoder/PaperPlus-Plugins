package com.webx.modernfix.util;

import java.util.concurrent.TimeUnit;

/**
 * Utility class for time measurements and formatting
 */
public class TimeUtil {
    
    /**
     * Format nanoseconds to human-readable string
     */
    public static String formatNanos(long nanos) {
        if (nanos < 1_000) {
            return nanos + "ns";
        } else if (nanos < 1_000_000) {
            return String.format("%.2fμs", nanos / 1_000.0);
        } else if (nanos < 1_000_000_000) {
            return String.format("%.2fms", nanos / 1_000_000.0);
        } else {
            return String.format("%.2fs", nanos / 1_000_000_000.0);
        }
    }
    
    /**
     * Format milliseconds to human-readable string
     */
    public static String formatMillis(long millis) {
        if (millis < 1000) {
            return millis + "ms";
        } else if (millis < 60_000) {
            return String.format("%.2fs", millis / 1000.0);
        } else if (millis < 3_600_000) {
            long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
            long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;
            return String.format("%dm %ds", minutes, seconds);
        } else {
            long hours = TimeUnit.MILLISECONDS.toHours(millis);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
            return String.format("%dh %dm", hours, minutes);
        }
    }
    
    /**
     * Measure execution time of a runnable
     * @param task Task to execute
     * @return Execution time in nanoseconds
     */
    public static long measure(Runnable task) {
        long start = System.nanoTime();
        task.run();
        return System.nanoTime() - start;
    }
    
    /**
     * Convert bytes to human-readable format
     */
    public static String formatBytes(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", bytes / 1024.0 / 1024.0);
        } else {
            return String.format("%.2f GB", bytes / 1024.0 / 1024.0 / 1024.0);
        }
    }
    
    /**
     * Create a simple stopwatch
     */
    public static class Stopwatch {
        private long startTime;
        private long stopTime;
        private boolean running;
        
        public Stopwatch start() {
            startTime = System.nanoTime();
            running = true;
            return this;
        }
        
        public Stopwatch stop() {
            stopTime = System.nanoTime();
            running = false;
            return this;
        }
        
        public long getElapsedNanos() {
            if (running) {
                return System.nanoTime() - startTime;
            }
            return stopTime - startTime;
        }
        
        public long getElapsedMillis() {
            return getElapsedNanos() / 1_000_000;
        }
        
        public String getElapsedFormatted() {
            return formatNanos(getElapsedNanos());
        }
        
        @Override
        public String toString() {
            return getElapsedFormatted();
        }
        
        public static Stopwatch createStarted() {
            return new Stopwatch().start();
        }
    }
}
