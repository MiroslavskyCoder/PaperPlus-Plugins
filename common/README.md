# LXXV Common Utilities

## Structure

```
common/
├── lxxv/
│   └── shared/
│       └── dbjson/
│           ├── SharedPluginDatabase.java
│           └── PluginDataHelper.java
└── build.gradle.kts
```

## Overview

This directory contains shared utilities used across all LXXV plugins:

### `lxxv.shared.dbjson`

**Shared Plugin Database System** - Centralized JSON-based data storage for all plugins.

**Features:**
- Single database file: `{CACHE}/lxxv_plugins_server.json`
- Thread-safe concurrent access
- Automatic disk persistence
- Three data sections: plugins, players, global
- Simple API with convenience helpers

**Location:** `{CACHE}/lxxv_plugins_server.json` (configurable via system property)

## Usage

### Import
```java
import lxxv.shared.dbjson.SharedPluginDatabase;
import lxxv.shared.dbjson.PluginDataHelper;
```

### Quick Start
```java
// Easy way - using helper
PluginDataHelper data = new PluginDataHelper("MyPlugin");
data.setString("version", "1.0");
data.setPlayerInt(uuid, "level", 50);

// Direct way - if needed
SharedPluginDatabase db = SharedPluginDatabase.getInstance();
db.setPluginValue("MyPlugin", "key", new JsonPrimitive("value"));
```

## Building

The common module is built as part of the main build:

```bash
gradle :common:build
```

Or as part of all plugins:
```bash
gradle buildAllPlugins
```

## Dependencies

- **Bukkit API** 1.20.4+ (provided scope)
- **GSON** 2.10.1 (bundled)

## Configuration

Set cache directory via system property before server startup:

```bash
# Linux/Mac
java -Dlxxv.cache.dir=/path/to/cache ...

# Windows  
java -Dlxxv.cache.dir=C:\lxxv\cache ...
```

Default: `./cache` relative to server directory

## Documentation

See parent directory for:
- [SHARED_DATABASE_GUIDE.md](../SHARED_DATABASE_GUIDE.md) - Complete API reference
- [SHARED_DATABASE_EXAMPLES.md](../SHARED_DATABASE_EXAMPLES.md) - Integration examples
- [packages/shared-database/README.md](../packages/shared-database/README.md) - Usage guide

## Future Extensions

Additional common utilities can be added under `lxxv.*` packages:

```
common/lxxv/
├── shared/
│   ├── dbjson/      (current)
│   ├── config/      (planned)
│   └── cache/       (planned)
├── utils/           (planned)
├── events/          (planned)
└── api/             (planned)
```

## Integration with Plugins

To use in a plugin:

1. Add dependency to your `build.gradle.kts`:
```kotlin
dependencies {
    implementation(project(":common"))
}
```

2. Import and use:
```java
import lxxv.shared.dbjson.PluginDataHelper;

PluginDataHelper data = new PluginDataHelper("YourPlugin");
```

## Version

**LXXV Common Utilities** v1.0.0
