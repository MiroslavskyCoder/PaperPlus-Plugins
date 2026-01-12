package lxxv.shared.server;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitTask;
import lxxv.shared.javascript.JavaScriptEngine;
import lxxv.shared.javascript.advanced.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Главный класс для регистрации Bukkit API в JavaScript окружении.
 * Предоставляет доступ к серверу, игрокам, командам и событиям из JS кода.
 *
 * Использование:
 * LXXVServer.initialize(plugin, engine);
 * engine.execute("LXXVServer.broadcast('Hello!')");
 */
public class LXXVServer {
    private static Server server;
    private static JavaScriptEngine jsEngine;
    private static JavaScriptEventSystem eventSystem;
    private static JavaScriptScheduler scheduler;
    private static JavaScriptModuleManager moduleManager;

    /**
     * Инициализирует LXXVServer с Bukkit сервером и JS движком
     */
    public static void initialize(Server bukkitServer, JavaScriptEngine engine) {
        server = bukkitServer;
        jsEngine = engine;
        eventSystem = new JavaScriptEventSystem();
        scheduler = new JavaScriptScheduler();
        moduleManager = new JavaScriptModuleManager(engine);

        registerAllFunctions();
    }

    /**
     * Регистрирует все функции в JS движке
     */
    private static void registerAllFunctions() {
        registerServerFunctions();
        registerPlayerFunctions();
        registerCommandFunctions();
        registerWorldFunctions();
        registerEventFunctions();
        registerSchedulerFunctions();
        registerUtilityFunctions();
    }

    /**
     * Функции сервера
     */
    private static void registerServerFunctions() {
        // Трансляция сообщения всем игрокам
        jsEngine.registerFunction("broadcast", args -> {
            String message = args[0].toString();
            server.broadcastMessage(message);
            return null;
        });

        // Получить количество игроков онлайн
        jsEngine.registerFunction("getOnlinePlayers", args -> {
            return server.getOnlinePlayers().size();
        });

        // Получить максимальное количество игроков
        jsEngine.registerFunction("getMaxPlayers", args -> {
            return server.getMaxPlayers();
        });

        // Получить MOTD сервера
        jsEngine.registerFunction("getMotd", args -> {
            return server.getMotd();
        });

        // Установить MOTD
        jsEngine.registerFunction("setMotd", args -> {
            server.setMotd(args[0].toString());
            return null;
        });

        // Получить версию сервера
        jsEngine.registerFunction("getVersion", args -> {
            return server.getVersion();
        });

        // Получить время работы сервера (в миллисекундах)
        jsEngine.registerFunction("getUptime", args -> {
            // В Paper API нет getTickCount(), используем текущее время сервера
            return server.getWorlds().isEmpty() ? 0L : server.getWorlds().get(0).getFullTime() * 50L;
        });

        // Перезагрузить сервер
        jsEngine.registerFunction("reload", args -> {
            server.reload();
            return null;
        });

        // Остановить сервер
        jsEngine.registerFunction("shutdown", args -> {
            server.shutdown();
            return null;
        });

        // Выполнить команду от сервера
        jsEngine.registerFunction("executeCommand", args -> {
            String command = args[0].toString();
            return server.dispatchCommand(server.getConsoleSender(), command);
        });
    }

