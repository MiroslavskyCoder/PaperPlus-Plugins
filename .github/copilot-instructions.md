# AI Coding Agent Instructions - My Polyglot Project

A 70+ plugin Minecraft server ecosystem with Web Dashboard, using loose coupling via reflection and JSON-based data persistence.

## Architecture Overview

### Multi-Module Gradle Project
- **Root build**: Aggregates 70+ plugins via `gradle buildAllPlugins` (outputs to `out/plugins/`)
- **Common module** (`common/`): Shared utilities, JavaScript engine (Javet/SWC4J), REST API framework (Javalin)
- **Web Dashboard** (`packages/webx-dashboard/`): React/Next.js UI + Java REST API backend
- **Package plugins** (`packages/{name}/`): Individual features (economy, shop, clans, etc.)

**Key principle**: Plugins communicate via reflection + shared JSON database, NOT hard dependencies.

## Critical Workflows

### Build All Plugins
```bash
gradle buildAllPlugins
# Builds ~70 plugins → out/plugins/*.jar (≈26 seconds)
```

### Build Single Plugin
```bash
gradle :economy:build          # Outputs to build/libs/economy-0.1.0.jar
gradle :clans:build
```

### Copy to Server
```bash
copy out\plugins\*.jar "C:\Game Servers\Paper\plugins\"  # Windows
cp out/plugins/*.jar /opt/gameservers/paper/plugins/      # Linux
```

### Test Integration
See [INTEGRATION_GUIDE.md](../INTEGRATION_GUIDE.md) for plugin inter-communication patterns and API endpoints.

## Data Storage Pattern

### SharedPluginDatabase (JSON-based)
Located at `{CACHE}/lxxv_plugins_server.json` (default cache dir = `./cache`)

**Three sections:**
- `plugins` - Plugin-specific config/state
- `players` - Per-player data (keyed by UUID)
- `global` - Server-wide shared data

**Usage:**
```java
SharedPluginDatabase db = SharedPluginDatabase.getInstance();

// Player data
db.setPlayerValue(playerUUID, "balance", new JsonPrimitive(1000.50));
JsonElement balance = db.getPlayerValue(playerUUID, "balance");

// Plugin data
db.setPluginValue("Economy", "version", new JsonPrimitive("0.1.0"));

// Global data
db.setGlobalValue("server_name", new JsonPrimitive("MyServer"));
```

**Thread-safe** (ReadWriteLock), auto-persists after writes.

## Cross-Plugin Communication

### Pattern 1: Reflection-Based Access (Recommended)
Use reflection to call methods on other plugins without compile-time dependency.

```java
// In combat listener, trigger economy plugin
Plugin ecoPlugin = plugin.getServer().getPluginManager().getPlugin("economy");
if (ecoPlugin != null) {
    Method addCoinsMethod = ecoPlugin.getClass().getMethod("addCoins", UUID.class, double.class);
    addCoinsMethod.invoke(ecoPlugin, playerUUID, 1.0);
}
```

**Why**: Avoids hard dependencies, plugins can be disabled independently.

### Pattern 2: Shared JSON Database
Store data that other plugins query directly from `SharedPluginDatabase`.

```java
// Economy plugin persists balance
db.setPlayerValue(uuid, "coins", new JsonPrimitive(balance));

// Shop plugin reads balance
JsonElement coinsElem = db.getPlayerValue(uuid, "coins");
```

**Why**: Clean separation, no method dependencies.

### Pattern 3: REST API (Web Dashboard)
The web dashboard (`packages/webx-dashboard/`) exposes REST endpoints that plugins can call or external systems can query.

**Common endpoints** (see [INTEGRATION_GUIDE.md](../INTEGRATION_GUIDE.md)):
- `GET /api/player/{uuid}/coins` - Get player balance
- `GET /api/shop` - Get shop items
- `GET /api/clans/{clanTag}` - Get clan info
- `POST /api/transaction` - Record transaction

## Plugin Structure Convention

### Standard Layout
```
packages/{plugin-name}/
├── build.gradle.kts                    # Java 17+, GSON, Paper 1.20.4 API
├── README.md                           # Plugin description & commands
└── src/main/java/com/webx/{name}/
    ├── {Name}Plugin.java              # Main class, extends JavaPlugin
    ├── listeners/                     # Event handlers
    ├── commands/                      # Command executors
    ├── managers/                      # Business logic
    ├── models/                        # POJOs/Records
    ├── storage/                       # Persistence
    └── utils/                         # Utilities
```

