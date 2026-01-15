package lxxv.shared.javascript.api;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Facade exposing a minimal server API surface to JS via the `server` object.
 */
public class ServerApiFacade {
    public Map<String, Object> playerSummary(UUID playerId) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", playerId == null ? null : playerId.toString());
        data.put("online", Boolean.TRUE);
        data.put("displayName", "Unknown");
        return data;
    }

    public String version() {
        return "server-api-facade-1";
    }
}