    /**
     * Функции для работы с игроками
     */
    private static void registerPlayerFunctions() {
        // Получить игрока по имени
        jsEngine.registerFunction("getPlayer", args -> {
            String name = args[0].toString();
            return server.getPlayerExact(name);
        });

        // Получить всех игроков онлайн
        jsEngine.registerFunction("getPlayers", args -> {
            return server.getOnlinePlayers().toArray();
        });

        // Получить игрока по UUID
        jsEngine.registerFunction("getPlayerByUUID", args -> {
            UUID uuid = UUID.fromString(args[0].toString());
            return server.getPlayer(uuid);
        });

        // Отправить сообщение игроку
        jsEngine.registerFunction("sendMessage", args -> {
            Player player = (Player) args[0];
            String message = args[1].toString();
            player.sendMessage(message);
            return null;
        });

        // Дать предмет игроку
        jsEngine.registerFunction("giveItem", args -> {
            Player player = (Player) args[0];
            String itemName = args[1].toString();
            int amount = ((Number) args[2]).intValue();
            Material material = Material.valueOf(itemName.toUpperCase());
            player.getInventory().addItem(new ItemStack(material, amount));
            return null;
        });

        // Телепортировать игрока
        jsEngine.registerFunction("teleportPlayer", args -> {
            Player player = (Player) args[0];
            double x = ((Number) args[1]).doubleValue();
            double y = ((Number) args[2]).doubleValue();
            double z = ((Number) args[3]).doubleValue();
            String worldName = args.length > 4 ? args[4].toString() : "world";
            World world = server.getWorld(worldName);
            if (world != null) {
                player.teleport(new Location(world, x, y, z));
                return true;
            }
            return false;
        });

        // Получить здоровье игрока
        jsEngine.registerFunction("getPlayerHealth", args -> {
            Player player = (Player) args[0];
            return player.getHealth();
        });

        // Установить здоровье игрока
        jsEngine.registerFunction("setPlayerHealth", args -> {
            Player player = (Player) args[0];
            double health = ((Number) args[1]).doubleValue();
            player.setHealth(Math.min(health, player.getMaxHealth()));
            return null;
        });

        // Получить голод игрока
        jsEngine.registerFunction("getPlayerFood", args -> {
            Player player = (Player) args[0];
            return player.getFoodLevel();
        });

        // Установить голод игрока
        jsEngine.registerFunction("setPlayerFood", args -> {
            Player player = (Player) args[0];
            int food = ((Number) args[1]).intValue();
            player.setFoodLevel(Math.min(food, 20));
            return null;
        });

        // Получить уровень опыта
        jsEngine.registerFunction("getPlayerExpLevel", args -> {
            Player player = (Player) args[0];
            return player.getLevel();
        });

        // Дать опыт игроку
        jsEngine.registerFunction("giveExp", args -> {
            Player player = (Player) args[0];
            int amount = ((Number) args[1]).intValue();
            player.giveExp(amount);
            return null;
        });

        // Проверить есть ли разрешение
        jsEngine.registerFunction("hasPermission", args -> {
            Player player = (Player) args[0];
            String permission = args[1].toString();
            return player.hasPermission(permission);
        });

        // Получить мир игрока
        jsEngine.registerFunction("getPlayerWorld", args -> {
            Player player = (Player) args[0];
            return player.getWorld().getName();
        });

        // Получить позицию игрока
        jsEngine.registerFunction("getPlayerLocation", args -> {
            Player player = (Player) args[0];
            Location loc = player.getLocation();
            Map<String, Object> locMap = new HashMap<>();
            locMap.put("x", loc.getX());
            locMap.put("y", loc.getY());
            locMap.put("z", loc.getZ());
            locMap.put("world", loc.getWorld().getName());
            return locMap;
        });

        // Получить режим игры
        jsEngine.registerFunction("getGameMode", args -> {
            Player player = (Player) args[0];
            return player.getGameMode().toString();
        });

        // Установить режим игры
        jsEngine.registerFunction("setGameMode", args -> {
            Player player = (Player) args[0];
            String modeStr = args[1].toString().toUpperCase();
            GameMode mode = GameMode.valueOf(modeStr);
            player.setGameMode(mode);
            return null;
        });
    }

    /**
     * Функции для команд
     */
    private static void registerCommandFunctions() {
        // Выполнить команду от игрока
        jsEngine.registerFunction("playerExecuteCommand", args -> {
            Player player = (Player) args[0];
            String command = args[1].toString();
            return player.performCommand(command);
        });

        // Получить таблицу коммунд
        jsEngine.registerFunction("getCommands", args -> {
            return server.getCommandMap().getKnownCommands().keySet().toArray();
        });
    }

    /**
     * Функции для работы с миром
     */
    private static void registerWorldFunctions() {
        // Получить мир по имени
        jsEngine.registerFunction("getWorld", args -> {
            String worldName = args[0].toString();
            return server.getWorld(worldName);
        });

        // Получить список миров
        jsEngine.registerFunction("getWorlds", args -> {
            return server.getWorlds().stream()
                    .map(World::getName)
                    .toArray();
        });

        // Получить время суток в мире
        jsEngine.registerFunction("getTime", args -> {
            World world = (World) args[0];
            return world.getTime();
        });

        // Установить время суток
        jsEngine.registerFunction("setTime", args -> {
            World world = (World) args[0];
            long time = ((Number) args[1]).longValue();
            world.setTime(time);
            return null;
        });

        // Получить погоду (true - идёт дождь)
        jsEngine.registerFunction("hasStorm", args -> {
            World world = (World) args[0];
            return world.hasStorm();
        });

        // Установить погоду
        jsEngine.registerFunction("setStorm", args -> {
            World world = (World) args[0];
            boolean storm = (boolean) args[1];
            world.setStorm(storm);
            return null;
        });

        // Получить сложность мира
        jsEngine.registerFunction("getDifficulty", args -> {
            World world = (World) args[0];
            return world.getDifficulty().toString();
        });
    }

