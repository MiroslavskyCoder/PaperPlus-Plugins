# TypeScript Definitions for LoaderScript

Complete TypeScript type definitions and JSDoc for the LoaderScript JavaScript API.

## Files Added

1. **`types/type.d.ts`** - TypeScript declaration file with full type definitions
2. **`tsconfig.json`** - TypeScript configuration for the scripts folder
3. **`JSDoc.js`** - JSDoc definitions for IDE intellisense
4. **`.vscode/settings.json`** - VS Code settings for type checking

## Features

✅ **Full TypeScript Support** - Get autocomplete and type checking in VS Code
✅ **JSDoc Definitions** - Works with plain JavaScript files
✅ **Type Safety** - Catch errors before runtime
✅ **IDE Intellisense** - Full autocomplete for LXXVServer API
✅ **Documentation** - Inline docs with examples

## Setup

### Option 1: VS Code with TypeScript (Recommended)

1. Open the scripts folder in VS Code
2. Files automatically pick up types from `types/type.d.ts`
3. Enable TypeScript checking:
   ```json
   // .vscode/settings.json
   "javascript.checkJs": true
   ```

### Option 2: Use TypeScript Files

1. Rename `.js` files to `.ts`
2. Configure TypeScript compiler:
   ```bash
   npm install -D typescript
   npx tsc --project scripts/tsconfig.json
   ```

## Using Types

### In JavaScript with JSDoc

```javascript
/**
 * @param {Player} player
 * @param {string} message
 */
function sendWelcome(player, message) {
  LXXVServer.sendMessage(player, message);
}
```

### In TypeScript

```typescript
function sendWelcome(player: Player, message: string): void {
  LXXVServer.sendMessage(player, message);
}
```

## Type Definitions

### LXXVServer Namespace

All server functionality is available through the `LXXVServer` namespace:

```typescript
// Events
LXXVServer.on(eventName, listener);
LXXVServer.emit(eventName, data);

// Players
LXXVServer.getPlayer(name);
LXXVServer.getOnlinePlayers();
LXXVServer.sendMessage(player, message);
LXXVServer.kickPlayer(player, reason);

// Items & Teleport
LXXVServer.giveItem(player, itemType, amount);
LXXVServer.teleportPlayer(player, x, y, z, world);

// Commands
LXXVServer.registerCommand(name, executor);
LXXVServer.executeCommand(command);

// Scheduling
LXXVServer.schedule(delayMs, callback);
LXXVServer.scheduleRepeating(delayMs, intervalMs, callback);

// Configuration
LXXVServer.getConfig(key);
LXXVServer.setConfig(key, value);

// Server Info
LXXVServer.getServerInfo();

// Persistent Data
LXXVServer.saveData(key, value);
LXXVServer.loadData(key);
LXXVServer.deleteData(key);

// Messaging
LXXVServer.broadcast(message);
```

### Player Interface

```typescript
interface Player {
  name: string;
  uuid: string;
  level: number;
  experience: number;
  health: number;
  maxHealth: number;
  hunger: number;
  saturation: number;
  location: {
    x: number;
    y: number;
    z: number;
    world: string;
    yaw: number;
    pitch: number;
  };
  inventory: {
    items: Item[];
    armor: Item[];
  };
  isOnline(): boolean;
  isOp(): boolean;
  hasPermission(permission: string): boolean;
}
```

### Item Interface

```typescript
interface Item {
  type: string;
  amount: number;
  displayName?: string;
  lore?: string[];
  enchantments?: Record<string, number>;
  nbt?: Record<string, any>;
}
```

## Example with Types

### Welcome Script

```javascript
/**
 * @type {Map<string, number>}
 */
const joinCount = new Map();

/**
 * Handle player join
 * @param {Player} player
 */
function onPlayerJoin(player) {
  const count = joinCount.get(player.name) || 0;
  joinCount.set(player.name, count + 1);

  if (count === 0) {
    LXXVServer.broadcast(`§6${player.name} joined for the first time!`);
    LXXVServer.giveItem(player, 'DIAMOND', 1);
  } else {
    LXXVServer.sendMessage(
      player,
      `§aWelcome back! You've joined ${count + 1} times.`
    );
  }
}

LXXVServer.on('playerJoin', onPlayerJoin);
```

### Custom Command

```javascript
/**
 * @param {Player|string} sender
 * @param {string[]} args
 */
function tpToSpawn(sender, args) {
  LXXVServer.teleportPlayer(sender, 0, 64, 0, 'world');
  LXXVServer.sendMessage(sender, '§aTeleported to spawn!');
}

LXXVServer.registerCommand('spawn', tpToSpawn);
```

### Event System

```javascript
/**
 * Custom event type
 * @typedef {Object} ScoreEvent
 * @property {Player} player
 * @property {number} points
 */

/**
 * @param {ScoreEvent} event
 */
function onScoreEvent(event) {
  console.log(`${event.player.name} scored ${event.points} points!`);
}

LXXVServer.on('scoreEvent', onScoreEvent);

