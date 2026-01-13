/**
 * LoaderScript TypeScript Definitions
 * Type definitions for LXXVServer API and global objects available in scripts
 */

/**
 * Event listener callback type
 */
type EventListener<T = any> = (data: T) => void | Promise<void>;

/**
 * Command executor callback type
 */
type CommandExecutor = (sender: any, args: string[]) => void | Promise<void>;

/**
 * Main LXXVServer namespace - provides access to server functionality
 */
declare namespace LXXVServer {
  /**
   * Register an event listener
   * @param eventName - Name of the event to listen for
   * @param listener - Callback function to execute when event fires
   * @example
   * LXXVServer.on('playerJoin', (player) => {
   *   console.log(player.name + ' joined!');
   * });
   */
  function on(eventName: string, listener: EventListener): void;

  /**
   * Emit a custom event
   * @param eventName - Name of the event
   * @param data - Data to pass to event listeners
   * @example
   * LXXVServer.emit('customEvent', { message: 'Hello' });
   */
  function emit(eventName: string, data?: any): void;

  /**
   * Broadcast a message to all online players
   * @param message - Message to broadcast
   * @example
   * LXXVServer.broadcast('§6§lServer Announcement!');
   */
  function broadcast(message: string): void;

  /**
   * Register a command
   * @param commandName - Name of the command (without /)
   * @param executor - Function to execute when command is used
   * @example
   * LXXVServer.registerCommand('hello', (sender, args) => {
   *   LXXVServer.sendMessage(sender, 'Hello, ' + args[0] + '!');
   * });
   */
  function registerCommand(commandName: string, executor: CommandExecutor): void;

  /**
   * Send a message to a specific player
   * @param player - Player object or player name
   * @param message - Message to send
   * @example
   * LXXVServer.sendMessage(player, '§aWelcome!');
   */
  function sendMessage(player: any, message: string): void;

  /**
   * Get a player by name
   * @param playerName - Name of the player to find
   * @returns Player object or null if not found
   * @example
   * const player = LXXVServer.getPlayer('Steve');
   */
  function getPlayer(playerName: string): any | null;

  /**
   * Get all online players
   * @returns Array of online player objects
   * @example
   * const players = LXXVServer.getOnlinePlayers();
   */
  function getOnlinePlayers(): any[];

  /**
   * Kick a player from the server
   * @param player - Player object or player name
   * @param reason - Kick reason message
   * @example
   * LXXVServer.kickPlayer('Steve', '§cViolated rules');
   */
  function kickPlayer(player: any, reason?: string): void;

  /**
   * Give an item to a player
   * @param player - Player object or player name
   * @param itemType - Type of item (e.g., 'DIAMOND_SWORD')
   * @param amount - Number of items to give
   * @example
   * LXXVServer.giveItem(player, 'DIAMOND_SWORD', 1);
   */
  function giveItem(player: any, itemType: string, amount: number): void;

  /**
   * Teleport a player to a location
   * @param player - Player object or player name
   * @param x - X coordinate
   * @param y - Y coordinate
   * @param z - Z coordinate
   * @param world - World name (optional, defaults to player's current world)
   * @example
   * LXXVServer.teleportPlayer(player, 0, 64, 0, 'world');
   */
  function teleportPlayer(
    player: any,
    x: number,
    y: number,
    z: number,
    world?: string
  ): void;

  /**
   * Execute a command as the console
   * @param command - Command to execute (without leading /)
   * @example
   * LXXVServer.executeCommand('say Hello everyone!');
   */
  function executeCommand(command: string): void;

  /**
   * Schedule a callback to execute after a delay
   * @param delayMs - Delay in milliseconds
   * @param callback - Function to execute
   * @example
   * LXXVServer.schedule(5000, () => {
   *   console.log('5 seconds have passed');
   * });
   */
  function schedule(delayMs: number, callback: () => void): void;

  /**
   * Schedule a repeating callback
   * @param delayMs - Delay in milliseconds
   * @param intervalMs - Interval in milliseconds
   * @param callback - Function to execute repeatedly
   * @example
   * LXXVServer.scheduleRepeating(1000, 1000, () => {
   *   console.log('Every second');
   * });
   */
  function scheduleRepeating(
    delayMs: number,
    intervalMs: number,
    callback: () => void
  ): void;

  /**
   * Get server configuration value
   * @param key - Configuration key
   * @returns Configuration value
   * @example
   * const maxPlayers = LXXVServer.getConfig('max-players');
   */
  function getConfig(key: string): any;

  /**
   * Set server configuration value
   * @param key - Configuration key
   * @param value - Value to set
   * @example
   * LXXVServer.setConfig('max-players', 20);
   */
  function setConfig(key: string, value: any): void;

  /**
   * Get server information
   * @returns Server info object
   * @example
   * const info = LXXVServer.getServerInfo();
   * console.log('Server running on ' + info.version);
   */
  function getServerInfo(): {
    name: string;
    version: string;
    maxPlayers: number;
    onlinePlayers: number;
    tps: number;
  };

  /**
   * Save data to persistent storage
   * @param key - Data key
   * @param value - Value to store (should be JSON-serializable)
   * @example
   * LXXVServer.saveData('myKey', { score: 100 });
   */
  function saveData(key: string, value: any): void;

  /**
   * Load data from persistent storage
   * @param key - Data key
   * @returns Stored value or null if not found
   * @example
   * const data = LXXVServer.loadData('myKey');
   */
  function loadData(key: string): any;

  /**
   * Delete data from persistent storage
   * @param key - Data key
   * @example
   * LXXVServer.deleteData('myKey');
   */
  function deleteData(key: string): void;
}

/**
 * Global console object for logging
 */
declare const console: {
  /**
   * Log a message
   */
  log(...args: any[]): void;

  /**
   * Log a warning
   */
  warn(...args: any[]): void;

  /**
   * Log an error
   */
  error(...args: any[]): void;
};

/**
 * Player object interface
 */
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

/**
 * Item object interface
 */
interface Item {
  type: string;
  amount: number;
  displayName?: string;
  lore?: string[];
  enchantments?: Record<string, number>;
  nbt?: Record<string, any>;
}

/**
 * Event data interfaces
 */
interface PlayerJoinEvent {
  player: Player;
  joinMessage: string;
}

interface PlayerLeaveEvent {
  player: Player;
  leaveMessage: string;
}

interface PlayerChatEvent {
  player: Player;
  message: string;
  recipients: Player[];
}

interface CommandEvent {
  sender: Player | string;
  command: string;
  args: string[];
  cancelled: boolean;
}

interface CustomEvent {
  [key: string]: any;
}

/**
 * Global this context in scripts
 */
declare global {
  var LXXVServer: typeof LXXVServer;
  var console: typeof console;
}

export {};
