package lxxv.shared.javascript.metrics;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

/**
 * Basic system metrics (CPU load, memory footprint).
 */
public class SystemMetrics {
    private final OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();

    public MetricSample cpuLoad() {
        double load = 0.0;
        try {
            if (osBean instanceof com.sun.management.OperatingSystemMXBean sun) {
                load = sun.getProcessCpuLoad();
            }
        } catch (Exception ignored) {
        }
        return new MetricSample("system.cpu.process_load", load, null);
    }

    public MetricSample systemLoadAvg() {
        double load = osBean.getSystemLoadAverage();
        return new MetricSample("system.cpu.load_average", load, null);
    }

    public MetricSample usedMemoryBytes() {
        long used = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        return new MetricSample("jvm.memory.used_bytes", used, null);
    }
}
