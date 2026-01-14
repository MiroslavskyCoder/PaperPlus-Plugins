/**
 * @fileoverview JSDoc configuration for LoaderScript
 * This file enables TypeScript-like intellisense in JavaScript files
 */

/**
 * @typedef {Object} Player
 * @property {string} name - Player's display name
 * @property {string} uuid - Player's unique identifier
 * @property {number} level - Experience level
 * @property {number} health - Current health
 * @property {number} maxHealth - Maximum health
 * @property {Object} location - Player's location
 * @property {number} location.x - X coordinate
 * @property {number} location.y - Y coordinate
 * @property {number} location.z - Z coordinate
 * @property {string} location.world - World name
 */

/**
 * @typedef {Object} Item
 * @property {string} type - Item type (e.g., 'DIAMOND_SWORD')
 * @property {number} amount - Stack size
 * @property {string} [displayName] - Custom display name
 * @property {string[]} [lore] - Item lore
 */

/**
 * Main LXXVServer API
 * @namespace LXXVServer
 */

/**
 * Register an event listener
 * @function on
 * @param {string} eventName - Event name
 * @param {Function} listener - Listener callback
 * @example
 * LXXVServer.on('playerJoin', (player) => {
 *   console.log('Player joined: ' + player.name);
 * });
 */

/**
 * Emit a custom event
 * @function emit
 * @param {string} eventName - Event name
 * @param {*} data - Event data
 * @example
 * LXXVServer.emit('customEvent', { message: 'hello' });
 */

/**
 * Broadcast a message to all players
 * @function broadcast
 * @param {string} message - Message (supports color codes with §)
 * @example
 * LXXVServer.broadcast('§6§lServer Announcement!');
 */

/**
 * Register a command
 * @function registerCommand
 * @param {string} commandName - Command name (without /)
 * @param {Function} executor - Executor function
 * @example
 * LXXVServer.registerCommand('hello', (sender, args) => {
 *   LXXVServer.sendMessage(sender, 'Hello ' + args[0]);
 * });
 */

/**
 * Send a message to a player
 * @function sendMessage
 * @param {Player|string} player - Player object or name
 * @param {string} message - Message to send
 * @example
 * LXXVServer.sendMessage(player, '§aWelcome!');
 */

/**
 * Get a player by name
 * @function getPlayer
 * @param {string} playerName - Player name
 * @returns {Player|null} Player object or null
 * @example
 * const player = LXXVServer.getPlayer('Steve');
 */

/**
 * Get all online players
 * @function getOnlinePlayers
 * @returns {Player[]} Array of online players
 * @example
 * const players = LXXVServer.getOnlinePlayers();
 */

/**
 * Kick a player
 * @function kickPlayer
 * @param {Player|string} player - Player object or name
 * @param {string} [reason] - Kick reason
 * @example
 * LXXVServer.kickPlayer('Steve', 'You were kicked');
 */

/**
 * Give an item to a player
 * @function giveItem
 * @param {Player|string} player - Player object or name
 * @param {string} itemType - Item type
 * @param {number} amount - Amount to give
 * @example
 * LXXVServer.giveItem(player, 'DIAMOND_SWORD', 1);
 */

/**
 * Teleport a player
 * @function teleportPlayer
 * @param {Player|string} player - Player object or name
 * @param {number} x - X coordinate
 * @param {number} y - Y coordinate
 * @param {number} z - Z coordinate
 * @param {string} [world] - World name
 * @example
 * LXXVServer.teleportPlayer(player, 0, 64, 0, 'world');
 */

/**
 * Execute a command as console
 * @function executeCommand
 * @param {string} command - Command to execute
 * @example
 * LXXVServer.executeCommand('say Hello everyone!');
 */

/**
 * Schedule a callback
 * @function schedule
 * @param {number} delayMs - Delay in milliseconds
 * @param {Function} callback - Callback function
 * @example
 * LXXVServer.schedule(5000, () => {
 *   console.log('5 seconds passed');
 * });
 */

/**
 * Schedule a repeating callback
 * @function scheduleRepeating
 * @param {number} delayMs - Initial delay
 * @param {number} intervalMs - Repeat interval
 * @param {Function} callback - Callback function
 * @example
 * LXXVServer.scheduleRepeating(1000, 1000, () => {
 *   console.log('Repeats every second');
 * });
 */

/**
 * Get configuration value
 * @function getConfig
 * @param {string} key - Config key
 * @returns {*} Config value
 * @example
 * const maxPlayers = LXXVServer.getConfig('max-players');
 */

/**
 * Set configuration value
 * @function setConfig
 * @param {string} key - Config key
 * @param {*} value - Config value
 * @example
 * LXXVServer.setConfig('max-players', 20);
 */

/**
 * Get server information
 * @function getServerInfo
 * @returns {Object} Server info
 * @returns {string} Server info.name - Server name
 * @returns {string} Server info.version - Server version
 * @returns {number} Server info.maxPlayers - Max players
 * @returns {number} Server info.onlinePlayers - Online players
 * @returns {number} Server info.tps - Ticks per second
 * @example
 * const info = LXXVServer.getServerInfo();
 */

/**
 * Save persistent data
 * @function saveData
 * @param {string} key - Data key
 * @param {*} value - Value to save
 * @example
 * LXXVServer.saveData('playerScores', { Steve: 100 });
 */

/**
 * Load persistent data
 * @function loadData
 * @param {string} key - Data key
 * @returns {*} Loaded value or null
 * @example
 * const scores = LXXVServer.loadData('playerScores');
 */

/**
 * Delete persistent data
 * @function deleteData
 * @param {string} key - Data key
 * @example
 * LXXVServer.deleteData('playerScores');
 */

/**
 * Global console for logging
 * @type {Object}
 * @property {Function} log - Log message
 * @property {Function} warn - Log warning
 * @property {Function} error - Log error
 * @example
 * console.log('Hello World');
 * console.warn('Warning message');
 * console.error('Error message');
 */
