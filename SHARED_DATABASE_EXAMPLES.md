# Shared Database Integration Example

## Example: Ranks Plugin Integration

### 1. Update plugin.yml
```yaml
name: Ranks
version: 1.0
description: Player rank management system
author: WebX
commands:
  rank:
    description: Manage player ranks
  rankinfo:
    description: View rank information
```

### 2. Update RanksPlugin.java
```java
package com.webx.ranks;

import com.webx.ranks.managers.RankManager;
import com.webx.ranks.managers.PlayerRankManager;
import com.webx.ranks.commands.*;
import com.webx.shared.SharedPluginDatabase;
import com.webx.shared.PluginDataHelper;
import org.bukkit.plugin.java.JavaPlugin;

public class RanksPlugin extends JavaPlugin {
    private RankManager rankManager;
    private PlayerRankManager playerRankManager;
    private PluginDataHelper dataHelper;

    @Override
    public void onEnable() {
        // Initialize shared database
        dataHelper = new PluginDataHelper("Ranks");
        
        // Initialize managers
        rankManager = new RankManager(getDataFolder());
        playerRankManager = new PlayerRankManager(getDataFolder());
        
        // Store plugin version in shared database
        dataHelper.setString("version", getDescription().getVersion());
        dataHelper.setLong("enabled_at", System.currentTimeMillis());
        
        // Initialize default permissions
        rankManager.initializeDefaultPermissions();

        // Register commands
        getCommand("rank").setExecutor(new RankCommand(rankManager, playerRankManager));
        getCommand("rankinfo").setExecutor(new RankInfoCommand(rankManager, playerRankManager));
        getCommand("ranklist").setExecutor(new RankListCommand(rankManager));

        // Register event listeners
        getServer().getPluginManager().registerEvents(
                new RankEventListener(rankManager, playerRankManager), this);

        getLogger().info("✅ Ranks & Permissions System enabled!");
    }

    @Override
    public void onDisable() {
        if (playerRankManager != null) {
            playerRankManager.saveData();
        }
        if (rankManager != null) {
            rankManager.saveData();
        }
        
        // Update status in shared database
        dataHelper.setString("status", "disabled");
        dataHelper.setLong("disabled_at", System.currentTimeMillis());
        
        getLogger().info("❌ Ranks & Permissions System disabled!");
    }
}
```

### 3. Store player rank data
```java
// In RankEventListener.java
@EventHandler
public void onPlayerJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    String uuid = player.getUniqueId().toString();
    
    // Store in shared database
    dataHelper.setPlayerString(uuid, "last_login", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    dataHelper.setPlayerString(uuid, "player_name", player.getName());
    
    // Load current rank from shared database
    String currentRank = dataHelper.getPlayerString(uuid, "rank");
    if (currentRank == null) {
        currentRank = "default";
        dataHelper.setPlayerString(uuid, "rank", currentRank);
    }
}

@EventHandler
public void onPlayerQuit(PlayerQuitEvent event) {
    Player player = event.getPlayer();
    String uuid = player.getUniqueId().toString();
    
    // Update last logout
    dataHelper.setPlayerString(uuid, "last_logout", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
}
```

### 4. Access shared data from other plugins
```java
// From any other plugin
PluginDataHelper ranksHelper = new PluginDataHelper("Ranks");

String playerRank = ranksHelper.getPlayerString(playerUUID, "rank");
long lastLogin = ranksHelper.getPlayerLong(playerUUID, "last_login");
```

## Database file example
```json
{
  "plugins": {
    "Ranks": {
      "version": "1.0",
      "enabled_at": 1704988800000,
      "status": "enabled"
    },
    "Economy": {
      "version": "2.1",
      "enabled_at": 1704988801000
    }
  },
  "players": {
    "550e8400-e29b-41d4-a716-446655440000": {
      "name": "PlayerName",
      "Ranks_rank": "admin",
      "Ranks_last_login": "2025-01-12 10:30:45",
      "Ranks_last_logout": "2025-01-12 10:35:20",
      "Economy_balance": 1000.50
    }
  },
  "global": {
    "server_maintenance": false,
    "total_players_ever": 150
  },
  "timestamp": 1704988800000
}
```

## Benefits
1. **Centralized**: Single source of truth for all plugin data
2. **Shared**: Any plugin can read any other plugin's data (with proper namespacing)
3. **Persistent**: Automatic saving to JSON file
4. **Thread-safe**: Built-in concurrency handling
5. **Simple API**: Easy helper methods for common operations