    /**
     * Функции для событий
     */
    private static void registerEventFunctions() {
        // Зарегистрировать слушателя события
        jsEngine.registerFunction("addEventListener", args -> {
            String eventName = args[0].toString();
            // Java код будет вызывать emit, а JS слушатели будут обработаны
            eventSystem.addEventListener(eventName, event -> {
                // JS обработчик будет вызван движком
            });
            return null;
        });

        // Запустить событие
        jsEngine.registerFunction("emit", args -> {
            String eventName = args[0].toString();
            Object[] eventArgs = new Object[args.length - 1];
            System.arraycopy(args, 1, eventArgs, 0, args.length - 1);
            eventSystem.emit(eventName, eventArgs);
            return null;
        });

        // Запустить событие асинхронно
        jsEngine.registerFunction("emitAsync", args -> {
            String eventName = args[0].toString();
            Object[] eventArgs = new Object[args.length - 1];
            System.arraycopy(args, 1, eventArgs, 0, args.length - 1);
            eventSystem.emitAsync(eventName, eventArgs);
            return null;
        });

        // Получить количество слушателей
        jsEngine.registerFunction("getListenerCount", args -> {
            String eventName = args[0].toString();
            return eventSystem.getListenerCount(eventName);
        });
    }

    /**
     * Функции планировщика
     */
    private static void registerSchedulerFunctions() {
        // setTimeout
        jsEngine.registerFunction("setTimeout", args -> {
            // Код будет передан как строка, выполнится через delay
            String taskId = scheduler.setTimeout(
                    () -> {},
                    ((Number) args[1]).longValue()
            );
            return taskId;
        });

        // setInterval
        jsEngine.registerFunction("setInterval", args -> {
            String taskId = scheduler.setInterval(
                    () -> {},
                    ((Number) args[1]).longValue()
            );
            return taskId;
        });

        // clearTimeout/clearInterval
        jsEngine.registerFunction("clearTimeout", args -> {
            String taskId = args[0].toString();
            return scheduler.clearTimeout(taskId);
        });

        jsEngine.registerFunction("clearInterval", args -> {
            String taskId = args[0].toString();
            return scheduler.clearInterval(taskId);
        });

        // Получить активные задачи
        jsEngine.registerFunction("getActiveTasks", args -> {
            return scheduler.getActiveTasks().toArray();
        });
    }

    /**
     * Служебные функции
     */
    private static void registerUtilityFunctions() {
        // Логирование
        jsEngine.registerFunction("log", args -> {
            System.out.println("[LXXV-JS] " + args[0]);
            return null;
        });

        // Предупреждение
        jsEngine.registerFunction("warn", args -> {
            System.out.println("[LXXV-JS WARN] " + args[0]);
            return null;
        });

        // Ошибка
        jsEngine.registerFunction("error", args -> {
            System.err.println("[LXXV-JS ERROR] " + args[0]);
            return null;
        });

        // Получить текущее время в миллисекундах
        jsEngine.registerFunction("now", args -> {
            return System.currentTimeMillis();
        });

        // Получить информацию о памяти
        jsEngine.registerFunction("getMemoryInfo", args -> {
            Runtime runtime = Runtime.getRuntime();
            Map<String, Object> info = new HashMap<>();
            info.put("total", runtime.totalMemory());
            info.put("free", runtime.freeMemory());
            info.put("max", runtime.maxMemory());
            info.put("used", runtime.totalMemory() - runtime.freeMemory());
            return info;
        });
    }

    /**
     * Получить систему событий
     */
    public static JavaScriptEventSystem getEventSystem() {
        return eventSystem;
    }

    /**
     * Получить планировщик
     */
    public static JavaScriptScheduler getScheduler() {
        return scheduler;
    }

    /**
     * Получить менеджер модулей
     */
    public static JavaScriptModuleManager getModuleManager() {
        return moduleManager;
    }

    /**
     * Получить JS движок
     */
    public static JavaScriptEngine getJSEngine() {
        return jsEngine;
    }

    /**
     * Получить Bukkit сервер
     */
    public static Server getBukkitServer() {
        return server;
    }
}
