# V8 JavaScript Интеграция - Расширенная Система

## 📋 Обзор

Расширенная V8 JavaScript система для Minecraft сервера включает:

1. **JavaScriptEventSystem** - Система событий с слушателями
2. **JavaScriptScheduler** - Планировщик для задач с задержками
3. **JavaScriptModuleManager** - Управление JavaScript модулями и зависимостями
4. **LXXVServer** - Главный класс с регистрацией всех Bukkit API функций
5. **ServerScriptController** - REST API контроллер для выполнения скриптов

---

## 🎯 JavaScriptEventSystem

Система для регистрации и запуска событий из JavaScript.

### Базовое использование

```java
JavaScriptEventSystem eventSystem = new JavaScriptEventSystem();

// Регистрировать слушателя события
eventSystem.addEventListener("playerJoin", event -> {
    String playerName = event.getArg(0).toString();
    System.out.println("Игрок " + playerName + " присоединился");
});

// Запустить событие
eventSystem.emit("playerJoin", "Steve");

// Асинхронное запускание
eventSystem.emitAsync("playerJoin", "Alex");
```

### API Методы

| Метод | Описание |
|-------|---------|
| `addEventListener(String, Listener)` | Регистрирует слушателя |
| `removeEventListener(String, Listener)` | Удаляет слушателя |
| `removeAllListeners(String)` | Удаляет всех слушателей события |
| `emit(String, Object...)` | Запускает событие синхронно |
| `emitAsync(String, Object...)` | Запускает событие асинхронно |
| `emitWait(String, Object...)` | Запускает и ждёт завершения |
| `getListenerCount(String)` | Получает количество слушателей |
| `getRegisteredEvents()` | Список всех событий |
| `setEventMetadata(String, Object)` | Устанавливает метаданные события |
| `getEventMetadata(String)` | Получает метаданные события |

### Класс JavaScriptEvent

```java
public class JavaScriptEvent {
    public String getName()                    // Имя события
    public Object[] getArgs()                  // Все аргументы
    public Object getArg(int index)            // Конкретный аргумент
    public int getArgCount()                   // Количество аргументов
    public Map<String, Object> getMetadata()   // Метаданные
    public long getTimestamp()                 // Время запуска
}
```

---

## ⏱️ JavaScriptScheduler

Планировщик для отложенного выполнения кода.

### Базовое использование

```java
JavaScriptScheduler scheduler = new JavaScriptScheduler();

// setTimeout - выполнить через 1 секунду
String taskId = scheduler.setTimeout(() -> {
    System.out.println("Выполнено через 1 сек");
}, 1000);

// setInterval - выполнять каждые 2 секунды
String intervalId = scheduler.setInterval(() -> {
    System.out.println("Повторяющаяся задача");
}, 2000);

// Отменить задачу
scheduler.clearTimeout(taskId);
scheduler.clearInterval(intervalId);

// Ежедневная задача в 12:00
scheduler.scheduleDailyTask(() -> {
    System.out.println("Ежедневная задача");
}, 12, 0);

// Еженедельная задача в среду в 18:00
scheduler.scheduleWeeklyTask(() -> {
    System.out.println("Еженедельная задача");
}, Calendar.WEDNESDAY, 18, 0);
```

### API Методы

| Метод | Описание |
|-------|---------|
| `setTimeout(Runnable, long)` | Выполнить через задержку |
| `setInterval(Runnable, long)` | Выполнять с интервалом |
| `scheduleDailyTask(Runnable, int, int)` | Ежедневная задача |
| `scheduleWeeklyTask(Runnable, int, int, int)` | Еженедельная задача |
| `clearTimeout(String)` | Отменить setTimeout |
| `clearInterval(String)` | Отменить setInterval |
| `getRemainingTime(String)` | Осталось миллисекунд до выполнения |
| `isTaskActive(String)` | Проверить активна ли задача |
| `getActiveTasks()` | Список всех активных задач |
| `getTaskInfo(String)` | Информация о задаче |

---

## 📦 JavaScriptModuleManager

Управление JavaScript модулями с поддержкой зависимостей.

### Базовое использование

```java
JavaScriptModuleManager moduleManager = new JavaScriptModuleManager(engine);

// Регистрировать модуль
String mathModule = "exports.add = (a, b) => a + b; exports.multiply = (a, b) => a * b;";
moduleManager.registerModule("math", mathModule);

// Регистрировать модуль с зависимостями
String utilsModule = "const math = require('math'); exports.square = (x) => math.multiply(x, x);";
moduleManager.registerModule("utils", utilsModule, "math");

// Загрузить модуль
Object mathExports = moduleManager.loadModule("math");

// Использовать экспорты
Map<String, Object> exports = (Map<String, Object>) mathExports;
// exports.add, exports.multiply ...
```

### Структура модуля

```javascript
// Экспортировать значения
exports.functionName = function(args) {
    return result;
};

exports.variableName = value;

// Или альтернативный синтаксис
module.exports = {
    functionName: function(args) { ... },
    variableName: value
};
```

