package lxxv.shared.server;

import lxxv.shared.javascript.JavaScriptEngine;
import lxxv.shared.javascript.advanced.JavaScriptEventSystem;
import lxxv.shared.javascript.advanced.JavaScriptScheduler;
import org.bukkit.Bukkit;
import org.bukkit.block.Biome;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.Server;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.util.Vector;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

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
    private static final Map<String, BossBar> bossBars = new HashMap<>();
    private static Economy economy;
    private static Permission permissions;

    /**
     * Initialize LXXVServer with Bukkit server instance
     */
    public static void initialize(Server serverInstance, JavaScriptEngine engine) {
        server = serverInstance;
        jsEngine = engine;
        eventSystem = new JavaScriptEventSystem();
        scheduler = new JavaScriptScheduler();

        registerConsoleFunctions();
        registerServerFunctions();
        registerPlayerFunctions();
        registerWorldFunctions();
        registerEventFunctions();
        registerSchedulerFunctions();
        registerUtilityFunctions();
        registerEntityFunctions();
        registerScoreboardFunctions();
        registerChatFunctions();
        registerSoundParticleFunctions();
        registerVaultFunctions();
    }

    // ===== CONSOLE FUNCTIONS =====

    private static void registerConsoleFunctions() {
        // console.log - output to server console with prefix
        jsEngine.registerFunctionLambda("console.log", args -> {
            StringBuilder message = new StringBuilder("[LXXV Engine]: ");
            for (int i = 0; i < args.length; i++) {
                if (i > 0) message.append(" ");
                message.append(args[i] != null ? args[i].toString() : "null");
            }
            server.getLogger().info(message.toString());
            return null;
        });

        // console.warn - warning messages
        jsEngine.registerFunctionLambda("console.warn", args -> {
            StringBuilder message = new StringBuilder("[LXXV Engine]: ");
            for (int i = 0; i < args.length; i++) {
                if (i > 0) message.append(" ");
                message.append(args[i] != null ? args[i].toString() : "null");
            }
            server.getLogger().warning(message.toString());
            return null;
        });

        // console.error - error messages
        jsEngine.registerFunctionLambda("console.error", args -> {
            StringBuilder message = new StringBuilder("[LXXV Engine]: ");
            for (int i = 0; i < args.length; i++) {
                if (i > 0) message.append(" ");
                message.append(args[i] != null ? args[i].toString() : "null");
            }
            server.getLogger().severe(message.toString());
            return null;
        });
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

        // Kick player by name with optional reason
        jsEngine.registerFunctionLambda("kickPlayer", args -> {
            String playerName = args[0].toString();
            String reason = args.length > 1 ? args[1].toString() : "Kicked by server";
            Player player = server.getPlayer(playerName);
            if (player != null) {
                player.kickPlayer(reason);
                return true;
            }
            return false;
        });

        // Whitelist toggle for a player
        jsEngine.registerFunctionLambda("setWhitelisted", args -> {
            String playerName = args[0].toString();
            boolean value = Boolean.parseBoolean(args[1].toString());
            org.bukkit.OfflinePlayer offline = server.getOfflinePlayer(playerName);
            offline.setWhitelisted(value);
            return true;
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

        // Clear player inventory
        jsEngine.registerFunctionLambda("clearInventory", args -> {
            String playerName = args[0].toString();
            Player player = server.getPlayer(playerName);
            if (player != null) {
                player.getInventory().clear();
                return true;
            }
            return false;
        });

        // Play sound to player
        jsEngine.registerFunctionLambda("playSound", args -> {
            String playerName = args[0].toString();
            String soundName = args[1].toString();
            float volume = Float.parseFloat(args.length > 2 ? args[2].toString() : "1.0");
            float pitch = Float.parseFloat(args.length > 3 ? args[3].toString() : "1.0");
            Player player = server.getPlayer(playerName);
            if (player != null) {
                try {
                    Sound sound = Sound.valueOf(soundName.toUpperCase());
                    player.playSound(player.getLocation(), sound, volume, pitch);
                    return true;
                } catch (IllegalArgumentException e) {
                    return false;
                }
            }
            return false;
        });

        // Add potion effect
        jsEngine.registerFunctionLambda("addPotionEffect", args -> {
            String playerName = args[0].toString();
            String effectName = args[1].toString();
            int durationTicks = Integer.parseInt(args[2].toString());
            int amplifier = Integer.parseInt(args[3].toString());
            Player player = server.getPlayer(playerName);
            if (player != null) {
                PotionEffectType type = PotionEffectType.getByName(effectName.toUpperCase());
                if (type != null) {
                    player.addPotionEffect(new PotionEffect(type, durationTicks, amplifier));
                    return true;
                }
            }
            return false;
        });

        // Set walk speed
        jsEngine.registerFunctionLambda("setWalkSpeed", args -> {
            String playerName = args[0].toString();
            float speed = Float.parseFloat(args[1].toString());
            Player player = server.getPlayer(playerName);
            if (player != null) {
                player.setWalkSpeed(Math.max(-1.0f, Math.min(1.0f, speed)));
                return true;
            }
            return false;
        });

        // Set fly speed
        jsEngine.registerFunctionLambda("setFlySpeed", args -> {
            String playerName = args[0].toString();
            float speed = Float.parseFloat(args[1].toString());
            Player player = server.getPlayer(playerName);
            if (player != null) {
                player.setFlySpeed(Math.max(-1.0f, Math.min(1.0f, speed)));
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

        // Get inventory snapshot (material names)
        jsEngine.registerFunctionLambda("getInventory", args -> {
            String playerName = args[0].toString();
            Player player = server.getPlayer(playerName);
            if (player == null) return null;
            ItemStack[] contents = player.getInventory().getContents();
            List<String> slots = new ArrayList<>();
            for (ItemStack item : contents) {
                slots.add(item == null ? "AIR" : item.getType().name());
            }
            return slots;
        });

        // Set inventory slot
        jsEngine.registerFunctionLambda("setInventorySlot", args -> {
            String playerName = args[0].toString();
            int slot = Integer.parseInt(args[1].toString());
            String materialName = args[2].toString();
            int amount = Integer.parseInt(args.length > 3 ? args[3].toString() : "1");
            Player player = server.getPlayer(playerName);
            if (player != null) {
                try {
                    Material material = Material.valueOf(materialName.toUpperCase());
                    ItemStack item = new ItemStack(material, amount);
                    player.getInventory().setItem(slot, item);
                    return true;
                } catch (IllegalArgumentException e) {
                    return false;
                }
            }
            return false;
        });

        // Set item name and lore in slot
        jsEngine.registerFunctionLambda("setItemMeta", args -> {
            String playerName = args[0].toString();
            int slot = Integer.parseInt(args[1].toString());
            String displayName = args.length > 2 ? args[2].toString() : null;
            @SuppressWarnings("unchecked")
            List<String> lore = args.length > 3 && args[3] instanceof List ? (List<String>) args[3] : null;
            Player player = server.getPlayer(playerName);
            if (player != null) {
                ItemStack item = player.getInventory().getItem(slot);
                if (item == null) return false;
                ItemMeta meta = item.getItemMeta();
                if (meta == null) return false;
                if (displayName != null) meta.setDisplayName(displayName);
                if (lore != null) meta.setLore(lore);
                item.setItemMeta(meta);
                player.getInventory().setItem(slot, item);
                return true;
            }
            return false;
        });

        // Add enchant to slot item
        jsEngine.registerFunctionLambda("addEnchant", args -> {
            String playerName = args[0].toString();
            int slot = Integer.parseInt(args[1].toString());
            String enchantName = args[2].toString();
            int level = Integer.parseInt(args.length > 3 ? args[3].toString() : "1");
            Player player = server.getPlayer(playerName);
            if (player != null) {
                ItemStack item = player.getInventory().getItem(slot);
                if (item == null) return false;
                ItemMeta meta = item.getItemMeta();
                if (meta == null) return false;
                Enchantment enchant = Enchantment.getByName(enchantName.toUpperCase());
                if (enchant == null) return false;
                meta.addEnchant(enchant, level, true);
                item.setItemMeta(meta);
                player.getInventory().setItem(slot, item);
                return true;
            }
            return false;
        });

        // Set persistent string tag on slot item
        jsEngine.registerFunctionLambda("setItemTag", args -> {
            String playerName = args[0].toString();
            int slot = Integer.parseInt(args[1].toString());
            String keyName = args[2].toString();
            String value = args[3].toString();
            Player player = server.getPlayer(playerName);
            if (player != null) {
                ItemStack item = player.getInventory().getItem(slot);
                if (item == null) return false;
                ItemMeta meta = item.getItemMeta();
                if (meta == null) return false;
                NamespacedKey key = NamespacedKey.minecraft(keyName.toLowerCase());
                meta.getPersistentDataContainer().set(key, org.bukkit.persistence.PersistentDataType.STRING, value);
                item.setItemMeta(meta);
                player.getInventory().setItem(slot, item);
                return true;
            }
            return false;
        });

        // Get persistent string tag from slot item
        jsEngine.registerFunctionLambda("getItemTag", args -> {
            String playerName = args[0].toString();
            int slot = Integer.parseInt(args[1].toString());
            String keyName = args[2].toString();
            Player player = server.getPlayer(playerName);
            if (player != null) {
                ItemStack item = player.getInventory().getItem(slot);
                if (item == null) return null;
                ItemMeta meta = item.getItemMeta();
                if (meta == null) return null;
                NamespacedKey key = NamespacedKey.minecraft(keyName.toLowerCase());
                return meta.getPersistentDataContainer().get(key, org.bukkit.persistence.PersistentDataType.STRING);
            }
            return null;
        });

        // Set armor/offhand slots
        jsEngine.registerFunctionLambda("setArmor", args -> {
            String playerName = args[0].toString();
            String helmet = args[1].toString();
            String chest = args[2].toString();
            String legs = args[3].toString();
            String boots = args[4].toString();
            Player player = server.getPlayer(playerName);
            if (player != null) {
                player.getInventory().setHelmet(createItemSafe(helmet));
                player.getInventory().setChestplate(createItemSafe(chest));
                player.getInventory().setLeggings(createItemSafe(legs));
                player.getInventory().setBoots(createItemSafe(boots));
                return true;
            }
            return false;
        });

        jsEngine.registerFunctionLambda("setOffhand", args -> {
            String playerName = args[0].toString();
            String material = args[1].toString();
            int amount = Integer.parseInt(args.length > 2 ? args[2].toString() : "1");
            Player player = server.getPlayer(playerName);
            if (player != null) {
                ItemStack item = createItemSafe(material, amount);
                if (item == null) return false;
                player.getInventory().setItemInOffHand(item);
                return true;
            }
            return false;
        });

        // EnderChest helpers
        jsEngine.registerFunctionLambda("getEnderContents", args -> {
            String playerName = args[0].toString();
            Player player = server.getPlayer(playerName);
            if (player == null) return null;
            ItemStack[] contents = player.getEnderChest().getContents();
            List<String> items = new ArrayList<>();
            for (ItemStack item : contents) {
                items.add(item == null ? "AIR" : item.getType().name());
            }
            return items;
        });

        jsEngine.registerFunctionLambda("setEnderSlot", args -> {
            String playerName = args[0].toString();
            int slot = Integer.parseInt(args[1].toString());
            String material = args[2].toString();
            int amount = Integer.parseInt(args.length > 3 ? args[3].toString() : "1");
            Player player = server.getPlayer(playerName);
            if (player != null) {
                ItemStack item = createItemSafe(material, amount);
                if (item == null) return false;
                player.getEnderChest().setItem(slot, item);
                return true;
            }
            return false;
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

        // Set world difficulty
        jsEngine.registerFunctionLambda("setDifficulty", args -> {
            String worldName = args[0].toString();
            String difficulty = args[1].toString();
            World world = server.getWorld(worldName);
            if (world != null) {
                try {
                    world.setDifficulty(org.bukkit.Difficulty.valueOf(difficulty.toUpperCase()));
                    return true;
                } catch (IllegalArgumentException e) {
                    return false;
                }
            }
            return false;
        });

        // Set gamerule
        jsEngine.registerFunctionLambda("setGameRule", args -> {
            String worldName = args[0].toString();
            String ruleName = args[1].toString();
            String value = args[2].toString();
            World world = server.getWorld(worldName);
            if (world != null) {
                GameRule<?> rule = GameRule.getByName(ruleName);
                if (rule != null) {
                    try {
                        //noinspection unchecked
                        return world.setGameRule((GameRule) rule, parseGameRuleValue(rule, value));
                    } catch (Exception e) {
                        return false;
                    }
                }
            }
            return false;
        });

        // Set block type
        jsEngine.registerFunctionLambda("setBlock", args -> {
            String worldName = args[0].toString();
            int x = Integer.parseInt(args[1].toString());
            int y = Integer.parseInt(args[2].toString());
            int z = Integer.parseInt(args[3].toString());
            String materialName = args[4].toString();
            World world = server.getWorld(worldName);
            if (world != null) {
                try {
                    Material mat = Material.valueOf(materialName.toUpperCase());
                    world.getBlockAt(x, y, z).setType(mat);
                    return true;
                } catch (IllegalArgumentException e) {
                    return false;
                }
            }
            return false;
        });

        // Get block type
        jsEngine.registerFunctionLambda("getBlock", args -> {
            String worldName = args[0].toString();
            int x = Integer.parseInt(args[1].toString());
            int y = Integer.parseInt(args[2].toString());
            int z = Integer.parseInt(args[3].toString());
            World world = server.getWorld(worldName);
            if (world != null) {
                return world.getBlockAt(x, y, z).getType().name();
            }
            return null;
        });

        // Spawn entity
        jsEngine.registerFunctionLambda("spawnEntity", args -> {
            String worldName = args[0].toString();
            double x = Double.parseDouble(args[1].toString());
            double y = Double.parseDouble(args[2].toString());
            double z = Double.parseDouble(args[3].toString());
            String typeName = args[4].toString();
            World world = server.getWorld(worldName);
            if (world != null) {
                try {
                    EntityType type = EntityType.valueOf(typeName.toUpperCase());
                    return world.spawnEntity(new org.bukkit.Location(world, x, y, z), type);
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
            return null;
        });

        // Biome get/set
        jsEngine.registerFunctionLambda("getBiome", args -> {
            String worldName = args[0].toString();
            int x = Integer.parseInt(args[1].toString());
            int z = Integer.parseInt(args[2].toString());
            World world = server.getWorld(worldName);
            if (world != null) {
                return world.getBiome(x, z).name();
            }
            return null;
        });

        jsEngine.registerFunctionLambda("setBiome", args -> {
            String worldName = args[0].toString();
            int x = Integer.parseInt(args[1].toString());
            int z = Integer.parseInt(args[2].toString());
            String biomeName = args[3].toString();
            World world = server.getWorld(worldName);
            if (world != null) {
                try {
                    Biome biome = Biome.valueOf(biomeName.toUpperCase());
                    world.setBiome(x, z, biome);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
            return false;
        });

        // Light level
        jsEngine.registerFunctionLambda("getLightLevel", args -> {
            String worldName = args[0].toString();
            int x = Integer.parseInt(args[1].toString());
            int y = Integer.parseInt(args[2].toString());
            int z = Integer.parseInt(args[3].toString());
            World world = server.getWorld(worldName);
            if (world != null) {
                return world.getBlockAt(x, y, z).getLightLevel();
            }
            return 0;
        });

        // Block data helpers
        jsEngine.registerFunctionLambda("setBlockData", args -> {
            String worldName = args[0].toString();
            int x = Integer.parseInt(args[1].toString());
            int y = Integer.parseInt(args[2].toString());
            int z = Integer.parseInt(args[3].toString());
            String dataString = args[4].toString();
            World world = server.getWorld(worldName);
            if (world != null) {
                Block block = world.getBlockAt(x, y, z);
                try {
                    BlockData data = Bukkit.createBlockData(dataString);
                    block.setBlockData(data, false);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
            return false;
        });

        jsEngine.registerFunctionLambda("setWaterlogged", args -> {
            String worldName = args[0].toString();
            int x = Integer.parseInt(args[1].toString());
            int y = Integer.parseInt(args[2].toString());
            int z = Integer.parseInt(args[3].toString());
            boolean state = Boolean.parseBoolean(args[4].toString());
            World world = server.getWorld(worldName);
            if (world != null) {
                BlockData data = world.getBlockAt(x, y, z).getBlockData();
                if (data instanceof Waterlogged) {
                    ((Waterlogged) data).setWaterlogged(state);
                    world.getBlockAt(x, y, z).setBlockData(data, false);
                    return true;
                }
            }
            return false;
        });

        jsEngine.registerFunctionLambda("setBlockFacing", args -> {
            String worldName = args[0].toString();
            int x = Integer.parseInt(args[1].toString());
            int y = Integer.parseInt(args[2].toString());
            int z = Integer.parseInt(args[3].toString());
            String face = args[4].toString();
            World world = server.getWorld(worldName);
            if (world != null) {
                BlockData data = world.getBlockAt(x, y, z).getBlockData();
                try {
                    if (data instanceof Directional directional) {
                        directional.setFacing(org.bukkit.block.BlockFace.valueOf(face.toUpperCase()));
                        world.getBlockAt(x, y, z).setBlockData(directional, false);
                        return true;
                    }
                    if (data instanceof Rotatable rot) {
                        rot.setRotation(org.bukkit.block.BlockFace.valueOf(face.toUpperCase()));
                        world.getBlockAt(x, y, z).setBlockData(rot, false);
                        return true;
                    }
                } catch (Exception ignored) {}
            }
            return false;
        });

        // Fill region (limited)
        jsEngine.registerFunctionLambda("fill", args -> {
            String worldName = args[0].toString();
            int x1 = Integer.parseInt(args[1].toString());
            int y1 = Integer.parseInt(args[2].toString());
            int z1 = Integer.parseInt(args[3].toString());
            int x2 = Integer.parseInt(args[4].toString());
            int y2 = Integer.parseInt(args[5].toString());
            int z2 = Integer.parseInt(args[6].toString());
            String materialName = args[7].toString();
            World world = server.getWorld(worldName);
            if (world != null) {
                try {
                    Material mat = Material.valueOf(materialName.toUpperCase());
                    int count = 0;
                    for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
                        for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
                            for (int z = Math.min(z1, z2); z <= Math.max(z1, z2); z++) {
                                world.getBlockAt(x, y, z).setType(mat, false);
                                count++;
                                if (count > 5000) return "limit"; // safety cap
                            }
                        }
                    }
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
            return false;
        });

        // Clone small region (limited)
        jsEngine.registerFunctionLambda("cloneRegion", args -> {
            String worldName = args[0].toString();
            int x1 = Integer.parseInt(args[1].toString());
            int y1 = Integer.parseInt(args[2].toString());
            int z1 = Integer.parseInt(args[3].toString());
            int x2 = Integer.parseInt(args[4].toString());
            int y2 = Integer.parseInt(args[5].toString());
            int z2 = Integer.parseInt(args[6].toString());
            int dx = Integer.parseInt(args[7].toString());
            int dy = Integer.parseInt(args[8].toString());
            int dz = Integer.parseInt(args[9].toString());
            World world = server.getWorld(worldName);
            if (world != null) {
                List<BlockSnapshot> snaps = new ArrayList<>();
                int count = 0;
                for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
                    for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
                        for (int z = Math.min(z1, z2); z <= Math.max(z1, z2); z++) {
                            Block block = world.getBlockAt(x, y, z);
                            snaps.add(new BlockSnapshot(block.getType(), block.getBlockData(), x, y, z));
                            count++;
                            if (count > 2000) return "limit";
                        }
                    }
                }
                for (BlockSnapshot snap : snaps) {
                    Block target = world.getBlockAt(snap.x + dx, snap.y + dy, snap.z + dz);
                    target.setType(snap.type, false);
                    target.setBlockData(snap.data, false);
                }
                return true;
            }
            return false;
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

    // ===== ENTITY / MOBS =====

    private static void registerEntityFunctions() {
        // Get nearby entities
        jsEngine.registerFunctionLambda("getNearbyEntities", args -> {
            String worldName = args[0].toString();
            double x = Double.parseDouble(args[1].toString());
            double y = Double.parseDouble(args[2].toString());
            double z = Double.parseDouble(args[3].toString());
            double radius = Double.parseDouble(args[4].toString());
            World world = server.getWorld(worldName);
            if (world == null) return List.of();
            Location loc = new Location(world, x, y, z);
            List<Map<String, Object>> result = new ArrayList<>();
            for (Entity e : world.getNearbyEntities(loc, radius, radius, radius)) {
                Map<String, Object> m = new HashMap<>();
                m.put("uuid", e.getUniqueId().toString());
                m.put("type", e.getType().name());
                m.put("name", e.getName());
                result.add(m);
            }
            return result;
        });

        // Add / remove / list tags
        jsEngine.registerFunctionLambda("addEntityTag", args -> {
            UUID uuid = UUID.fromString(args[0].toString());
            String tag = args[1].toString();
            Entity e = Bukkit.getEntity(uuid);
            if (e != null) {
                e.addScoreboardTag(tag);
                return true;
            }
            return false;
        });

        jsEngine.registerFunctionLambda("removeEntityTag", args -> {
            UUID uuid = UUID.fromString(args[0].toString());
            String tag = args[1].toString();
            Entity e = Bukkit.getEntity(uuid);
            if (e != null) {
                e.removeScoreboardTag(tag);
                return true;
            }
            return false;
        });

        jsEngine.registerFunctionLambda("getEntityTags", args -> {
            UUID uuid = UUID.fromString(args[0].toString());
            Entity e = Bukkit.getEntity(uuid);
            return e != null ? new ArrayList<>(e.getScoreboardTags()) : List.of();
        });

        // Health / armor
        jsEngine.registerFunctionLambda("setEntityHealth", args -> {
            UUID uuid = UUID.fromString(args[0].toString());
            double health = Double.parseDouble(args[1].toString());
            Entity e = Bukkit.getEntity(uuid);
            if (e instanceof LivingEntity le) {
                le.setHealth(Math.min(health, le.getMaxHealth()));
                return true;
            }
            return false;
        });

        jsEngine.registerFunctionLambda("getEntityHealth", args -> {
            UUID uuid = UUID.fromString(args[0].toString());
            Entity e = Bukkit.getEntity(uuid);
            if (e instanceof LivingEntity le) {
                return le.getHealth();
            }
            return 0;
        });

        jsEngine.registerFunctionLambda("setEntityArmor", args -> {
            UUID uuid = UUID.fromString(args[0].toString());
            String helmet = args[1].toString();
            String chest = args[2].toString();
            String legs = args[3].toString();
            String boots = args[4].toString();
            Entity e = Bukkit.getEntity(uuid);
            if (e instanceof LivingEntity le) {
                le.getEquipment().setHelmet(createItemSafe(helmet));
                le.getEquipment().setChestplate(createItemSafe(chest));
                le.getEquipment().setLeggings(createItemSafe(legs));
                le.getEquipment().setBoots(createItemSafe(boots));
                return true;
            }
            return false;
        });

        // AI toggle
        jsEngine.registerFunctionLambda("setAI", args -> {
            UUID uuid = UUID.fromString(args[0].toString());
            boolean enabled = Boolean.parseBoolean(args[1].toString());
            Entity e = Bukkit.getEntity(uuid);
            if (e instanceof LivingEntity le) {
                le.setAI(enabled);
                return true;
            }
            return false;
        });

        // Spawn projectile
        jsEngine.registerFunctionLambda("spawnProjectile", args -> {
            String worldName = args[0].toString();
            double x = Double.parseDouble(args[1].toString());
            double y = Double.parseDouble(args[2].toString());
            double z = Double.parseDouble(args[3].toString());
            String typeName = args[4].toString();
            double vx = Double.parseDouble(args.length > 5 ? args[5].toString() : "0");
            double vy = Double.parseDouble(args.length > 6 ? args[6].toString() : "0.2");
            double vz = Double.parseDouble(args.length > 7 ? args[7].toString() : "0");
            World world = server.getWorld(worldName);
            if (world != null) {
                try {
                    EntityType type = EntityType.valueOf(typeName.toUpperCase());
                    Entity proj = world.spawnEntity(new Location(world, x, y, z), type);
                    proj.setVelocity(new Vector(vx, vy, vz));
                    return proj.getUniqueId().toString();
                } catch (Exception e) {
                    return null;
                }
            }
            return null;
        });

        // Set owner for tameable (pet)
        jsEngine.registerFunctionLambda("setOwner", args -> {
            UUID uuid = UUID.fromString(args[0].toString());
            String playerName = args[1].toString();
            Entity e = Bukkit.getEntity(uuid);
            if (e instanceof Tameable t) {
                Player owner = server.getPlayer(playerName);
                if (owner != null) {
                    t.setOwner(owner);
                    return true;
                }
            }
            return false;
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

    // ===== SCOREBOARD / STATS =====

    private static void registerScoreboardFunctions() {
        jsEngine.registerFunctionLambda("createObjective", args -> {
            String name = args[0].toString();
            String criteria = args.length > 1 ? args[1].toString() : "dummy";
            Scoreboard board = server.getScoreboardManager().getMainScoreboard();
            Objective existing = board.getObjective(name);
            if (existing != null) return existing.getName();
            Objective obj = board.registerNewObjective(name, criteria, name);
            obj.setDisplaySlot(org.bukkit.scoreboard.DisplaySlot.SIDEBAR);
            return obj.getName();
        });

        jsEngine.registerFunctionLambda("removeObjective", args -> {
            String name = args[0].toString();
            Scoreboard board = server.getScoreboardManager().getMainScoreboard();
            Objective obj = board.getObjective(name);
            if (obj != null) {
                obj.unregister();
                return true;
            }
            return false;
        });

        jsEngine.registerFunctionLambda("setScore", args -> {
            String objective = args[0].toString();
            String entry = args[1].toString();
            int scoreVal = Integer.parseInt(args[2].toString());
            Scoreboard board = server.getScoreboardManager().getMainScoreboard();
            Objective obj = board.getObjective(objective);
            if (obj == null) return false;
            Score score = obj.getScore(entry);
            score.setScore(scoreVal);
            return true;
        });

        jsEngine.registerFunctionLambda("getScore", args -> {
            String objective = args[0].toString();
            String entry = args[1].toString();
            Scoreboard board = server.getScoreboardManager().getMainScoreboard();
            Objective obj = board.getObjective(objective);
            if (obj == null) return null;
            return obj.getScore(entry).getScore();
        });

        jsEngine.registerFunctionLambda("addTeam", args -> {
            String name = args[0].toString();
            Scoreboard board = server.getScoreboardManager().getMainScoreboard();
            Team team = board.getTeam(name);
            if (team == null) {
                team = board.registerNewTeam(name);
            }
            return team.getName();
        });

        jsEngine.registerFunctionLambda("addToTeam", args -> {
            String teamName = args[0].toString();
            String entry = args[1].toString();
            Scoreboard board = server.getScoreboardManager().getMainScoreboard();
            Team team = board.getTeam(teamName);
            if (team != null) {
                team.addEntry(entry);
                return true;
            }
            return false;
        });
    }

    // ===== CHAT / TAB / BOSSBAR =====

    private static void registerChatFunctions() {
        jsEngine.registerFunctionLambda("setTabHeaderFooter", args -> {
            String playerName = args[0].toString();
            String header = args[1].toString();
            String footer = args[2].toString();
            Player player = server.getPlayer(playerName);
            if (player != null) {
                player.setPlayerListHeaderFooter(header, footer);
                return true;
            }
            return false;
        });

        jsEngine.registerFunctionLambda("sendTitle", args -> {
            String playerName = args[0].toString();
            String title = args[1].toString();
            String subtitle = args.length > 2 ? args[2].toString() : "";
            int fadeIn = args.length > 3 ? Integer.parseInt(args[3].toString()) : 10;
            int stay = args.length > 4 ? Integer.parseInt(args[4].toString()) : 70;
            int fadeOut = args.length > 5 ? Integer.parseInt(args[5].toString()) : 20;
            Player player = server.getPlayer(playerName);
            if (player != null) {
                player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
                return true;
            }
            return false;
        });

        jsEngine.registerFunctionLambda("sendActionBar", args -> {
            String playerName = args[0].toString();
            String message = args[1].toString();
            Player player = server.getPlayer(playerName);
            if (player != null) {
                player.sendActionBar(message);
                return true;
            }
            return false;
        });

        // BossBars
        jsEngine.registerFunctionLambda("createBossBar", args -> {
            String id = args[0].toString();
            String title = args[1].toString();
            String color = args.length > 2 ? args[2].toString() : "PURPLE";
            String style = args.length > 3 ? args[3].toString() : "SOLID";
            try {
                BossBar bar = Bukkit.createBossBar(title, BarColor.valueOf(color.toUpperCase()), BarStyle.valueOf(style.toUpperCase()));
                bossBars.put(id, bar);
                return true;
            } catch (Exception e) {
                return false;
            }
        });

        jsEngine.registerFunctionLambda("showBossBar", args -> {
            String playerName = args[0].toString();
            String id = args[1].toString();
            double progress = Double.parseDouble(args.length > 2 ? args[2].toString() : "1.0");
            Player player = server.getPlayer(playerName);
            BossBar bar = bossBars.get(id);
            if (player != null && bar != null) {
                bar.setProgress(Math.max(0, Math.min(1, progress)));
                bar.addPlayer(player);
                return true;
            }
            return false;
        });

        jsEngine.registerFunctionLambda("removeBossBar", args -> {
            String id = args[0].toString();
            BossBar bar = bossBars.remove(id);
            if (bar != null) {
                bar.removeAll();
                return true;
            }
            return false;
        });
    }

    // ===== SOUND / PARTICLES =====

    private static void registerSoundParticleFunctions() {
        jsEngine.registerFunctionLambda("stopSound", args -> {
            String playerName = args[0].toString();
            String soundName = args.length > 1 ? args[1].toString() : null;
            Player player = server.getPlayer(playerName);
            if (player != null) {
                if (soundName == null) {
                    player.stopAllSounds();
                } else {
                    try {
                        player.stopSound(Sound.valueOf(soundName.toUpperCase()));
                    } catch (Exception ignored) {}
                }
                return true;
            }
            return false;
        });

        jsEngine.registerFunctionLambda("spawnParticle", args -> {
            String worldName = args[0].toString();
            double x = Double.parseDouble(args[1].toString());
            double y = Double.parseDouble(args[2].toString());
            double z = Double.parseDouble(args[3].toString());
            String particleName = args[4].toString();
            int count = Integer.parseInt(args.length > 5 ? args[5].toString() : "1");
            double offsetX = args.length > 6 ? Double.parseDouble(args[6].toString()) : 0;
            double offsetY = args.length > 7 ? Double.parseDouble(args[7].toString()) : 0;
            double offsetZ = args.length > 8 ? Double.parseDouble(args[8].toString()) : 0;
            double extra = args.length > 9 ? Double.parseDouble(args[9].toString()) : 0;
            World world = server.getWorld(worldName);
            if (world != null) {
                try {
                    Particle particle = Particle.valueOf(particleName.toUpperCase());
                    world.spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ, extra);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
            return false;
        });
    }

    // ===== VAULT ECON / PERMS =====

    private static void registerVaultFunctions() {
        jsEngine.registerFunctionLambda("getBalance", args -> {
            Economy eco = ensureEconomy();
            String playerName = args[0].toString();
            if (eco == null) return null;
            return eco.getBalance(server.getOfflinePlayer(playerName));
        });

        jsEngine.registerFunctionLambda("deposit", args -> {
            Economy eco = ensureEconomy();
            String playerName = args[0].toString();
            double amount = Double.parseDouble(args[1].toString());
            if (eco == null) return false;
            return eco.depositPlayer(server.getOfflinePlayer(playerName), amount).transactionSuccess();
        });

        jsEngine.registerFunctionLambda("withdraw", args -> {
            Economy eco = ensureEconomy();
            String playerName = args[0].toString();
            double amount = Double.parseDouble(args[1].toString());
            if (eco == null) return false;
            return eco.withdrawPlayer(server.getOfflinePlayer(playerName), amount).transactionSuccess();
        });

        jsEngine.registerFunctionLambda("addPermission", args -> {
            Permission perm = ensurePermission();
            String playerName = args[0].toString();
            String node = args[1].toString();
            Player player = server.getPlayer(playerName);
            if (perm != null && player != null) {
                return perm.playerAdd(player, node);
            }
            return false;
        });

        jsEngine.registerFunctionLambda("removePermission", args -> {
            Permission perm = ensurePermission();
            String playerName = args[0].toString();
            String node = args[1].toString();
            Player player = server.getPlayer(playerName);
            if (perm != null && player != null) {
                return perm.playerRemove(player, node);
            }
            return false;
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

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Object parseGameRuleValue(GameRule<?> rule, String value) {
        if (rule.getType() == Boolean.class) {
            return Boolean.parseBoolean(value);
        }
        if (rule.getType() == Integer.class) {
            return Integer.parseInt(value);
        }
        return value;
    }

    private static ItemStack createItemSafe(String materialName) {
        return createItemSafe(materialName, 1);
    }

    private static ItemStack createItemSafe(String materialName, int amount) {
        try {
            Material material = Material.valueOf(materialName.toUpperCase());
            return new ItemStack(material, amount);
        } catch (Exception e) {
            return null;
        }
    }

    private record BlockSnapshot(Material type, BlockData data, int x, int y, int z) { }

    private static Economy ensureEconomy() {
        if (economy != null) return economy;
        try {
            var rsp = server.getServicesManager().getRegistration(Economy.class);
            if (rsp != null) {
                economy = rsp.getProvider();
            }
        } catch (Exception ignored) {}
        return economy;
    }

    private static Permission ensurePermission() {
        if (permissions != null) return permissions;
        try {
            var rsp = server.getServicesManager().getRegistration(Permission.class);
            if (rsp != null) {
                permissions = rsp.getProvider();
            }
        } catch (Exception ignored) {}
        return permissions;
    }
}
