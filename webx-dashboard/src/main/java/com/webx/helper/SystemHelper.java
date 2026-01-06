package com.webx.helper;

import com.sun.management.OperatingSystemMXBean;
import org.bukkit.Bukkit;

import java.io.File;
import java.lang.management.ManagementFactory;

public class SystemHelper {

    private static final OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    private static final Runtime runtime = Runtime.getRuntime();

    public static long getMemoryUsageMB() {
        return (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
    }

    public static long getMaxMemoryMB() {
        return runtime.maxMemory() / (1024 * 1024);
    }

    public static double getCpuLoad() { 
        return osBean.getSystemLoadAverage() * 100 / osBean.getAvailableProcessors();
    }

    public static long getTotalDiskSpaceGB() {
        File root = new File(Bukkit.getWorldContainer().getAbsolutePath()).getParentFile();
        if (root != null) {
            return root.getTotalSpace() / (1024 * 1024 * 1024);
        }
        return 1000; // Fallback
    }

    public static long getDiskUsageGB() {
        File root = new File(Bukkit.getWorldContainer().getAbsolutePath()).getParentFile();
        if (root != null) {
            return (root.getTotalSpace() - root.getUsableSpace()) / (1024 * 1024 * 1024);
        }
        return 500; // Fallback
    }
}