---

## 🖥️ LXXVServer - Bukkit API в JavaScript

Главный класс для регистрации всех Bukkit функций в JavaScript.

### Инициализация

```java
// В главном плагине
Server server = Bukkit.getServer();
JavaScriptEngine engine = JavaScriptEngine.getInstance();

LXXVServer.initialize(server, engine);
```

### Функции сервера

```javascript
// Трансляция сообщения
broadcast("Сервер перезагружается!");

// Информация о сервере
const onlinePlayers = getOnlinePlayers();  // Количество игроков
const maxPlayers = getMaxPlayers();        // Макс игроков
const motd = getMotd();                    // MOTD
const version = getVersion();              // Версия

// Управление сервером
setMotd("Новый MOTD");
reload();
shutdown();

// Команды
executeCommand("say Привет!");
```

### Функции игроков

```javascript
// Получить игрока
const player = getPlayer("Steve");
const players = getPlayers();  // Array всех игроков

// Отправить сообщение
sendMessage(player, "§aДобро пожаловать!");

// Инвентарь
giveItem(player, "DIAMOND_SWORD", 1);

// Телепортация
teleportPlayer(player, 0, 64, 0, "world");

// Здоровье и голод
const health = getPlayerHealth(player);
setPlayerHealth(player, 20);
const food = getPlayerFood(player);
setPlayerFood(player, 20);

// Опыт
const level = getPlayerExpLevel(player);
giveExp(player, 100);

// Разрешения
const hasAdmin = hasPermission(player, "admin.command");

// Информация
const location = getPlayerLocation(player);  // {x, y, z, world}
const world = getPlayerWorld(player);
const gameMode = getGameMode(player);
setGameMode(player, "CREATIVE");
```

### Функции мира

```javascript
// Получить мир
const world = getWorld("world");
const worlds = getWorlds();  // Array всех миров

// Время суток
const time = getTime(world);
setTime(world, 0);  // Полночь
setTime(world, 6000);  // Полдень

// Погода
const hasStorm = hasStorm(world);
setStorm(world, true);  // Включить дождь

// Сложность
const difficulty = getDifficulty(world);
```

### События

```javascript
// Слушать событие
addEventListener("playerJoin", (event) => {
    const playerName = event.getArg(0);
    broadcast("§e" + playerName + " присоединился!");
});

// Запустить событие
emit("customEvent", "arg1", 123);
emitAsync("longRunningEvent", data);

// Получить количество слушателей
const count = getListenerCount("playerJoin");
```

### Планировщик

```javascript
// setTimeout
const timeoutId = setTimeout(() => {
    broadcast("§c5 секунд прошло!");
}, 5000);

// setInterval
const intervalId = setInterval(() => {
    broadcast("§b10 секунд интервал");
}, 10000);

// Отменить
clearTimeout(timeoutId);
clearInterval(intervalId);

// Получить активные задачи
const tasks = getActiveTasks();
```

### Утилиты

```javascript
// Логирование
log("Информационное сообщение");
warn("Предупреждение");
error("Ошибка!");

// Время
const timestamp = now();  // Миллисекунды

// Память сервера
const memInfo = getMemoryInfo();
// {total, free, max, used}
```

---

## 🔗 REST API - ServerScriptController

API для выполнения JavaScript из WebX Dashboard.

### Запуск JavaScript кода

```
POST /api/script/execute
Content-Type: application/json

{
  "code": "2 + 2",
  "context": {
    "x": 10,
    "y": 20
  }
}

Response:
{
  "success": true,
  "result": 4,
  "timestamp": 1704067200000
}
```

### Асинхронное выполнение

```
POST /api/script/execute-async
{
  "code": "Math.sqrt(16)"
}
```

### Работа с событиями

```
// Запустить событие
POST /api/script/event/playerJoin
{
  "args": ["Steve"],
  "async": false
}

// Получить события
GET /api/script/events
Response:
{
  "success": true,
  "count": 3,
  "events": ["playerJoin", "playerLeave", "questComplete"]
}

// Получить слушателей события
GET /api/script/listeners/playerJoin
Response:
{
  "success": true,
  "eventName": "playerJoin",
  "listenerCount": 5
}
```

### Планировщик (setTimeout/setInterval)

```
// Создать задачу
POST /api/script/timeout
{
  "code": "broadcast('Hello!')",
  "delayMs": 5000
}
Response:
{
  "success": true,
  "taskId": "timeout_0",
  "delayMs": 5000
}

// Список активных задач
GET /api/script/tasks
Response:
{
  "success": true,
  "count": 2,
  "tasks": ["timeout_0", "interval_1"]
}

// Отменить задачу
DELETE /api/script/task/timeout_0
```

### Работа с модулями

```
// Регистрировать модуль
POST /api/script/module/math
{
  "code": "exports.add = (a, b) => a + b;",
  "dependencies": []
}

// Загрузить модуль
POST /api/script/module/math/load

// Список модулей
GET /api/script/modules
Response:
{
  "success": true,
  "registered": ["math", "utils"],
  "loaded": ["math"]
}
```

