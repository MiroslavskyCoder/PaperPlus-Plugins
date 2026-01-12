# V8 JavaScript Integration - Полный Индекс

## 📚 Документация

### Основные Гайды
1. **[Javet Integration](./JAVET_INTEGRATION.md)** - Java + V8 (Рекомендуется)
2. **[V8 Quick Start](./V8_QUICKSTART.md)** - Быстрый старт для новичков
3. **[JavaScript Integration Documentation](./JAVASCRIPT_INTEGRATION.md)** - Полная документация
4. **[Common Module README](./COMMON_README.md)** - Обзор модуля common

---

## 🎯 Компоненты JavaScript

### 1. JavaScriptEngine
**Файл:** `lxxv/shared/javascript/JavaScriptEngine.java`

Основной компонент для выполнения JavaScript кода из Java.

**Основные методы:**
- `execute(String code)` - Синхронное выполнение
- `execute(String code, Map<String, Object> variables)` - С переменными контекста
- `executeAsync(String code)` - Асинхронное выполнение
- `callFunction(String code, Object... args)` - Вызов функции
- `registerFunction(String name, JavaScriptFunction func)` - Регистрация Java функции
- `getInstance()` - Получить синглтон

**Пример:**
```java
JavaScriptEngine engine = JavaScriptEngine.getInstance();
Object result = engine.execute("2 + 2"); // 4
```

---

### 2. JavaScriptException
**Файл:** `lxxv/shared/javascript/JavaScriptException.java`

Исключение для ошибок выполнения JavaScript.

**Использование:**
```java
try {
    engine.execute("invalid code");
} catch (JavaScriptException e) {
    System.err.println(e.getMessage());
}
```

---

### 3. JavaScriptFunction
**Файл:** `lxxv/shared/javascript/JavaScriptFunction.java`

Функциональный интерфейс для создания Java функций, вызываемых из JavaScript.

**Реализация:**
```java
engine.registerFunction("multiply", (args) -> {
    double a = ((Number) args[0]).doubleValue();
    double b = ((Number) args[1]).doubleValue();
    return a * b;
});

engine.execute("multiply(5, 3)"); // 15
```

---

### 4. JavaScriptSandbox
**Файл:** `lxxv/shared/javascript/JavaScriptSandbox.java`

Безопасное окружение для выполнения untrusted кода.

**Функции безопасности:**
- Ограничение доступа к файловой системе
- Ограничение сетевого доступа
- Защита от создания процессов
- Таймауты для защиты от бесконечных циклов

**Использование:**
```java
JavaScriptSandbox sandbox = new JavaScriptSandbox.Builder()
    .allowGlobal("data", data)
    .allowFileAccess(false)
    .executionTimeout(5000)
    .build();

Object result = sandbox.execute("data.process()");
```

---

### 5. JavaScriptUtils
**Файл:** `lxxv/shared/javascript/JavaScriptUtils.java`

Утилиты для частых операций с JavaScript.

**Методы:**
- `math(String expression)` - Математические вычисления
- `condition(String condition, Map context)` - Проверка условия
- `transform(String func, Object value)` - Трансформация значения
- `filter(Object[] array, String condition)` - Фильтрация массива
- `map(Object[] array, String func)` - Маппинг массива
- `reduce(Object[] array, String func, Object initial)` - Редукция
- `parse(String json)` - Парсинг JSON
- `stringify(Object obj)` - Сериализация в JSON
- `getType(Object value)` - Определение типа
- `deepClone(Object object)` - Глубокое клонирование
- `merge(Object obj1, Object obj2)` - Слияние объектов

**Пример:**
```java
double sqrt = JavaScriptUtils.math("Math.sqrt(16)"); // 4.0
Object[] filtered = JavaScriptUtils.filter(
    array,
    "x => x > 5"
);
```

---

### 6. JavaScriptScriptManager
**Файл:** `lxxv/shared/javascript/JavaScriptScriptManager.java`

Управление JavaScript скриптами как ресурсами/файлами.

**Методы:**
- `loadScript(String name, File file)` - Загрузить скрипт из файла
- `loadAllScripts()` - Загрузить все скрипты из директории
- `executeScript(String name)` - Выполнить скрипт
- `executeScriptAsync(String name)` - Асинхронно
- `executeScriptFunction(String script, String func, Object... args)` - Вызвать функцию
- `reloadScript(String name)` - Перезагрузить скрипт
- `getLoadedScripts()` - Список загруженных скриптов

**Пример:**
```java
JavaScriptScriptManager manager = new JavaScriptScriptManager(
    new File("plugins/scripts")
);
manager.loadAllScripts();
Object result = manager.executeScriptFunction(
    "combat",
    "calculateDamage",
    10, 2.5
);
```

---

## 📂 Примеры Скриптов

### 1. math_operations.js
Математические операции и вычисления.

**Функции:**
- `basicMath()` - Базовые операции
- `calculatePercentage(value, total)` - Процент
- `calculateDamage(base, weapon, resistance)` - Расчет урона

---

### 2. array_operations.js
Работа с массивами и объектами.

**Функции:**
- `processPlayerData(player)` - Обработка данных игрока
- `filterHighLevelPlayers(players)` - Фильтрация по уровню
- `mapPlayersToNames(players)` - Маппинг на имена
- `calculateTotalExp(players)` - Сумма опыта
- `groupPlayersByLevel(players)` - Группировка по уровню

