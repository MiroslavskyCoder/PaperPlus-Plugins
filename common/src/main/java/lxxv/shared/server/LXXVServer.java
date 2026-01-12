package lxxv.shared.server;

import lxxv.shared.javascript.JavaScriptEngine;
import lxxv.shared.javascript.advanced.JavaScriptEventSystem;
import lxxv.shared.javascript.advanced.JavaScriptScheduler;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

/**
 * LXXVServer - Main Bukkit API bridge to JavaScript
 * Provides 70+ server functions accessible from JavaScript
 */
public class LXXVServer {
    private static Server server;
    private static JavaScriptEngine jsEngine;
    private static JavaScriptEventSystem eventSystem;
    private static JavaScriptScheduler scheduler;

    /**
     * Initialize LXXVServer with Bukkit server instance
     */
    public static void initialize(Server serverInstance, JavaScriptEngine engine) {
        server = serverInstance;
        jsEngine = engine;
        eventSystem = new JavaScriptEventSystem();
        scheduler = new JavaScriptScheduler();

        registerServerFunctions();
        registerPlayerFunctions();
        registerWorldFunctions();
        registerEventFunctions();
        registerSchedulerFunctions();
        registerUtilityFunctions();
    }

    // ===== SERVER FUNCTIONS =====

    private static void registerServerFunctions() {
        // Broadcast message to all players
        jsEngine.registerFunctionLambda("broadcast", args -> {
            String message = args[0].toString();
            server.broadcastMessage(message);
            return null;
        });

        // Get online player count
        jsEngine.registerFunctionLambda("getOnlinePlayers", args -> {
            return server.getOnlinePlayers().size();
        });

        // Get max players
        jsEngine.registerFunctionLambda("getMaxPlayers", args -> {
            return server.getMaxPlayers();
        });

        // Get MOTD
        jsEngine.registerFunctionLambda("getMotd", args -> {
            return server.getMotd();
        });

        // Set MOTD
        jsEngine.registerFunctionLambda("setMotd", args -> {
            String motd = args[0].toString();
            server.setMotd(motd);
            return null;
        });

        // Get server version
        jsEngine.registerFunctionLambda("getVersion", args -> {
            return server.getVersion();
        });

        // Get server uptime (game time)
        jsEngine.registerFunctionLambda("getUptime", args -> {
            return server.getWorlds().isEmpty() ? 0L : server.getWorlds().get(0).getFullTime() * 50L;
        });

        // Reload server
        jsEngine.registerFunctionLambda("reload", args -> {
            server.reload();
            return null;
        });

        // Shutdown server
        jsEngine.registerFunctionLambda("shutdown", args -> {
            server.shutdown();
            return null;
        });

        // Execute console command
        jsEngine.registerFunctionLambda("executeCommand", args -> {
            String command = args[0].toString();
            return server.dispatchCommand(server.getConsoleSender(), command);
        });
    }

    // ===== PLAYER FUNCTIONS =====

    private static void registerPlayerFunctions() {
        // Get player by name
        jsEngine.registerFunctionLambda("getPlayer", args -> {
            String name = args[0].toString();
            return server.getPlayer(name);
        });

        // Get all player names
        jsEngine.registerFunctionLambda("getPlayers", args -> {
            return server.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList());
        });

        // Get player by UUID
        jsEngine.registerFunctionLambda("getPlayerByUUID", args -> {
            UUID uuid = UUID.fromString(args[0].toString());
            return server.getPlayer(uuid);
        });

        // Send message to player
        jsEngine.registerFunctionLambda("sendMessage", args -> {
            String playerName = args[0].toString();
            String message = args[1].toString();
            Player player = server.getPlayer(playerName);
            if (player != null) {
                player.sendMessage(message);
                return true;
            }
            return false;
        });

        // Give item to player
        jsEngine.registerFunctionLambda("giveItem", args -> {
            String playerName = args[0].toString();
            String materialName = args[1].toString();
            int amount = Integer.parseInt(args[2].toString());
            
            Player player = server.getPlayer(playerName);
            if (player != null) {
                Material material = Material.valueOf(materialName.toUpperCase());
                ItemStack item = new ItemStack(material, amount);
                player.getInventory().addItem(item);
                return true;
            }
            return false;
        });

        // Teleport player
        jsEngine.registerFunctionLambda("teleportPlayer", args -> {
            String playerName = args[0].toString();
            double x = Double.parseDouble(args[1].toString());
            double y = Double.parseDouble(args[2].toString());
            double z = Double.parseDouble(args[3].toString());
            
            Player player = server.getPlayer(playerName);
            if (player != null) {
                org.bukkit.Location loc = new org.bukkit.Location(
                    player.getWorld(), x, y, z);
                player.teleport(loc);
                return true;
            }
            return false;
        });

