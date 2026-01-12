# V8 JavaScript Integration for Common Module

## Обзор

Полная интеграция JavaScript (V8/GraalVM) в модуль common для выполнения JavaScript кода из Java с полной функциональностью.

---

## Компоненты

### 1. JavaScriptEngine
Основной движок для выполнения JavaScript кода.

```java
JavaScriptEngine engine = JavaScriptEngine.getInstance();

// Выполнить код
Object result = engine.execute("2 + 2");

// Выполнить асинхронно
engine.executeAsync("Math.sqrt(16)").thenAccept(result -> {
    System.out.println(result); // 4.0
});

// С переменными контекста
Map<String, Object> vars = new HashMap<>();
vars.put("x", 10);
vars.put("y", 20);
Object sum = engine.execute("x + y", vars); // 30

// Вызвать функцию
Object result = engine.callFunction(
    "function add(a, b) { return a + b; }",
    5, 3
); // 8

// Зарегистрировать Java функцию
engine.registerFunction("log", args -> {
    System.out.println(args[0]);
    return null;
});

// Использовать в JavaScript
engine.execute("log('Hello from JS!')");
```

---

### 2. JavaScriptException
Исключение для ошибок JavaScript.

```java
try {
    engine.execute("invalid javascript !!!");
} catch (JavaScriptException e) {
    System.err.println(e.getMessage());
}
```

---

### 3. JavaScriptSandbox
Безопасное окружение с ограничениями.

```java
// Создать sandbox с ограничениями
JavaScriptSandbox sandbox = new JavaScriptSandbox.Builder()
    .allowGlobal("name", "Alice")
    .allowGlobal("age", 25)
    .allowFileAccess(false)
    .allowNetworkAccess(false)
    .allowProcessAccess(false)
    .executionTimeout(3000)
    .build();

// Выполнить в sandbox
try {
    Object result = sandbox.execute(
        "name.toUpperCase() + ', age: ' + age"
    ); // "ALICE, age: 25"
} catch (JavaScriptException e) {
    e.printStackTrace();
}

// Код с file access будет отклонен
try {
    sandbox.execute("fs.readFileSync('/etc/passwd')");
} catch (JavaScriptException e) {
    // "Code contains restricted operations"
}
```

---

### 4. JavaScriptUtils
Утилиты для частых операций.

```java
// Математика
double result = JavaScriptUtils.math("Math.pow(2, 8)"); // 256.0

// Условия
Map<String, Object> ctx = Map.of("x", 10, "y", 20);
boolean result = JavaScriptUtils.condition("x > y", ctx); // false

// Трансформация значений
Object transformed = JavaScriptUtils.transform(
    "x => x * 2",
    5
); // 10

// Фильтрация
Object[] filtered = JavaScriptUtils.filter(
    new Object[]{1, 2, 3, 4, 5},
    "x => x > 2"
); // [3, 4, 5]

// Маппинг
Object[] mapped = JavaScriptUtils.map(
    new Object[]{1, 2, 3},
    "x => x * 2"
); // [2, 4, 6]

// Редукция
Object sum = JavaScriptUtils.reduce(
    new Object[]{1, 2, 3, 4},
    "(acc, x) => acc + x",
    0
); // 10

// Работа с JSON
String json = JavaScriptUtils.stringify(obj);
Object parsed = JavaScriptUtils.parse(json);

// Проверка типов
String type = JavaScriptUtils.getType("hello"); // "string"
```

---

### 5. JavaScriptScriptManager
Управление JavaScript скриптами как файлами.

```java
File scriptsDir = new File("plugins/scripts");
JavaScriptScriptManager manager = new JavaScriptScriptManager(scriptsDir);

// Загрузить все скрипты
manager.loadAllScripts();

// Выполнить скрипт
Object result = manager.executeScript("myScript");

// С контекстом
Map<String, Object> ctx = Map.of("player", player, "data", data);
Object result = manager.executeScript("processData", ctx);

// Вызвать функцию из скрипта
Object result = manager.executeScriptFunction(
    "utils",
    "calculateDamage",
    10,
    2.5,
    "fire"
);

// Асинхронно
manager.executeScriptAsync("heavyComputation")
    .thenAccept(result -> System.out.println(result));

// Перезагрузить скрипт
manager.reloadScript("myScript");

// Получить список загруженных
String[] scripts = manager.getLoadedScripts();
```

---

## Примеры Использования

### Пример 1: Обработка игровых событий

```javascript
// events/PlayerDamage.js
function onPlayerDamage(player, damage, damageType) {
    let baseDamage = damage;
    let modifiers = {
        fire: 1.5,
        ice: 0.8,
        normal: 1.0
    };
    
    let multiplier = modifiers[damageType] || 1.0;
    let finalDamage = baseDamage * multiplier;
    
    return {
        damage: finalDamage,
        type: damageType,
        reduction: finalDamage * 0.1,
        blocked: false
    };
}
```

```java
// Java код
JavaScriptScriptManager manager = new JavaScriptScriptManager(new File("plugins/scripts"));
manager.loadScript("PlayerDamage", new File("plugins/scripts/events/PlayerDamage.js"));

double damage = 20;
String damageType = "fire";

Object result = manager.executeScriptFunction(
    "PlayerDamage",
    "onPlayerDamage",
    player, damage, damageType
);

// result = {damage: 30.0, type: "fire", reduction: 3.0, blocked: false}
```

---

### Пример 2: Вычисление наград на основе формулы