// Emit custom event
const event = {
  player: LXXVServer.getPlayer('Steve'),
  points: 100
};
LXXVServer.emit('scoreEvent', event);
```

## IDE Setup

### VS Code

1. **Install TypeScript extension** (built-in)
2. **Enable JS type checking**:
   ```json
   {
     "javascript.checkJs": true
   }
   ```
3. **Format on save**:
   ```json
   {
     "editor.formatOnSave": true,
     "editor.defaultFormatter": "esbenp.prettier-vscode"
   }
   ```

### WebStorm / IntelliJ IDEA

1. **Settings → Languages & Frameworks → JavaScript**
2. **Check "Analyze code"**
3. **Language version: ES2020**
4. **Add type definitions**:
   - Settings → Project Structure → Script Interpreters
   - Add Node.js with types

### Sublime Text

Install TypeScript plugin and configure:
```json
{
  "typescript_plugin": true,
  "auto_complete_triggers": [
    {"selector": "source.js - string - comment", "characters": "."}
  ]
}
```

## Console API

```typescript
/**
 * Global console for logging
 */
interface Console {
  log(...args: any[]): void;
  warn(...args: any[]): void;
  error(...args: any[]): void;
}
```

Usage:
```javascript
console.log('Information message');
console.warn('Warning message');
console.error('Error message');
```

## Event Types

### Built-in Events

- **playerJoin** - Player joined the server
- **playerLeave** - Player left the server
- **playerChat** - Player sent message
- **playerDeath** - Player died
- **blockBreak** - Block was broken
- **blockPlace** - Block was placed
- **itemPickup** - Item was picked up
- **itemDrop** - Item was dropped

### Custom Events

You can emit and listen to custom events:

```javascript
// Listen to custom event
LXXVServer.on('myEvent', (data) => {
  console.log('Custom event fired:', data);
});

// Emit custom event
LXXVServer.emit('myEvent', { custom: 'data' });
```

## Data Persistence

Store and retrieve data across server restarts:

```javascript
// Save data
LXXVServer.saveData('playerStats', {
  Steve: { level: 50, gold: 1000 },
  Alex: { level: 45, gold: 800 }
});

// Load data
const stats = LXXVServer.loadData('playerStats');
console.log(stats.Steve.level); // 50

// Delete data
LXXVServer.deleteData('playerStats');
```

## Configuration

Get and set server configuration:

```javascript
// Get max players
const maxPlayers = LXXVServer.getConfig('max-players');

// Set max players
LXXVServer.setConfig('max-players', 20);

// Get server info
const info = LXXVServer.getServerInfo();
console.log(`Server: ${info.name}`);
console.log(`Players: ${info.onlinePlayers}/${info.maxPlayers}`);
console.log(`TPS: ${info.tps}`);
```

## Best Practices

1. **Use type annotations** - Helps catch errors early
2. **Add JSDoc comments** - Improves IDE support
3. **Group related functions** - Better code organization
4. **Use const** - Prevents accidental reassignment
5. **Error handling** - Wrap async operations in try-catch
6. **Logging** - Use console.log for debugging

## Example Script with Full Types

```javascript
/**
 * Welcome System - Greets players and manages join counts
 * @requires types/type.d.ts
 */

/**
 * @type {Map<string, {joins: number, firstJoin: number}>}
 */
const playerData = new Map();

/**
 * Handle player join event
 * @param {Player} player - The player who joined
 */
function handlePlayerJoin(player) {
  let data = playerData.get(player.uuid) || {
    joins: 0,
    firstJoin: Date.now()
  };

  data.joins++;
  playerData.set(player.uuid, data);

  // Send welcome message
  const message = data.joins === 1
    ? `§6§lWelcome ${player.name}! You're new here!`
    : `§aWelcome back ${player.name}! (${data.joins} joins)`;

  LXXVServer.sendMessage(player, message);
  LXXVServer.broadcast(`§7${player.name} joined the game`);

  // Give first-time bonus
  if (data.joins === 1) {
    LXXVServer.giveItem(player, 'DIAMOND', 1);
    LXXVServer.executeCommand(`give ${player.name} apple 10`);
  }
}

// Register event listener
LXXVServer.on('playerJoin', handlePlayerJoin);

console.log('✅ Welcome system initialized');
```

## Troubleshooting

### No autocomplete?

1. Check `types/type.d.ts` exists in `scripts/types/`
2. Enable `javascript.checkJs` in VS Code
3. Reload VS Code (Ctrl+R)

### Type errors not showing?

1. Verify `tsconfig.json` is in scripts folder
2. Enable type checking in settings
3. Check Output panel for TypeScript errors

### JSDoc not working?

1. Ensure JSDoc comments are formatted correctly
2. Use `@param`, `@returns`, `@type` annotations
3. Reload editor after changes

---

**Status**: ✅ TypeScript support fully configured
**Location**: `scripts/types/type.d.ts`
**IDE Support**: VS Code, WebStorm, Sublime Text