### Main Plugin Class Template
```java
public class EconomyPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        // Initialize managers, register listeners, load config
        getServer().getPluginManager().registerEvents(new MobKillRewardListener(this), this);
        getCommand("balance").setExecutor(new BalanceCommand(this));
        getLogger().info("Economy plugin enabled!");
    }
}
```

### Dependency Setup (build.gradle.kts)
```kotlin
dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation(project(":common"))  // For SharedPluginDatabase
}
```

## Web Dashboard Integration

### REST API Service Pattern
Dashboard plugins expose REST endpoints via `Javalin` framework (common module).

**File**: `packages/webx-dashboard/src/main/java/com/webx/api/RouterProvider.java`

```java
// Register route
app.get("/api/player/:uuid/coins", ctx -> {
    String uuid = ctx.pathParam("uuid");
    // Query SharedPluginDatabase or call plugin method via reflection
    JsonElement coins = db.getPlayerValue(uuid, "coins");
    ctx.json(Map.of("coins", coins));
});
```

### JavaScript/TypeScript Support
Common module includes Javet (Java V8 engine) + SWC4J (TypeScript transpiler).

**Use case**: Dynamic script execution for configuration or game logic.

```java
Javet engine = new JavetEngine();
engine.eval("const sum = 1 + 2; sum;"); // → 3
```

## Key Integration Examples

### 1. Economy → Combat (Mob Kill Rewards)
- **Combat** plugin listens for `EntityDamageByEntityEvent`
- On mob kill, calls **Economy** plugin's `addCoins()` method via reflection
- Balance stored in `SharedPluginDatabase` under `players.{uuid}.coins`

### 2. Shop → Economy (Purchase Verification)
- **Shop** plugin opens GUI, reads item prices from `shop.json`
- On purchase, checks player balance via `SharedPluginDatabase`
- Deducts coins via reflection call to Economy's `removeCoins()`

### 3. Clans → Economy (Clan Power Calculation)
- **Clans** plugin auto-updates `Clan Power` (sum of all members' coins) every 5 minutes
- Reads each player's balance from `SharedPluginDatabase`
- Uses for leaderboard rankings in Web Dashboard

## Coding Conventions

- **Encoding**: UTF-8 (`compileJava.options.encoding = "UTF-8"`)
- **Java version**: 17+ (Paper 1.20.4+)
- **File naming**: `CamelCase` for classes, `lowercase_with_underscores.json` for configs
- **Logging**: Use `getLogger().info(msg)`, `getLogger().warning(msg)` (not System.out)
- **JSON**: GSON for serialization, store configs in `plugins/{name}/{name}.json`
- **Async tasks**: Use `Bukkit.getScheduler().runTaskAsyncTimer()` for background tasks
- **Reflection safety**: Wrap in try-catch, check `plugin != null` before calling

## Common Pitfalls to Avoid

1. **Hard dependencies between plugins**: Use reflection instead of `implementation(project(":otherPlugin"))`
2. **Blocking server thread**: Wrap I/O and heavy computation in `runTaskAsync()`
3. **Forgetting to unregister listeners**: Store listener references, unregister in `onDisable()`
4. **Not checking player permission**: Always call `player.hasPermission(perm)` before actions
5. **Database writes without sync**: `SharedPluginDatabase` is thread-safe but mutations should be guarded

## File Location Reference

| Component | Path |
|-----------|------|
| Root Gradle config | [settings.gradle.kts](../settings.gradle.kts), [build.gradle.kts](../build.gradle.kts) |
| Shared utilities | [common/](../common) |
| Web Dashboard UI | [packages/webx-dashboard/](../packages/webx-dashboard) |
| REST API routes | [packages/webx-dashboard/src/main/java/com/webx/api/RouterProvider.java](../packages/webx-dashboard/src/main/java/com/webx/api/RouterProvider.java) |
| Example plugin (Economy) | [packages/economy/](../packages/economy) |
| Shared database | [packages/shared-database/](../packages/shared-database) |
| Build output | `out/plugins/` |

## Useful Documentation

- [QUICK_START.md](../QUICK_START.md) - Build and deployment commands
- [INTEGRATION_GUIDE.md](../INTEGRATION_GUIDE.md) - Cross-plugin integration patterns & API endpoints
- [SYSTEM_OVERVIEW.md](../SYSTEM_OVERVIEW.md) - Clans, Economy, Shop system architecture
- [README.md](../README.md) - Full plugin catalog with descriptions
- [SHARED_DATABASE_GUIDE.md](../SHARED_DATABASE_GUIDE.md) - JSON persistence API reference