```java
JavaScriptEngine engine = JavaScriptEngine.getInstance();

Map<String, Object> context = new HashMap<>();
context.put("baseReward", 100);
context.put("level", 10);
context.put("difficulty", 2.5);
context.put("bonusMultiplier", 1.2);

Object reward = engine.execute(
    "baseReward * level * difficulty * bonusMultiplier",
    context
);

System.out.println("Reward: " + reward); // 3000.0
```

---

### Пример 3: Фильтрация и трансформация данных

```java
List<Quest> quests = questManager.getQuests();
Object[] questArray = quests.toArray();

// Отфильтровать квесты сложности HARD и выше
Object[] filtered = JavaScriptUtils.filter(
    questArray,
    "q => ['HARD', 'EXPERT', 'LEGENDARY'].includes(q.difficulty)"
);

// Трансформировать в награды
Object[] rewards = JavaScriptUtils.map(
    filtered,
    "q => ({ questId: q.id, baseReward: q.reward * q.difficulty.multiplier })"
);
```

---

### Пример 4: Валидация конфигурации

```java
String configJson = "{ \"name\": \"my-plugin\", \"version\": \"1.0\", \"enabled\": true }";

JavaScriptSandbox sandbox = new JavaScriptSandbox.Builder()
    .executionTimeout(1000)
    .build();

// Валидировать структуру
boolean isValid = sandbox.execute(
    "let config = " + configJson + "; " +
    "typeof config.name === 'string' && " +
    "typeof config.version === 'string' && " +
    "typeof config.enabled === 'boolean'"
).equals(true);
```

---

### Пример 5: Кастомные Java функции в JavaScript

```java
JavaScriptEngine engine = JavaScriptEngine.getInstance();

// Зарегистрировать функцию для логирования
engine.registerFunction("logInfo", args -> {
    System.out.println("[INFO] " + args[0]);
    return null;
});

// Зарегистрировать функцию для получения данных игрока
engine.registerFunction("getPlayerHealth", args -> {
    String playerName = args[0].toString();
    Player player = Bukkit.getPlayer(playerName);
    return player != null ? player.getHealth() : 0.0;
});

// Использовать в JavaScript
engine.execute(
    "logInfo('Player health: ' + getPlayerHealth('Steve'))"
);
```

---

### Пример 6: Сложная бизнес-логика

```javascript
// quests/QuestCalculations.js
function calculateQuestReward(playerLevel, questDifficulty, baseReward) {
    const difficultyMultipliers = {
        EASY: 1.0,
        MEDIUM: 1.5,
        HARD: 2.0,
        EXPERT: 3.0,
        LEGENDARY: 5.0
    };
    
    const levelBonus = 1 + (playerLevel / 100);
    const diffMultiplier = difficultyMultipliers[questDifficulty] || 1.0;
    
    const totalReward = baseReward * levelBonus * diffMultiplier;
    const taxAmount = totalReward * 0.1; // 10% tax
    const finalReward = totalReward - taxAmount;
    
    return {
        base: baseReward,
        withLevel: baseReward * levelBonus,
        withDifficulty: baseReward * levelBonus * diffMultiplier,
        tax: taxAmount,
        final: Math.floor(finalReward)
    };
}

function validateQuestCompletion(objectives) {
    return objectives.every(obj => obj.progress >= obj.required);
}

function calculateNextLevel(currentXP, nextLevelXP) {
    return {
        current: currentXP,
        nextLevel: nextLevelXP,
        remaining: Math.max(0, nextLevelXP - currentXP),
        percentage: Math.round((currentXP / nextLevelXP) * 100)
    };
}
```

```java
JavaScriptScriptManager manager = new JavaScriptScriptManager(new File("plugins/scripts"));
manager.loadScript("QuestCalculations", new File("plugins/scripts/quests/QuestCalculations.js"));

// Вычислить награду
Object reward = manager.executeScriptFunction(
    "QuestCalculations",
    "calculateQuestReward",
    15, // уровень игрока
    "HARD", // сложность
    500 // базовая награда
);
// Result: {base: 500, withLevel: 575, withDifficulty: 1150, tax: 115, final: 1035}

// Проверить завершение
Object[] objectives = {...};
Object isValid = manager.executeScriptFunction(
    "QuestCalculations",
    "validateQuestCompletion",
    objectives
);
```

---

## Производительность

- **Синхронное выполнение**: ~1-5ms для простых операций
- **Асинхронное выполнение**: ~0.5-2ms в отдельном потоке
- **Кэширование скриптов**: Скрипты загружаются один раз и остаются в памяти
- **Timeout**: По умолчанию 5 секунд для защиты от бесконечных циклов

---

## Безопасность

1. **Sandboxing**: Ограничение доступа к файловой системе, сети и процессам
2. **Timeout**: Защита от бесконечных циклов
3. **Валидация кода**: Проверка на опасные операции перед выполнением
4. **Контролируемый контекст**: Только разрешенные переменные видны в скриптах

---

## Зависимости

```gradle
// GraalVM JavaScript
implementation("org.graalvm.js:js:22.3.0")
implementation("org.graalvm.js:js-scriptengine:22.3.0")

// J2V8 (alternative)
implementation("com.eclipsesource.j2v8:j2v8:6.2.0")
```

---

## Лучшие Практики

1. **Используйте async** для тяжелых вычислений
2. **Кэшируйте скрипты** через ScriptManager
3. **Валидируйте входные данные** в JavaScript
4. **Используйте Sandbox** для untrusted кода
5. **Обрабатывайте исключения** JavaScriptException
6. **Логируйте ошибки** для отладки

---

## Авторы

WebX Development Team © 2024
