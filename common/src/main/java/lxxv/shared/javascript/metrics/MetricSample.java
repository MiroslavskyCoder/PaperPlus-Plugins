package lxxv.shared.javascript.metrics;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

/**
 * Represents a single metric sample.
 */
public class MetricSample {
    private final String name;
    private final double value;
    private final Instant timestamp;
    private final Map<String, String> tags;

    public MetricSample(String name, double value, Map<String, String> tags) {
        this.name = name;
        this.value = value;
        this.tags = tags == null ? Collections.emptyMap() : Collections.unmodifiableMap(tags);
        this.timestamp = Instant.now();
    }

    public String name() {
        return name;
    }

    public double value() {
        return value;
    }

    public Instant timestamp() {
        return timestamp;
    }

    public Map<String, String> tags() {
        return tags;
    }
}
