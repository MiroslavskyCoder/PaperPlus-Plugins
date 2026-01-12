# LoaderScript Plugin

JavaScript script loader for Minecraft Paper/Spigot servers with hot-reload and web management.

## Features

- ✅ Load JavaScript/TypeScript files from `scripts/` folder
- ✅ Hot-reload scripts without server restart
- ✅ Web dashboard integration for script management
- ✅ Full LXXVServer API support (70+ Bukkit functions)
- ✅ Automatic TypeScript/JSX transpilation with swc4j
- ✅ REST API for remote script control
- ✅ In-game commands for script management
- ✅ Script templates and error reporting

## Installation

1. Copy `LoaderScript.jar` to `plugins/` folder
2. Install `common.jar` (LXXV Common module) to `plugins/` folder
3. Install `webx-dashboard.jar` (WebX Dashboard) to `plugins/` folder for web UI
4. Restart server
5. Scripts folder will be created at: `scripts/` (in server root)

## How It Works

### Architecture

```
LoaderScript Plugin
├── ScriptManager (load/reload/unload scripts)
├── ScriptAPIController (REST API endpoints)
└── LoaderScriptDashboardIntegration (WebX Dashboard integration)
    └── Registers API routes on WebX Dashboard Javalin server
```

### Plugin Registration

LoaderScript **does NOT** start its own REST API server. Instead, it provides API routes that are registered with **WebX Dashboard's** Javalin server.

When WebX Dashboard initializes, it calls:
```java
LoaderScriptDashboardIntegration.registerWithDashboard(javalinApp);
```

This registers all LoaderScript API routes on the WebX Dashboard Javalin instance.

## Usage

### In-Game Commands

```
/loaderscript list              - List all scripts
/loaderscript load <script>     - Load a script
/loaderscript reload <script>   - Reload a script
/loaderscript unload <script>   - Unload a script
/loaderscript reload-all        - Reload all scripts
/loaderscript info <script>     - Show script info
/loaderscript create <name>     - Create new script template
/loaderscript delete <script>   - Delete a script
```

**Aliases:** `/ls`, `/lscript`

### Web Dashboard

Access the script manager at: `http://localhost:8080/dashboard` (WebX Dashboard)

**API Endpoints** (on WebX Dashboard port, not separate):

**API Endpoints:**

```
GET    /api/loaderscript/scripts              - List all scripts
GET    /api/loaderscript/scripts/:name        - Get script content
POST   /api/loaderscript/scripts              - Create new script
PUT    /api/loaderscript/scripts/:name        - Update script
DELETE /api/loaderscript/scripts/:name        - Delete script

POST   /api/loaderscript/scripts/:name/load   - Load script
POST   /api/loaderscript/scripts/:name/reload - Reload script
POST   /api/loaderscript/scripts/:name/unload - Unload script
POST   /api/loaderscript/reload-all           - Reload all

POST   /api/loaderscript/execute              - Execute JS code
POST   /api/loaderscript/transpile            - Transpile TS code
GET    /api/loaderscript/info                 - Get plugin info
```

## Writing Scripts

### Basic Script Template

```javascript
/**
 * My Script - Description
 */

// Initialize
console.log('§a[MyScript] Loaded!');

// Event handlers
LXXVServer.on('playerJoin', (player) => {
    const name = player.getName();
    LXXVServer.sendMessage(name, '§aWelcome!');
});

// Commands
LXXVServer.registerCommand('mycommand', (player, args) => {
    LXXVServer.sendMessage(player.getName(), '§eCommand executed!');
});

// Scheduler
LXXVServer.runTaskTimer(() => {
    LXXVServer.broadcast('§7Periodic message');
}, 0, 6000); // Every 5 minutes
```

### TypeScript Support

Create `.ts` files and they will be automatically transpiled:

```typescript
interface Player {
    getName(): string;
    getHealth(): number;
}

LXXVServer.on('playerJoin', (player: Player) => {
    const name: string = player.getName();
    LXXVServer.sendMessage(name, `§aWelcome, ${name}!`);
});
```

### Available APIs

Scripts have access to **LXXVServer API** with 70+ functions:

**Server:**
- `getOnlinePlayers()`, `getMaxPlayers()`, `broadcast()`, `executeCommand()`

**Players:**
- `sendMessage()`, `sendTitle()`, `sendActionBar()`, `teleport()`, `giveItem()`, `kickPlayer()`

**World:**
- `setBlock()`, `getBlock()`, `spawnEntity()`, `setGameRule()`, `saveAllWorlds()`

**Events:**
- `on('playerJoin', callback)`, `on('playerQuit', callback)`, `on('playerDeath', callback)`

**Scheduler:**
- `runTask()`, `runTaskLater()`, `runTaskTimer()`, `cancelTask()`

**Scoreboard:**
- `createObjective()`, `setScore()`, `createTeam()`, `addToTeam()`

**Boss Bar:**
- `createBossBar()`, `showBossBar()`, `removeBossBar()`

**Vault (Economy/Permissions):**
- `getBalance()`, `deposit()`, `withdraw()`, `hasPermission()`, `addPermission()`

**Sounds & Particles:**
- `playSound()`, `stopSound()`, `spawnParticle()`

See `scripts/README.md` for full API documentation.

## Configuration

Edit `plugins/LoaderScript/config.yml`:

```yaml
# Auto-load scripts on startup
auto-load: true

# Script execution timeout (ms)
script-timeout: 30000

# Enable debug logging
debug: false
```

## Examples

Example scripts are available in `scripts/` folder:

- `welcome.js` - Welcome messages with titles and sounds
- `daily-rewards.js` - Daily rewards system with Vault integration
- `custom-events.js` - Custom server events with boss bars
- `auto-restart.js` - Automatic server restart scheduler
- `scoreboard-manager.js` - Dynamic player scoreboards

## Integration with WebX Dashboard

### For WebX Dashboard Developers

To integrate LoaderScript API with WebX Dashboard:

```java
import com.webx.loaderscript.integration.LoaderScriptDashboardIntegration;
import io.javalin.Javalin;

// When initializing WebX Dashboard Javalin server
Javalin app = Javalin.create().start(8080);

// Register LoaderScript API routes if available
if (LoaderScriptDashboardIntegration.isLoaderScriptAvailable()) {
    LoaderScriptDashboardIntegration.registerWithDashboard(app);
    logger.info("LoaderScript API registered with WebX Dashboard");
}
```

### API Controller Access

```java
LoaderScriptPlugin loaderScript = LoaderScriptDashboardIntegration.getLoaderScript();
if (loaderScript != null) {
    // Access script manager
    ScriptManager manager = loaderScript.getScriptManager();
    
    // Access JavaScript engine
    JavaScriptEngine engine = loaderScript.getJavaScriptEngine();
    
    // Access API controller
    ScriptAPIController controller = loaderScript.getAPIController();
    controller.registerRoutes(app); // Register routes
}
```

## Permissions

```
loaderscript.admin  - All LoaderScript commands (default: op)
loaderscript.user   - Basic user commands (default: true)
```

## Dependencies

- **Paper API** 1.20.4+
- **LXXV Common** (included in build)
- **Vault** (optional, for economy/permissions)
- **WebX Dashboard** (optional, for web UI)

## Building

```bash
./gradlew :loaderscript:build
```

Output: `packages/loaderscript/build/libs/loaderscript-1.0.0.jar`

## Support

- **Documentation:** `scripts/README.md`
- **LXXVServer API:** `common/src/main/java/lxxv/shared/server/LXXVServer.java`
- **Issues:** Create issue in project repository

## License

MIT License
