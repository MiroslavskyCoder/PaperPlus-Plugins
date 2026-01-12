# Shared Plugin Database System

## Overview

The **Shared Plugin Database** is a centralized JSON database that all plugins can use to store and retrieve data. This eliminates the need for each plugin to manage its own file-based storage system.

**Location**: `{CACHE}/lxxv_plugins_server.json`

## Key Features

✅ **Centralized Storage** - Single database for all plugins  
✅ **Thread-Safe** - ReadWriteLock for concurrent access  
✅ **Auto-Persistence** - Automatic saving after each write  
✅ **Three Data Sections** - Plugin, Player, and Global data  
✅ **Simple API** - Helper utilities for easy integration  
✅ **GSON Integration** - JSON serialization/deserialization  

## Quick Start

### 1. Initialize in Your Plugin
```java
import com.webx.shared.PluginDataHelper;

public class MyPlugin extends JavaPlugin {
    private PluginDataHelper data;
    
    @Override
    public void onEnable() {
        data = new PluginDataHelper("MyPlugin");
        data.setString("version", "1.0");
    }
}
```

### 2. Store Plugin Data
```java
// Store configuration
data.setString("config_version", "1.0");
data.setInt("max_players", 100);
data.setBoolean("maintenance_mode", false);
```

### 3. Store Player Data
```java
String uuid = player.getUniqueId().toString();

// Save player stats
data.setPlayerString(uuid, "rank", "admin");
data.setPlayerInt(uuid, "level", 50);
data.setPlayerDouble(uuid, "experience", 1500.75);
```

### 4. Access Global Data
```java
// Shared across all plugins
data.setGlobalString("server_name", "MyServer");
data.setGlobalInt("online_players", 45);
```

## Database Structure

```
{CACHE}/lxxv_plugins_server.json
├── plugins/
│   ├── Ranks/
│   ├── Economy/
│   └── MyPlugin/
├── players/
│   ├── {UUID1}/
│   ├── {UUID2}/
│   └── ...
├── global/
└── timestamp
```

## Configuration

Set cache directory via system property:

```bash
java -Dlxxv.cache.dir=/path/to/cache ...
```

Default: `./cache` (relative to server directory)

## API Methods

### Plugin Helper (`PluginDataHelper`)

```java
// Type-specific setters/getters
setString(key, value) / getString(key)
setInt(key, value) / getInt(key)
setDouble(key, value) / getDouble(key)
setBoolean(key, value) / getBoolean(key)
setObject(key, value) / getObject(key)
setArray(key, value) / getArray(key)

// Player data (auto-namespaced)
setPlayerString(uuid, key, value)
getPlayerString(uuid, key)
setPlayerInt(uuid, key, value)
getPlayerInt(uuid, key)
// ... and more

// Global data (auto-namespaced)
setGlobalString(key, value)
getGlobalString(key)
// ... and more
```

### Direct Access (`SharedPluginDatabase`)

```java
SharedPluginDatabase db = SharedPluginDatabase.getInstance();

// Plugin-level
db.getPluginData("PluginName")
db.setPluginValue("PluginName", key, value)
db.getPluginValue("PluginName", key)

// Player-level
db.getPlayerData(uuid)
db.setPlayerValue(uuid, key, value)
db.getPlayerValue(uuid, key)

// Global
db.getGlobalData()
db.setGlobalValue(key, value)
db.getGlobalValue(key)

// File operations
db.save()
db.reload()
db.getAllData()
db.getDatabasePath()
```

## Use Cases

### 1. Cross-Plugin Communication
```java
// Plugin A stores data
dataHelper.setGlobalString("economy_enabled", "true");

// Plugin B reads data
PluginDataHelper economyHelper = new PluginDataHelper("Economy");
String enabled = economyHelper.getGlobalString("economy_enabled");
```

### 2. Player Statistics Tracking
```java
// Store player achievements across plugins
data.setPlayerString(uuid, "first_login_date", LocalDate.now().toString());
data.setPlayerInt(uuid, "total_playtime_hours", 100);
data.setPlayerObject(uuid, "achievements", achievementsList);
```

### 3. Server Configuration
```java
// Centralized server settings
data.setGlobalString("server_mode", "survival");
data.setGlobalBoolean("pvp_enabled", true);
data.setGlobalInt("difficulty_level", 2);
```

### 4. Plugin Metadata
```java
// Track plugin status
data.setString("status", "enabled");
data.setLong("last_restart", System.currentTimeMillis());
data.setString("version", getDescription().getVersion());
```

## Performance Considerations

- **Database loaded in memory** - Fast read/write but uses RAM
- **Disk writes on every change** - Consider batching operations
- **Thread-safe via locks** - Multiple readers, exclusive writers
- **GSON overhead** - Minimal for typical use cases

## Best Practices

1. **Use descriptive key names**
   ```java
   // ✅ Good
   data.setPlayerInt(uuid, "total_kills", 50);
   
   // ❌ Bad
   data.setPlayerInt(uuid, "k", 50);
   ```

2. **Namespace player data by plugin**
   ```java
   // ✅ Automatic with PluginDataHelper
   data.setPlayerString(uuid, "rank", "admin");
   // Stored as: "RanksPlugin_rank"
   ```

3. **Handle null values**
   ```java
   String rank = data.getPlayerString(uuid, "rank");
   if (rank == null) {
       rank = "default";
   }
   ```

4. **Batch related updates**
   ```java
   data.setPlayerInt(uuid, "level", 50);
   data.setPlayerInt(uuid, "exp", 1000);
   data.setPlayerString(uuid, "rank", "admin");
   // All saved in rapid succession
   ```

## Troubleshooting

**Q: Database file not found?**  
A: Ensure cache directory is writable and system property is set correctly.

**Q: Data not persisting?**  
A: Check for I/O errors in logs. Database auto-saves after each write.

**Q: Performance issues?**  
A: Consider the size of your database. Archive old player data periodically.

**Q: Lock timeout?**  
A: Shouldn't happen with proper API usage. Check for deadlocks in custom code.

## Migration Guide

### From Plugin-Specific Files

**Before (old way):**
```java
// Each plugin has its own data folder
FileConfiguration config = YamlConfiguration.loadConfiguration(
    new File(getDataFolder(), "players.yml"));
```

**After (new way):**
```java
PluginDataHelper data = new PluginDataHelper("MyPlugin");
String playerData = data.getPlayerString(uuid, "data");
```

## API Documentation

See [SHARED_DATABASE_GUIDE.md](../SHARED_DATABASE_GUIDE.md) for complete API reference.

## Examples

See [SHARED_DATABASE_EXAMPLES.md](../SHARED_DATABASE_EXAMPLES.md) for detailed integration examples.

## License

Part of WebX Minecraft Server Plugin Suite
