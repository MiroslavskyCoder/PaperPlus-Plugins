# Shared Plugin Database Guide

## Overview

The `SharedPluginDatabase` provides a centralized JSON database for all plugins to store and retrieve data. The database is stored at `{CACHE}/lxxv_plugins_server.json` where `{CACHE}` is configurable via system property `lxxv.cache.dir` (defaults to `./cache`).

## Features

- **Thread-safe** access with ReadWriteLock
- **Automatic persistence** to disk after each write
- **Three data sections**:
  - `plugins` - Plugin-specific data
  - `players` - Player-specific data organized by UUID
  - `global` - Shared global data across all plugins
- **Singleton pattern** for consistent instance
- **GSON integration** for JSON serialization

## Usage Examples

### Initialize Database
```java
SharedPluginDatabase db = SharedPluginDatabase.getInstance();
```

### Plugin Data
```java
// Set a value
db.setPluginValue("MyPlugin", "config_version", new JsonPrimitive("1.0"));

// Get a value
JsonElement value = db.getPluginValue("MyPlugin", "config_version");

// Get plugin's entire data object
JsonObject pluginData = db.getPluginData("MyPlugin");
```

### Player Data
```java
String playerUUID = player.getUniqueId().toString();

// Set player data
db.setPlayerValue(playerUUID, "level", new JsonPrimitive(10));
db.setPlayerValue(playerUUID, "balance", new JsonPrimitive(1000.50));

// Get player data
JsonElement level = db.getPlayerValue(playerUUID, "level");

// Get player's entire data object
JsonObject playerData = db.getPlayerData(playerUUID);
```

### Global Data
```java
// Set global value
db.setGlobalValue("server_name", new JsonPrimitive("MyServer"));
db.setGlobalValue("maintenance_mode", new JsonPrimitive(false));

// Get global value
JsonElement serverName = db.getGlobalValue("server_name");
```

### Advanced Operations
```java
// Get entire database
JsonObject allData = db.getAllData();

// Get database file path
String path = db.getDatabasePath();

// Reload from disk
db.reload();

// Clear all data
db.clear();

// Manual save (usually not needed, happens automatically)
db.save();
```

## Database Structure

```json
{
  "plugins": {
    "Ranks": {
      "version": "1.0",
      "last_updated": 1234567890
    },
    "Economy": {
      "currency_symbol": "$",
      "conversion_rate": 1.5
    }
  },
  "players": {
    "550e8400-e29b-41d4-a716-446655440000": {
      "name": "PlayerName",
      "level": 10,
      "balance": 1000.50,
      "custom_data": {}
    }
  },
  "global": {
    "server_name": "MyServer",
    "maintenance_mode": false,
    "total_players": 42
  },
  "timestamp": 1704988800000
}
```

## Configuration

Set the cache directory via system property before server startup:

```bash
# Linux/Mac
java -Dlxxv.cache.dir=/var/lib/lxxv/cache ...

# Windows
java -Dlxxv.cache.dir=C:\lxxv\cache ...
```

Or set it in your launcher script. Default is `./cache` relative to server directory.

## Best Practices

1. **Always use descriptive keys** - Use snake_case for consistency
2. **Organize nested data** - Use JsonObjects for complex data structures
3. **Handle null values** - Check if value exists before using
4. **Avoid circular dependencies** - Don't create plugin interdependencies through database
5. **Regular backups** - Back up the JSON file alongside your server backups

## Thread Safety

The database uses `ReadWriteLock` for thread-safe access:
- Multiple plugins can read simultaneously
- Only one plugin can write at a time
- Automatic locking/unlocking in public methods

## Performance Considerations

- Database is loaded entirely into memory
- All writes trigger disk I/O (use batching for multiple updates)
- For high-frequency updates, batch writes when possible:

```java
SharedPluginDatabase db = SharedPluginDatabase.getInstance();
// Update multiple values
db.setPluginValue("MyPlugin", "stat1", new JsonPrimitive(100));
db.setPluginValue("MyPlugin", "stat2", new JsonPrimitive(200));
// Disk write happens after each setPluginValue
// Consider wrapping in transaction logic if needed
```

## Migration from Plugin-Specific Storage

To migrate existing plugin data to shared database:

```java
// Old way (plugin-specific file)
// Load from plugins/MyPlugin/data.json

// New way
SharedPluginDatabase db = SharedPluginDatabase.getInstance();
db.setPluginValue("MyPlugin", "config", jsonData);
```

## Troubleshooting

**Database file not found**: Ensure `lxxv.cache.dir` system property is set or `./cache` directory is writable

**Permission errors**: Check file permissions on cache directory

**Data not persisting**: Database automatically saves after each write - check logs for I/O errors

**Memory issues with large databases**: Consider archiving old player data or implementing cleanup tasks