---

### 3. string_manipulation.js
Работа со строками.

**Функции:**
- `processQuestText(name, difficulty, level)` - Форматирование квеста
- `formatCurrency(amount)` - Форматирование денег
- `sanitizePlayerName(name)` - Очистка имени
- `createMessageTemplate(template, variables)` - Template обработка

---

### 4. quest_rewards.js
Система расчета наград за квесты.

**Функции:**
- `calculateQuestReward(config)` - Полный расчет награды
- `calculateLevelUpXp(level)` - XP для следующего уровня
- `validateQuestCompletion(progress)` - Проверка завершения

---

### 5. conditions_decisions.js
Условная логика и деревья решений.

**Функции:**
- `determineDamageType(weapon, enchantments)` - Определение типа урона
- `getQuestNextStep(progress, total)` - Следующий шаг квеста
- `evaluatePlayerRank(quests, kills, deaths, playtime)` - Определение ранга

---

### 6. date_time.js
Работа с датой и временем.

**Функции:**
- `getCurrentTimestamp()` - Текущее время
- `formatDateTime(timestamp)` - Форматирование даты
- `calculateDaysSince(timestamp)` - Дни с момента
- `calculateTimeUntilNextReset(hour)` - Время до ресета
- `isWithinDaily(time, questId)` - Проверка дневного квеста

---

### 7. game_mechanics.js
Игровые механики и расчеты.

**Функции:**
- `calculateInventorySpace(items, maxSlots)` - Место в инвентаре
- `calculateCombinedStats(base, equipment)` - Объединение статов
- `calculateEnchantmentCost(level)` - Стоимость зачарования
- `predictNextUpgrade(stats, bonus)` - Предсказание улучшения

---

## 💻 Java Примеры

### 1. JavaScriptEngineExamples
**Файл:** `lxxv/shared/javascript/examples/JavaScriptEngineExamples.java`

Примеры базового использования движка.

**Примеры:**
- Базовое выполнение
- Использование контекста переменных
- Регистрация Java функций
- Использование утилит
- Sandbox выполнение
- Script Manager

---

### 2. QuestRewardSystemExample
**Файл:** `lxxv/shared/javascript/examples/QuestRewardSystemExample.java`

Пример системы расчета наград за квесты.

**Методы:**
- `calculateReward()` - Расчет награды
- `canCompleteQuest()` - Проверка возможности
- `getXpForNextLevel()` - XP до следующего уровня

---

## 🚀 Быстрый Старт

### 1. Базовый код
```java
JavaScriptEngine engine = JavaScriptEngine.getInstance();
Object result = engine.execute("2 + 2");
```

### 2. С контекстом
```java
Map<String, Object> ctx = new HashMap<>();
ctx.put("x", 10);
Object result = engine.execute("x * 2", ctx);
```

### 3. С функциями
```java
engine.registerFunction("log", args -> {
    System.out.println(args[0]);
    return null;
});
engine.execute("log('Hello!')");
```

### 4. Асинхронно
```java
engine.executeAsync("Math.sqrt(16)")
    .thenAccept(result -> System.out.println(result));
```

### 5. Безопасно
```java
JavaScriptSandbox sandbox = new JavaScriptSandbox.Builder()
    .executionTimeout(5000)
    .build();
Object result = sandbox.execute("1 + 1");
```

---

## 📊 Производительность

| Операция | Время |
|----------|-------|
| Простой расчет | ~0.5ms |
| Вызов функции | ~1ms |
| Фильтрация (100 элементов) | ~2-3ms |
| Асинхронное выполнение | ~0.5ms |

---

## ⚙️ Конфигурация

### build.gradle.kts
```gradle
dependencies {
    // Javet - Java + V8 (рекомендуется)
    implementation("com.caoccao.javet:javet:3.1.3")
    
    // GraalVM JS (fallback)
    implementation("org.graalvm.js:js:22.3.0")
    implementation("org.graalvm.js:js-scriptengine:22.3.0")
}
```

---

## 🔒 Безопасность

### Доступные операции
✅ Math операции
✅ String операции
✅ Array/Object операции
✅ JSON операции
✅ Условная логика

### Заблокированные операции
❌ Файловая система (по умолчанию)
❌ Сетевой доступ (по умолчанию)
❌ Создание процессов (по умолчанию)

---

## 📝 Лучшие Практики

1. **Кэшируйте скрипты** - Используйте ScriptManager
2. **Используйте async** для тяжелых операций
3. **Применяйте Sandbox** для untrusted кода
4. **Обрабатывайте исключения** - Ловите JavaScriptException
5. **Валидируйте входные данные** - Проверяйте перед использованием
6. **Логируйте ошибки** - Для отладки

---

## 🔗 Ссылки

- [GraalVM JavaScript](https://www.graalvm.org/latest/reference-manual/js/)
- [ECMAScript 2023](https://tc39.es/ecma262/)
- [Mozilla JavaScript Reference](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/)

---

## 📦 Версионирование

- **Common Module:** 1.0.0
- **GraalVM JS:** 22.3.0
- **Java:** 11+
- **Bukkit/Paper:** 1.20.4+

---

## 👥 Авторы

WebX Development Team © 2024
