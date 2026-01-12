# Common Module - Shared Library for All Plugins

Централизованный модуль с общими функциями для всех плагинов проекта.

## Содержание

### 1. Shared Database (lxxv.shared.dbjson)
Потокобезопасная база данных для всех плагинов.

**Классы:**
- `SharedPluginDatabase` - Основной singleton для управления данными
- `PluginDataHelper` - Удобные методы для работы с данными

**Функционал:**
- Асинхронная работа с данными
- JSON сериализация
- ReadWriteLock для потокобезопасности
- Автосохранение

[Подробно →](./README.md#shared-database)

### 2. JavaScript Integration (lxxv.shared.javascript)
Полная интеграция V8 JavaScript для выполнения скриптов из Java.

**Классы:**
- `JavaScriptEngine` - Основной движок для выполнения кода
- `JavaScriptSandbox` - Безопасное выполнение untrusted кода
- `JavaScriptUtils` - Утилиты для частых операций
- `JavaScriptScriptManager` - Управление скриптами как файлами
- `JavaScriptFunction` - Интерфейс для Java функций
- `JavaScriptException` - Исключения

**Функционал:**
- Выполнение JavaScript кода
- Синхронное и асинхронное выполнение
- Работа с переменными контекста
- Кастомные Java функции
- Безопасное выполнение в Sandbox
- Загрузка скриптов из файлов

[Quick Start →](./V8_QUICKSTART.md)
[Полная документация →](./JAVASCRIPT_INTEGRATION.md)

---

## Использование в Плагинах

### Подключение в build.gradle.kts

```gradle
dependencies {
    implementation(project(":common"))
}
```

### Использование SharedPluginDatabase

```java
import lxxv.shared.dbjson.SharedPluginDatabase;
import lxxv.shared.dbjson.PluginDataHelper;

// Получить синглтон БД
SharedPluginDatabase db = SharedPluginDatabase.getInstance(dataFolder);

// Работать с данными
db.setPluginValue("MyPlugin", "key", "value");
Object value = db.getPluginValue("MyPlugin", "key");

// Или через helper
PluginDataHelper helper = new PluginDataHelper(db, "MyPlugin");
helper.setPlayerInt(playerId, "level", 25);
int level = helper.getPlayerInt(playerId, "level", 1);
```

### Использование JavaScript Engine

```java
import lxxv.shared.javascript.*;

// Выполнить код
JavaScriptEngine engine = JavaScriptEngine.getInstance();
Object result = engine.execute("2 + 2");

// С контекстом
Map<String, Object> ctx = new HashMap<>();
ctx.put("x", 10);
Object sum = engine.execute("x + 5", ctx);

// Асинхронно
engine.executeAsync("Math.sqrt(16)")
    .thenAccept(result -> System.out.println(result));

// Sandbox для безопасности
JavaScriptSandbox sandbox = new JavaScriptSandbox.Builder()
    .allowGlobal("data", data)
    .executionTimeout(5000)
    .build();
Object safe = sandbox.execute("data.process()");
```

---

## Файловая Структура

```
common/
├── build.gradle.kts                          # Конфиг сборки
├── README.md                                 # Этот файл
├── V8_QUICKSTART.md                          # Quick Start Guide
├── JAVASCRIPT_INTEGRATION.md                 # Полная документация
├── src/main/java/
│   └── lxxv/
│       └── shared/
│           ├── dbjson/
│           │   ├── SharedPluginDatabase.java
│           │   └── PluginDataHelper.java
│           └── javascript/
│               ├── JavaScriptEngine.java
│               ├── JavaScriptException.java
│               ├── JavaScriptFunction.java
│               ├── JavaScriptSandbox.java
│               ├── JavaScriptUtils.java
│               ├── JavaScriptScriptManager.java
│               └── examples/
│                   ├── JavaScriptEngineExamples.java
│                   └── QuestRewardSystemExample.java
├── examples/
│   └── scripts/
│       ├── math_operations.js
│       ├── array_operations.js
│       ├── string_manipulation.js
│       ├── quest_rewards.js
│       ├── conditions_decisions.js
│       ├── date_time.js
│       └── game_mechanics.js
└── [build artifacts]
```

---

## Зависимости

```gradle
dependencies {
    // Bukkit API
    compileOnly("org.bukkit:bukkit:1.20.4-R0.1-SNAPSHOT")
    
    // JSON обработка
    implementation("com.google.code.gson:gson:2.10.1")
    
    // JavaScript (GraalVM)
    implementation("org.graalvm.js:js:22.3.0")
    implementation("org.graalvm.js:js-scriptengine:22.3.0")
}
```

---

## API Reference

### SharedPluginDatabase

```java
// Получить синглтон
SharedPluginDatabase db = SharedPluginDatabase.getInstance(cacheDir);

// Управление данными плагина
void setPluginValue(String plugin, String key, Object value);
Object getPluginValue(String plugin, String key);
Map<String, Object> getPluginData(String plugin);

// Управление данными игрока
void setPlayerValue(String playerId, String key, Object value);
Object getPlayerValue(String playerId, String key);

// Глобальные данные
void setGlobalValue(String key, Object value);
Object getGlobalValue(String key);

// Сохранение и загрузка
void save();
void reload();
```

### JavaScriptEngine

```java
// Выполнение кода
Object execute(String code) throws JavaScriptException;
Object execute(String code, Map<String, Object> variables) throws JavaScriptException;
CompletableFuture<Object> executeAsync(String code);

// Функции
Object callFunction(String code, Object... args) throws JavaScriptException;
void registerFunction(String name, JavaScriptFunction function);

// Глобальные переменные
void setGlobalVariable(String name, Object value);
Object getGlobalVariable(String name);

// JSON операции
Object parseJSON(String json) throws JavaScriptException;
String toJSON(Object obj) throws JavaScriptException;

// Математика
double evaluateMath(String expression) throws JavaScriptException;

// Состояние
boolean isEnabled();
void shutdown();
```

### JavaScriptUtils

```java
// Математика
static double math(String expression) throws JavaScriptException;

// Строки
static String format(String template, Map<String, Object> variables);
static String stringify(Object obj);
static Object parse(String json);

// Условия
static boolean condition(String condition, Map<String, Object> context);

// Трансформация
static Object transform(String function, Object value);
static Object[] filter(Object[] array, String condition);
static Object[] map(Object[] array, String function);
static Object reduce(Object[] array, String function, Object initial);

// Типы
static String getType(Object value);
static boolean isInstanceOf(Object value, String type);

// Объекты
static Object deepClone(Object object);
static Object merge(Object obj1, Object obj2);
```

### JavaScriptSandbox

```java
// Создание
JavaScriptSandbox.Builder builder = new JavaScriptSandbox.Builder();
builder.allowGlobal(String name, Object value);
builder.allowFileAccess(boolean allow);
builder.allowNetworkAccess(boolean allow);
builder.allowProcessAccess(boolean allow);
builder.executionTimeout(long ms);
JavaScriptSandbox sandbox = builder.build();

// Выполнение
Object execute(String code) throws JavaScriptException;
Object executeWithTimeout(String code) throws JavaScriptException;
```

### JavaScriptScriptManager

```java
// Загрузка
void loadScript(String name, File file) throws IOException, JavaScriptException;
void loadAllScripts() throws IOException, JavaScriptException;

// Выполнение
Object executeScript(String name) throws JavaScriptException;
Object executeScript(String name, Map<String, Object> context);
CompletableFuture<Object> executeScriptAsync(String name);

// Функции
Object executeScriptFunction(String scriptName, String functionName, Object... args);

// Управление
void reloadScript(String name) throws IOException, JavaScriptException;
void removeScript(String name);
void clearAllScripts();
String[] getLoadedScripts();
```

---

## Примеры

### Пример 1: Простые вычисления

```java
JavaScriptEngine engine = JavaScriptEngine.getInstance();

// Базовая математика
Object result = engine.execute("2 + 2 * 3"); // 8

// С переменными
Map<String, Object> ctx = Map.of("x", 5, "y", 3);
Object product = engine.execute("x * y", ctx); // 15
```

### Пример 2: Квесты и награды

```javascript
// quests/rewards.js
function calculateReward(baseReward, playerLevel, difficulty) {
    const multipliers = {
        EASY: 1.0,
        MEDIUM: 1.5,
        HARD: 2.0,
        EXPERT: 3.0,
        LEGENDARY: 5.0
    };
    
    const levelBonus = 1 + (playerLevel / 100);
    const diffMultiplier = multipliers[difficulty] || 1.0;
    
    return Math.round(baseReward * levelBonus * diffMultiplier);
}
```

```java
JavaScriptScriptManager manager = new JavaScriptScriptManager(scriptsDir);
manager.loadScript("rewards", new File("quests/rewards.js"));

Object reward = manager.executeScriptFunction(
    "rewards",
    "calculateReward",
    100,  // baseReward
    20,   // playerLevel
    "HARD" // difficulty
); // Result: 450
```

### Пример 3: Безопасное выполнение

```java
JavaScriptSandbox sandbox = new JavaScriptSandbox.Builder()
    .allowGlobal("health", 100)
    .allowGlobal("maxHealth", 100)
    .allowFileAccess(false)
    .executionTimeout(5000)
    .build();

// Это работает
Object healthPercent = sandbox.execute(
    "Math.round((health / maxHealth) * 100) + '%'"
);

// Это будет заблокировано
try {
    sandbox.execute("require('fs').readFileSync('/etc/passwd')");
} catch (JavaScriptException e) {
    // Code contains restricted operations
}
```

---

## Лучшие Практики

### 1. Кэширование скриптов
```java
// ПЛОХО - загружает скрипт каждый раз
Object result = engine.execute(readScriptFile());

// ХОРОШО - кэширует в памяти
manager.loadScript("myScript", scriptFile);
Object result = manager.executeScript("myScript");
```

### 2. Асинхронное выполнение
```java
// Для тяжелых операций
engine.executeAsync(heavyCode)
    .thenAccept(result -> {
        // Обработать результат
    });
```

### 3. Безопасность
```java
// НИКОГДА не выполняйте untrusted код без Sandbox
// ВСЕГДА используйте Sandbox для пользовательских скриптов
JavaScriptSandbox sandbox = new JavaScriptSandbox.Builder()
    .executionTimeout(5000)
    .build();
```

### 4. Обработка ошибок
```java
try {
    Object result = engine.execute(code);
} catch (JavaScriptException e) {
    logger.log(Level.SEVERE, "JavaScript error", e);
}
```

---

## Поддержка

- **Issues:** GitHub Issues
- **Documentation:** Смотрите `*.md` файлы
- **Examples:** `examples/` директория

---

## Версия

- **Версия:** 1.0.0
- **Java:** 11+
- **GraalVM JS:** 22.3.0
- **Bukkit/Paper:** 1.20.4+

---

## Авторы

WebX Development Team © 2024
