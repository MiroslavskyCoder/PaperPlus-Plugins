package com.webx.quests.integration;

import com.google.gson.Gson;
import io.javalin.Javalin;
import lxxv.shared.javascript.JavaScriptEngine;
import lxxv.shared.javascript.advanced.*;
import lxxv.shared.server.LXXVServer;
import lxxv.shared.server.script.ServerScriptController;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * Интеграция V8 JavaScript в QuestsPlugin
 * 
 * Пример использования:
 * JavaScriptIntegration integration = new JavaScriptIntegration(plugin);
 * integration.initialize();
 * integration.registerQuestFunctions();
 */
public class JavaScriptIntegration {
    private final JavaPlugin plugin;
    private final JavaScriptEngine jsEngine;
    private final JavaScriptEventSystem eventSystem;
    private final JavaScriptScheduler scheduler;
    private final JavaScriptModuleManager moduleManager;
    private final File scriptsDir;
    private final Gson gson;
    private Javalin apiServer;

    public JavaScriptIntegration(JavaPlugin plugin) {
        this.plugin = plugin;
        this.jsEngine = JavaScriptEngine.getInstance();
        this.eventSystem = new JavaScriptEventSystem();
        this.scheduler = new JavaScriptScheduler();
        this.moduleManager = new JavaScriptModuleManager(jsEngine);
        this.scriptsDir = new File(plugin.getDataFolder(), "scripts");
        this.scriptsDir.mkdirs();
        this.gson = new Gson();
    }

    /**
     * Инициализировать JavaScript интеграцию
     */
    public void initialize() {
        try {
            // Инициализировать LXXVServer
            LXXVServer.initialize(Bukkit.getServer(), jsEngine);

            // Запустить API сервер
            startApiServer();

            // Регистрировать кастомные функции для квестов
            registerQuestFunctions();

            // Регистрировать события квестов
            registerQuestEvents();

            plugin.getLogger().info("JavaScript интеграция инициализирована");
        } catch (Exception e) {
            plugin.getLogger().severe("Ошибка при инициализации JavaScript: " + e.getMessage());
        }
    }

    /**
     * Запустить REST API сервер для скриптов
     */
    private void startApiServer() {
        try {
            apiServer = Javalin.create().start(7071);

            // Регистрировать контроллер
            ServerScriptController controller = new ServerScriptController(
                    jsEngine,
                    eventSystem,
                    scheduler,
                    moduleManager
            );
            controller.register(apiServer);

            plugin.getLogger().info("API сервер запущен на порту 7071");
        } catch (Exception e) {
            plugin.getLogger().warning("Не удалось запустить API сервер: " + e.getMessage());
        }
    }

    /**
     * Регистрировать функции для квестов
     */
    private void registerQuestFunctions() {
        // Проверить завершил ли игрок квест
        jsEngine.registerFunction("hasQuestCompleted", args -> {
            Player player = (Player) args[0];
            String questId = args[1].toString();
            // Здесь должна быть реальная проверка
            return false;
        });

        // Начать квест
        jsEngine.registerFunction("startQuest", args -> {
            Player player = (Player) args[0];
            String questId = args[1].toString();
            // Начать квест логика
            return true;
        });

        // Завершить квест
        jsEngine.registerFunction("completeQuest", args -> {
            Player player = (Player) args[0];
            String questId = args[1].toString();
            // Завершить квест логика
            return true;
        });

        // Получить прогресс квеста
        jsEngine.registerFunction("getQuestProgress", args -> {
            Player player = (Player) args[0];
            String questId = args[1].toString();

            Map<String, Object> progress = new HashMap<>();
            progress.put("questId", questId);
            progress.put("player", player.getName());
            progress.put("completed", false);
            progress.put("progress", 0);
            return progress;
        });

        // Дать награду за квест
        jsEngine.registerFunction("giveQuestReward", args -> {
            Player player = (Player) args[0];
            String questId = args[1].toString();
            int amount = ((Number) args[2]).intValue();

            // Дать награду (золото, опыт и т.д.)
            player.giveExp(amount * 10);
            return true;
        });

        // Получить список квестов игрока
        jsEngine.registerFunction("getPlayerQuests", args -> {
            Player player = (Player) args[0];
            // Вернуть список квестов
            return new String[]{"quest_001", "quest_002", "quest_003"};
        });
    }

    /**
     * Регистрировать события квестов
     */
    private void registerQuestEvents() {
        // Событие: Квест начат
        eventSystem.addEventListener("questStarted", event -> {
            if (event.length < 2) return;
            Player player = Bukkit.getPlayer(String.valueOf(event[0]));
            String questId = String.valueOf(event[1]);

            if (player != null) {
                player.sendMessage("§aВы начали квест: " + questId);
            }
        });

        // Событие: Квест завершен
        eventSystem.addEventListener("questCompleted", event -> {
            if (event.length < 2) return;
            Player player = Bukkit.getPlayer(String.valueOf(event[0]));
            String questId = String.valueOf(event[1]);

            if (player != null) {
                player.sendMessage("§eВы завершили квест: " + questId);
            }
        });

        // Событие: Прогресс квеста обновлён
        eventSystem.addEventListener("questProgressUpdated", event -> {
            if (event.length < 3) return;
            Player player = Bukkit.getPlayer(String.valueOf(event[0]));
            String questId = String.valueOf(event[1]);
            int progress = ((Number) event[2]).intValue();

            if (player != null) {
                player.sendMessage("§bПрогресс: " + progress + "%");
            }
        });
    }