### Информация

```
// Информация о скриптинге
GET /api/script/info
Response:
{
  "success": true,
  "info": {
    "engine": "GraalVM JavaScript 22.3.0",
    "events": 10,
    "tasks": 3,
    "loadedScripts": 5,
    "modules": {
      "registered": 8,
      "loaded": 3
    }
  }
}

// Глобальные переменные
GET /api/script/globals
Response:
{
  "success": true,
  "count": 25,
  "globals": ["broadcast", "getPlayer", "log", ...]
}
```

---

## 🎨 WebX Dashboard компоненты

### ScriptConsoleTab

Интерактивная консоль для выполнения JavaScript.

```tsx
<ScriptConsoleTab />
```

**Функционал:**
- Редактор кода с подсветкой
- Синхронное и асинхронное выполнение
- Отображение результатов и ошибок
- История выполнения

### TaskSchedulerTab

Управление запланированными задачами.

```tsx
<TaskSchedulerTab />
```

**Функционал:**
- Список активных задач
- Отсчёт времени до выполнения
- Отмена задач
- Real-time обновление

### EventSystemTab

Система событий с возможностью запуска.

```tsx
<EventSystemTab />
```

**Функционал:**
- Запуск произвольных событий
- Список зарегистрированных событий
- Счётчик слушателей
- Передача аргументов JSON

---

## 📝 Примеры скриптов

### Пример 1: Ежедневный бонус

```javascript
// Запустить каждый день в 00:00
scheduleDailyTask(() => {
  const players = getPlayers();
  for (let player of players) {
    giveExp(player, 100);
    sendMessage(player, "§aДневной бонус: +100 XP");
  }
}, 0, 0);
```

### Пример 2: Событие присоединения

```javascript
addEventListener("playerJoin", (event) => {
  const playerName = event.getArg(0);
  const player = getPlayer(playerName);
  
  sendMessage(player, "§eДобро пожаловать на сервер!");
  broadcast("§a" + playerName + " присоединился");
  
  // Дать приветственный подарок
  setTimeout(() => {
    giveItem(player, "GOLDEN_APPLE", 1);
  }, 2000);
});
```

### Пример 3: Автосохранение каждые 10 минут

```javascript
setInterval(() => {
  executeCommand("save-all");
  log("Сервер сохранён");
}, 10 * 60 * 1000);
```

---

## 🔒 Безопасность

### Ограничения в Sandbox

- Файловая система (по умолчанию запрещена)
- Сетевой доступ (по умолчанию запрещен)
- Создание процессов (по умолчанию запрещено)
- eval() функция
- Динамическое создание функций

### Лучшие практики

1. **Валидируйте входные данные** перед использованием в команде
2. **Используйте Sandbox** для untrusted кода
3. **Обрабатывайте исключения** JavaScriptException
4. **Логируйте вызовы** важных функций
5. **Ограничивайте таймауты** для длительных операций

---

## 📊 Производительность

| Операция | Время |
|----------|-------|
| Простое выражение | ~0.5ms |
| Вызов функции | ~1ms |
| Событие с 5 слушателями | ~3ms |
| setTimeout регистрация | ~0.1ms |
| Запуск события | ~0.5ms |

---

## ⚙️ Конфигурация в build.gradle.kts

```gradle
dependencies {
    implementation("org.graalvm.js:js:22.3.0")
    implementation("org.graalvm.js:js-scriptengine:22.3.0")
    implementation("io.javalin:javalin:5.6.2")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.slf4j:slf4j-api:2.0.5")
}
```

---

## 🤝 Интеграция в плагины

### QuestsPlugin пример

```java
public class QuestsPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        // Инициализировать V8
        JavaScriptEngine engine = JavaScriptEngine.getInstance();
        JavaScriptScriptManager scriptManager = new JavaScriptScriptManager(
            new File(getDataFolder(), "scripts")
        );
        
        // Инициализировать LXXVServer
        LXXVServer.initialize(getServer(), engine);
        
        // Регистрировать кастомные функции
        engine.registerFunction("addQuest", args -> {
            // ... реализация
        });
        
        // Запустить API сервер
        Javalin app = Javalin.create().start(7071);
        ServerScriptController controller = new ServerScriptController(
            engine, scriptManager, 
            LXXVServer.getEventSystem(),
            LXXVServer.getScheduler(),
            LXXVServer.getModuleManager()
        );
        controller.register(app);
    }
}
```

---

## 📚 Дополнительные ресурсы

- [V8 QuickStart Guide](./V8_QUICKSTART.md)
- [JavaScript Examples](./examples/scripts/)
- [GraalVM JS Documentation](https://www.graalvm.org/latest/reference-manual/js/)
- [Bukkit API Documentation](https://hub.spigotmc.org/javadocs/bukkit/)

---

**Версия:** 2.0.0  
**Дата:** 2024-01-12  
**Автор:** WebX Development Team