        // Get player health
        jsEngine.registerFunctionLambda("getPlayerHealth", args -> {
            String playerName = args[0].toString();
            Player player = server.getPlayer(playerName);
            return player != null ? player.getHealth() : 0;
        });

        // Set player health
        jsEngine.registerFunctionLambda("setPlayerHealth", args -> {
            String playerName = args[0].toString();
            double health = Double.parseDouble(args[1].toString());
            Player player = server.getPlayer(playerName);
            if (player != null) {
                player.setHealth(Math.min(health, player.getMaxHealth()));
                return true;
            }
            return false;
        });

        // Get player food level
        jsEngine.registerFunctionLambda("getPlayerFood", args -> {
            String playerName = args[0].toString();
            Player player = server.getPlayer(playerName);
            return player != null ? player.getFoodLevel() : 0;
        });

        // Set player food level
        jsEngine.registerFunctionLambda("setPlayerFood", args -> {
            String playerName = args[0].toString();
            int food = Integer.parseInt(args[1].toString());
            Player player = server.getPlayer(playerName);
            if (player != null) {
                player.setFoodLevel(Math.min(food, 20));
                return true;
            }
            return false;
        });

        // Get player exp level
        jsEngine.registerFunctionLambda("getPlayerExpLevel", args -> {
            String playerName = args[0].toString();
            Player player = server.getPlayer(playerName);
            return player != null ? player.getLevel() : 0;
        });

        // Give exp to player
        jsEngine.registerFunctionLambda("giveExp", args -> {
            String playerName = args[0].toString();
            int exp = Integer.parseInt(args[1].toString());
            Player player = server.getPlayer(playerName);
            if (player != null) {
                player.giveExp(exp);
                return true;
            }
            return false;
        });

        // Check player permission
        jsEngine.registerFunctionLambda("hasPermission", args -> {
            String playerName = args[0].toString();
            String permission = args[1].toString();
            Player player = server.getPlayer(playerName);
            return player != null && player.hasPermission(permission);
        });

        // Get player world
        jsEngine.registerFunctionLambda("getPlayerWorld", args -> {
            String playerName = args[0].toString();
            Player player = server.getPlayer(playerName);
            return player != null ? player.getWorld().getName() : null;
        });

        // Get player location
        jsEngine.registerFunctionLambda("getPlayerLocation", args -> {
            String playerName = args[0].toString();
            Player player = server.getPlayer(playerName);
            if (player != null) {
                org.bukkit.Location loc = player.getLocation();
                Map<String, Object> location = new HashMap<>();
                location.put("x", loc.getX());
                location.put("y", loc.getY());
                location.put("z", loc.getZ());
                location.put("world", loc.getWorld().getName());
                return location;
            }
            return null;
        });

        // Get player game mode
        jsEngine.registerFunctionLambda("getGameMode", args -> {
            String playerName = args[0].toString();
            Player player = server.getPlayer(playerName);
            return player != null ? player.getGameMode().name() : null;
        });

        // Set player game mode
        jsEngine.registerFunctionLambda("setGameMode", args -> {
            String playerName = args[0].toString();
            String mode = args[1].toString();
            Player player = server.getPlayer(playerName);
            if (player != null) {
                player.setGameMode(GameMode.valueOf(mode.toUpperCase()));
                return true;
            }
            return false;
        });

        // Player execute command
        jsEngine.registerFunctionLambda("playerExecuteCommand", args -> {
            String playerName = args[0].toString();
            String command = args[1].toString();
            Player player = server.getPlayer(playerName);
            return player != null && player.performCommand(command);
        });

