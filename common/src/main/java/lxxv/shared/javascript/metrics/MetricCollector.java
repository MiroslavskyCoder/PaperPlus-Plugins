package lxxv.shared.javascript.metrics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Thread-safe metric collector.
 */
public class MetricCollector {
    private final List<MetricSample> samples = Collections.synchronizedList(new ArrayList<>());

    public void record(MetricSample sample) {
        if (sample != null) {
            samples.add(sample);
        }
    }

    public List<MetricSample> snapshot() {
        synchronized (samples) {
            return new ArrayList<>(samples);
        }
    }

    public void clear() {
        samples.clear();
    }
}
