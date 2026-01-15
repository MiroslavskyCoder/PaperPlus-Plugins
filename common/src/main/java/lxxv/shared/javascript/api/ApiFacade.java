package lxxv.shared.javascript.api;

import java.util.Map;

/**
 * Exposes registered APIs as a single facade to scripts.
 */
public class ApiFacade {
    private final ApiRegistry registry;

    public ApiFacade(ApiRegistry registry) {
        this.registry = registry;
    }

    public Object get(String name) {
        return registry.get(name);
    }

    public Map<String, Object> all() {
        return registry.all();
    }
}