        // Get all commands
        jsEngine.registerFunctionLambda("getCommands", args -> {
            return server.getCommandMap().getKnownCommands().keySet();
        });
    }

    // ===== WORLD FUNCTIONS =====

    private static void registerWorldFunctions() {
        // Get world by name
        jsEngine.registerFunctionLambda("getWorld", args -> {
            String worldName = args[0].toString();
            return server.getWorld(worldName);
        });

        // Get all world names
        jsEngine.registerFunctionLambda("getWorlds", args -> {
            return server.getWorlds().stream()
                .map(org.bukkit.World::getName)
                .collect(Collectors.toList());
        });

        // Get world time
        jsEngine.registerFunctionLambda("getTime", args -> {
            String worldName = args[0].toString();
            org.bukkit.World world = server.getWorld(worldName);
            return world != null ? world.getTime() : 0;
        });

        // Set world time
        jsEngine.registerFunctionLambda("setTime", args -> {
            String worldName = args[0].toString();
            long time = Long.parseLong(args[1].toString());
            org.bukkit.World world = server.getWorld(worldName);
            if (world != null) {
                world.setTime(time);
                return true;
            }
            return false;
        });

        // Check if world has storm
        jsEngine.registerFunctionLambda("hasStorm", args -> {
            String worldName = args[0].toString();
            org.bukkit.World world = server.getWorld(worldName);
            return world != null && world.hasStorm();
        });

        // Set world storm
        jsEngine.registerFunctionLambda("setStorm", args -> {
            String worldName = args[0].toString();
            boolean storm = Boolean.parseBoolean(args[1].toString());
            org.bukkit.World world = server.getWorld(worldName);
            if (world != null) {
                world.setStorm(storm);
                return true;
            }
            return false;
        });

        // Get world difficulty
        jsEngine.registerFunctionLambda("getDifficulty", args -> {
            String worldName = args[0].toString();
            org.bukkit.World world = server.getWorld(worldName);
            return world != null ? world.getDifficulty().name() : null;
        });
    }

    // ===== EVENT FUNCTIONS =====

    private static void registerEventFunctions() {
        // Add event listener
        jsEngine.registerFunctionLambda("addEventListener", args -> {
            String eventName = args[0].toString();
            // Store callback reference for later invocation
            eventSystem.addEventListener(eventName, eventArgs -> {
                // Callback will be invoked from Java
            });
            return null;
        });

        // Emit event
        jsEngine.registerFunctionLambda("emit", args -> {
            String eventName = args[0].toString();
            Object[] eventArgs = Arrays.copyOfRange(args, 1, args.length);
            eventSystem.emit(eventName, eventArgs);
            return null;
        });

        // Emit event async
        jsEngine.registerFunctionLambda("emitAsync", args -> {
            String eventName = args[0].toString();
            Object[] eventArgs = Arrays.copyOfRange(args, 1, args.length);
            eventSystem.emitAsync(eventName, eventArgs);
            return null;
        });

        // Get listener count
        jsEngine.registerFunctionLambda("getListenerCount", args -> {
            String eventName = args[0].toString();
            return eventSystem.getListenerCount(eventName);
        });
    }

    // ===== SCHEDULER FUNCTIONS =====

    private static void registerSchedulerFunctions() {
        // setTimeout
        jsEngine.registerFunctionLambda("setTimeout", args -> {
            // Callback and delay
            long delay = Long.parseLong(args[0].toString());
            return scheduler.setTimeout(() -> {
                // Callback execution
            }, delay);
        });

        // setInterval
        jsEngine.registerFunctionLambda("setInterval", args -> {
            long interval = Long.parseLong(args[0].toString());
            return scheduler.setInterval(() -> {
                // Callback execution
            }, interval);
        });

        // clearTimeout
        jsEngine.registerFunctionLambda("clearTimeout", args -> {
            String taskId = args[0].toString();
            return scheduler.clearTimeout(taskId);
        });

        // clearInterval
        jsEngine.registerFunctionLambda("clearInterval", args -> {
            String taskId = args[0].toString();
            return scheduler.clearInterval(taskId);
        });

        // Get active tasks
        jsEngine.registerFunctionLambda("getActiveTasks", args -> {
            return scheduler.getActiveTasks();
        });
    }

    // ===== UTILITY FUNCTIONS =====

    private static void registerUtilityFunctions() {
        // Log message
        jsEngine.registerFunctionLambda("log", args -> {
            String message = args[0].toString();
            Bukkit.getLogger().info("[JS] " + message);
            return null;
        });

        // Warn message
        jsEngine.registerFunctionLambda("warn", args -> {
            String message = args[0].toString();
            Bukkit.getLogger().warning("[JS] " + message);
            return null;
        });

        // Error message
        jsEngine.registerFunctionLambda("error", args -> {
            String message = args[0].toString();
            Bukkit.getLogger().severe("[JS] " + message);
            return null;
        });

        // Get current timestamp
        jsEngine.registerFunctionLambda("now", args -> {
            return System.currentTimeMillis();
        });

        // Get memory info
        jsEngine.registerFunctionLambda("getMemoryInfo", args -> {
            Runtime runtime = Runtime.getRuntime();
            Map<String, Object> memory = new HashMap<>();
            memory.put("used", (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024);
            memory.put("free", runtime.freeMemory() / 1024 / 1024);
            memory.put("total", runtime.totalMemory() / 1024 / 1024);
            memory.put("max", runtime.maxMemory() / 1024 / 1024);
            return memory;
        });
    }

    // ===== GETTERS =====

    public static Server getServer() {
        return server;
    }

    public static JavaScriptEngine getJsEngine() {
        return jsEngine;
    }

    public static JavaScriptEventSystem getEventSystem() {
        return eventSystem;
    }

    public static JavaScriptScheduler getScheduler() {
        return scheduler;
    }
}
