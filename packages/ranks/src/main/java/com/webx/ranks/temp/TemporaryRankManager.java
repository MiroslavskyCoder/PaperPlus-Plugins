
package com.webx.ranks.temp;

import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import java.util.Collections;

import org.bukkit.entity.Player;

public class TemporaryRankManager {
    public Map<String, Long> getExpiringRanks(UUID playerId) {
        // Stub: return empty map
        return new HashMap<>();
    }

    public boolean isTemporary(String rankId) {
        // Stub: always false
        return false;
    }
    public void assignTemporaryRank(Player player, String rankId, long expireAt) {
        // Назначить временный ранг
    }
    public void checkExpirations() {
        // Проверить истечение временных рангов
    }
}
