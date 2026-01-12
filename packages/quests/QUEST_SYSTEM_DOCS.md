# Quests Plugin - Advanced Quest System

## Обзор

Расширенная система квестов с поддержкой:
- **30+ готовых квестов** с различной сложностью
- **5 уровней сложности** (EASY, MEDIUM, HARD, EXPERT, LEGENDARY)
- **20 типов квестов** (убийство, сбор, крафт, исследование и др.)
- **REST API** для управления через WebX Panel
- **Асинхронная БД** через SharedPluginDatabase
- **Сложная логика** квестов (цепочки, требования, таймеры)

---

## Типы Квестов

### 1. KILL - Убийство монстров/мобов
- Пример: Убить 10 зомби, 50 криперов, Эндер Дракона

### 2. COLLECT - Сбор предметов
- Пример: Собрать 64 дубовых бревна, 64 обсидиана

### 3. BREAK - Разрушение блоков
- Пример: Сломать 128 камня

### 4. PLACE - Размещение блоков
- Пример: Построить здание (400+ блоков)

### 5. CRAFT - Создание предметов
- Пример: Создать железную броню, незеритовый набор

### 6. MINE - Добыча руд
- Пример: Добыть 5 алмазов, 50 железа

### 7. EXPLORE - Исследование
- Пример: Посетить 3 биома, найти сундуки с сокровищами

### 8. FARM - Фермерство
- Пример: Собрать 64 пшеницы, 128 морковок

### 9. FISH - Рыбалка
- Пример: Поймать 30 рыб

### 10. TRADE - Торговля
- Пример: Совершить 20 сделок с жителями

### 11-20. TALK, BUILD, TAME, BREED, ENCHANT, TRAVEL, DEFEND, COMPLETE_DUNGEON, WIN_MINIGAME, DAILY

---

## Уровни Сложности

| Сложность | Множитель награды | Цвет |
|-----------|------------------|------|
| EASY      | 1.0x            | §a (зеленый) |
| MEDIUM    | 1.5x            | §e (желтый) |
| HARD      | 2.0x            | §6 (золотой) |
| EXPERT    | 3.0x            | §c (красный) |
| LEGENDARY | 5.0x            | §5 (фиолетовый) |

---

## REST API Endpoints

### Quest Management

#### GET /api/quests
Получить все квесты
```json
[
  {
    "id": "001_first_blood",
    "name": "Первая Кровь",
    "type": "KILL",
    "difficulty": "EASY",
    ...
  }
]
```

#### GET /api/quests/{id}
Получить конкретный квест

#### POST /api/quests
Создать новый квест
```json
{
  "id": "custom_quest",
  "name": "Мой Квест",
  "description": "Описание",
  "type": "KILL",
  "difficulty": "MEDIUM",
  "level": 5,
  "objectives": [...],
  "rewards": {...}
}
```

#### PUT /api/quests/{id}
Обновить квест

#### DELETE /api/quests/{id}
Удалить квест

---

### Player Progress

#### GET /api/quests/progress/{playerId}
Получить весь прогресс игрока

#### GET /api/quests/progress/{playerId}/{questId}
Получить прогресс по конкретному квесту

#### POST /api/quests/progress/{playerId}/{questId}/start
Начать квест для игрока

#### POST /api/quests/progress/{playerId}/{questId}/complete
Завершить квест

#### POST /api/quests/progress/{playerId}/{questId}/reset
Сбросить прогресс

---

### Statistics

#### GET /api/quests/stats/{playerId}
Получить статистику игрока
```json
{
  "completedQuests": 15,
  "lastCompletion": 1234567890,
  "history": ["001_first_blood:1234567890", ...]
}
```

#### GET /api/quests/leaderboard
Получить таблицу лидеров

---

### Admin

#### POST /api/quests/reload
Перезагрузить квесты из БД

#### GET /api/quests/types
Получить список всех типов квестов

#### GET /api/quests/difficulties
Получить список всех уровней сложности

---

## Конфигурация (config.yml)

```yaml
quest:
  max-active-quests: 10  # Максимум активных квестов
  auto-complete: false    # Автоматическое завершение
  
rewards:
  default-money: 100      # Деньги по умолчанию
  default-exp: 50         # Опыт по умолчанию

api:
  enabled: true           # Включить REST API
  port: 7071             # Порт для API
  host: "0.0.0.0"        # Хост для API
  cors-enabled: true     # Включить CORS

messages:
  prefix: '&8[&6Quests&8]&r '
  quest-accepted: '&aQuest &6{quest} &aaccepted!'
  quest-completed: '&aQuest &6{quest} &acompleted!'
```

---

## Примеры Квестов

### Легкий квест (EASY)
```json
{
  "id": "001_first_blood",
  "name": "Первая Кровь",
  "type": "KILL",
  "difficulty": "EASY",
  "level": 1,
  "objectives": [
    {
      "id": "kill_zombies",
      "type": "KILL",
      "target": "ZOMBIE",
      "required": 10
    }
  ],
  "rewards": {
    "money": 100,
    "exp": 50
  }
}
```

### Легендарный квест (LEGENDARY)
```json
{
  "id": "026_ender_dragon",
  "name": "Убийца Дракона",
  "type": "KILL",
  "difficulty": "LEGENDARY",
  "level": 40,
  "requirements": ["005_diamond_rush", "016_creeper_killer"],
  "objectives": [
    {
      "id": "kill_ender_dragon",
      "type": "KILL",
      "target": "ENDER_DRAGON",
      "required": 1
    }
  ],
  "rewards": {
    "money": 20000,
    "exp": 10000,
    "items": [...]
  }
}
```

---

## Цепочки Квестов

Квесты могут иметь требования (prerequisites):

```
001_first_blood (EASY)
    ↓
004_skeleton_hunter (MEDIUM)
    ↓
015_zombie_slayer (HARD)
    ↓
016_creeper_killer (EXPERT)
    ↓
026_ender_dragon (LEGENDARY)
```

---

## Интеграция с WebX Panel

1. REST API запускается автоматически на порту **7071**
2. Доступ через `http://localhost:7071/api/quests`
3. CORS включен для кросс-доменных запросов
4. Все операции асинхронные через SharedPluginDatabase

---

## Команды

- `/quests` - Открыть меню квестов
- `/quests list` - Список доступных квестов
- `/quests start <id>` - Начать квест
- `/quests progress` - Показать прогресс
- `/quests abandon <id>` - Отменить квест

---

## Разработка

### Добавление нового квеста

1. Создать JSON файл в `quests/`
2. Указать ID, название, тип, сложность
3. Добавить objectives и rewards
4. Опционально: requirements для цепочки

### Добавление нового типа квеста

1. Добавить в `QuestType.java`
2. Создать listener для отслеживания
3. Обновить `ObjectiveManager.java`

---

## База Данных

Использует **SharedPluginDatabase** (common module):
- Асинхронные операции через CompletableFuture
- JSON-сериализация через GSON
- Thread-safe операции с ReadWriteLock
- Автосохранение каждые 5 минут

### Структура данных:
```json
{
  "plugins": {
    "Quests": {
      "quest_001_first_blood": {...},
      "quest_002_wood_collector": {...}
    }
  },
  "players": {
    "uuid": {
      "Quests_progress_001": {...},
      "Quests_completed_quests": 15
    }
  }
}
```

---

## Зависимости

```gradle
implementation(project(":common"))           // SharedPluginDatabase
implementation("io.javalin:javalin:5.6.2")  // REST API
implementation("com.google.code.gson:gson:2.10.1")
```

---

## Авторы

WebX Development Team © 2024