    /**
     * Выполнить JavaScript скрипт с контекстом игрока
     */
    public Object executeQuestScript(String scriptName, Player player, Map<String, Object> context) {
        try {
            Map<String, Object> fullContext = new HashMap<>(context);
            fullContext.put("player", player);
            fullContext.put("playerName", player.getName());
            fullContext.put("playerLevel", player.getLevel());

            String code = loadScript(scriptName);
            if (code == null) return null;
            if (isTypeScript(scriptName)) {
                code = jsEngine.transpile(code, scriptName);
            }
            return jsEngine.execute(code, fullContext);
        } catch (Exception e) {
            plugin.getLogger().warning("Ошибка при выполнении скрипта: " + e.getMessage());
            return null;
        }
    }

    /**
     * Выполнить функцию из скрипта квеста
     */
    public Object executeQuestFunction(String scriptName, String functionName, Player player, Object... args) {
        try {
            Object[] fullArgs = new Object[args.length + 1];
            fullArgs[0] = player;
            System.arraycopy(args, 0, fullArgs, 1, args.length);
            String code = loadScript(scriptName);
            if (code == null) return null;
            if (isTypeScript(scriptName)) {
                code = jsEngine.transpile(code, scriptName);
            }
            String invoke = code + "\nif (typeof " + functionName + " === 'function') { " +
                    "module.exports = " + functionName + "; }";
            Map<String, Object> ctx = new HashMap<>();
            ctx.put("args", fullArgs);
            return jsEngine.execute(invoke + "\nmodule.exports.apply(null, args);", ctx);
        } catch (Exception e) {
            plugin.getLogger().warning("Ошибка при вызове функции: " + e.getMessage());
            return null;
        }
    }

    /**
     * Запустить событие квеста
     */
    public void fireQuestEvent(String eventName, Object... args) {
        eventSystem.emit("quest" + eventName, args);
    }

    /**
     * Запланировать ежедневный поиск сокровищ
     */
    public void scheduleDailyTreasureHunt() {
        scheduler.scheduleDailyTask(() -> {
            try {
                String code = "broadcast('§6Ежедневный поиск сокровищ начался!')";
                jsEngine.execute(code);
                fireQuestEvent("TreasureHuntStarted");
            } catch (Exception e) {
                plugin.getLogger().warning("Ошибка при запуске поиска: " + e.getMessage());
            }
        }, 12, 0);
    }

    /**
     * Остановить JavaScript интеграцию
     */
    public void shutdown() {
        try {
            eventSystem.shutdown();
            scheduler.shutdown();

            if (apiServer != null) {
                apiServer.stop();
            }

            plugin.getLogger().info("JavaScript интеграция остановлена");
        } catch (Exception e) {
            plugin.getLogger().warning("Ошибка при остановке JavaScript: " + e.getMessage());
        }
    }

    /**
     * Создать пример скрипта квеста
     */
    public void createExampleQuestScript() {
        String exampleScript = "// Скрипт для квеста поиска руды\n" +
                "\n" +
                "function checkQuestRequirements(player) {\n" +
                "  const level = getPlayerExpLevel(player);\n" +
                "  return level >= 5;\n" +
                "}\n" +
                "\n" +
                "function calculateReward(difficulty) {\n" +
                "  const rewards = {\n" +
                "    'EASY': 50,\n" +
                "    'MEDIUM': 100,\n" +
                "    'HARD': 250,\n" +
                "    'LEGENDARY': 1000\n" +
                "  };\n" +
                "  return rewards[difficulty] || 0;\n" +
                "}\n" +
                "\n" +
                "function processQuestCompletion(player, questId) {\n" +
                "  const reward = calculateReward('MEDIUM');\n" +
                "  giveExp(player, reward);\n" +
                "  sendMessage(player, '§e+' + reward + ' опыта!');\n" +
                "  return true;\n" +
                "}\n";

        try {
            File scriptsDir = new File(plugin.getDataFolder(), "scripts");
            scriptsDir.mkdirs();

            File exampleFile = new File(scriptsDir, "example_quest.js");
            java.nio.file.Files.write(exampleFile.toPath(), exampleScript.getBytes());

            plugin.getLogger().info("Пример скрипта квеста создан: example_quest.js");
        } catch (Exception e) {
            plugin.getLogger().warning("Ошибка при создании примера скрипта: " + e.getMessage());
        }
    }

    /**
     * Получить движок JavaScript
     */
    public JavaScriptEngine getJsEngine() {
        return jsEngine;
    }

    /**
     * Получить менеджер скриптов
     */
    /**
     * Получить систему событий
     */
    public JavaScriptEventSystem getEventSystem() {
        return eventSystem;
    }

    /**
     * Получить планировщик
     */
    public JavaScriptScheduler getScheduler() {
        return scheduler;
    }

    /**
     * Получить менеджер модулей
     */
    public JavaScriptModuleManager getModuleManager() {
        return moduleManager;
    }

    private String loadScript(String scriptName) {
        try {
            File file = new File(scriptsDir, scriptName);
            if (!file.exists()) return null;
            return Files.readString(file.toPath(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            plugin.getLogger().warning("Не удалось прочитать скрипт " + scriptName + ": " + e.getMessage());
            return null;
        }
    }

    private boolean isTypeScript(String name) {
        String lower = name.toLowerCase();
        return lower.endsWith(".ts") || lower.endsWith(".tsx");
    }
}
