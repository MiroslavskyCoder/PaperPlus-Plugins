# 🎮 My Polyglot Project - Minecraft Server Plugins

Коллекция 58+ плагинов для Paper сервера Minecraft 1.21.11 с интеграцией Web Dashboard

## ✨ Основные возможности

### 🌐 Web Dashboard с React/Next.js
- **Drawer навигация** - удобное меню на мобильных и десктопных устройствах
- **REST API** - полное управление через API endpoints
- **Real-time мониторинг** - CPU, RAM, игроки, статистика
- **Управление плагинами** - включение/отключение, конфигурация

### 💰 Интеграция плагинов
- **Economy → Combat** - автоматическое начисление монет за убийства мобов
- **Shop → Economy** - покупка товаров за монеты
- **Clans → Economy** - система кланов с поддержкой экономики
- **Leaderboards API** - таблицы лидеров игроков и кланов

### 🔧 Архитектура
- **Loose coupling** - плагины общаются через рефлексию (без hard dependencies)
- **JSON хранилище** - все данные сохраняются в JSON файлы
- **Модульная структура** - каждый плагин независим

## 🎯 Новое в этой версии

**✅ Исправлено:**
- 🔄 Переделана навигация Web Dashboard с Tabs на Drawer
- 🏪 Исправлена модель ShopItem для корректной работы с JSON
- ⚙️ Исправлены конфликты методов в AutoShutdown плагине
- 🔗 Все cross-plugin импорты переделаны на рефлексию

**✅ Компилируется:**
- 58+ плагинов успешно собираются без ошибок
- Все JAR файлы готовы в `out/plugins/`

---

## 📦 Плагины

### 🏠 HomeTP (0.1.0)
Система домашних точек телепортации с защитой и задержкой

**Команды:**
- `/sethome <название>` - установить дом
- `/delhome <название>` - удалить дом  
- `/home <название>` - телепортироваться в дом
- `/homes` - список всех домов

**Функции:**
- До 5 домов на игрока (настраивается)
- Задержка телепортации 3 секунды
- Отмена при движении или получении урона
- Сохранение координат и направления взгляда

[📖 Подробнее](packages/home-tp/README.md)

---

### ⏪ BackTP (0.1.0)
Телепортация к месту смерти + система запросов на ТП между игроками

**Команды:**
- `/back` - вернуться к месту последней смерти
- `/tpa <игрок>` - запрос на телепортацию к игроку
- `/tpahere <игрок>` - запросить игрока к себе
- `/tpaccept` - принять запрос (алиас: `/tpyes`)
- `/tpdeny` - отклонить запрос (алиас: `/tpno`)

**Функции:**
- Автосохранение точки смерти
- Запросы с таймаутом 60 секунд
- Защита от злоупотреблений
- Задержка телепортации с отменой

[📖 Подробнее](packages/back-tp/README.md)

---

### 💀 DeathMark (0.1.0)
Сохранение вещей игрока в защищенном сундуке при смерти

**Функции:**
- Автоматическое создание сундука на месте смерти
- 3D текст над сундуком с именем игрока (☠ ИмяИгрока ☠)
- Защита от других игроков
- Вещи хранятся вечно (expire-seconds: 0)
- Умный поиск безопасного места для сундука

[📖 Подробнее](packages/death-mark/README.md)

---

### 👹 Abomination (0.1.0)
Враждебный босс с AI, способностями и системой спавна

**Функции:**
- Естественный спавн по ночам
- Автоматический деспавн днём
- Агрессивное поведение и способности
- Система миньонов
- Настраиваемый урон и здоровье

[📖 Подробнее](packages/abomination/README.md)

---

### 🗺️ RegionIgroksMap (0.1.0)
Система управления регионами и минимап

**Функции:**
- Создание и управление регионами
- Защита территорий
- Визуализация на карте
- Права доступа

---

### ⚔️ PvP Base (0.1.0)
Игровые режимы: SkyWars, BedWars, Duels, Siege

**Функции:**
- Множество PvP режимов
- Система матчмейкинга
- Статистика игроков
- Настраиваемые арены

---

### ❤️ ShowHealth (0.1.2)
Отображение здоровья мобов и игроков

**Функции:**
- Полоски здоровья над головой
- Настраиваемый формат
- Поддержка боссов
- Цветовая индикация

---

### 🌍 Warps (0.1.0)
Продвинутая система телепортации с GUI меню

**Функции:**
- Создание и управление вarp точками
- GUI меню для быстрого выбора
- Экономическая стоимость телепортации
- Система кулдаунов и задержек
- Поддержка прав доступа

**Команды:**
- `/warp [name]` - телепортироваться
- `/setwarp <name>` - создать точку
- `/delwarp <name>` - удалить точку
- `/warps` - список/меню

[📖 Подробнее](packages/warps/README.md)

---

### 💰 Economy (0.1.0)
Полнофункциональная система экономики с банком

**Функции:**
- Система балансов и транзакций
- Банк с процентами
- Таблица лидеров (baltop)
- Комиссии за переводы
- Админ команды для управления деньгами

**Команды:**
- `/balance [player]` - баланс
- `/pay <player> <amount>` - отправить деньги
- `/baltop` - топ богатых
- `/bank <deposit|withdraw>` - операции банка

[📖 Подробнее](packages/economy/README.md)

---

### 🏪 Shop (0.1.0)
Система магазинов с GUI и управлением товарами

**Функции:**
- Создание магазинов админом
- GUI интерфейс для покупок
- Раздельные цены на покупку/продажу
- Управление инвентарем магазина

**Команды:**
- `/shop [name]` - открыть магазин
- `/adminshop create <name>` - создать магазин
- `/adminshop delete <name>` - удалить магазин

[📖 Подробнее](packages/shop/README.md)

---

### 👥 Clans (0.1.0)
Система кланов с территориями и рангами

**Функции:**
- Создание и управление кланами
- Система рангов (Лидер/Офицер/Член)
- Территориальная защита (блоки по чанкам)
- Защита от урона между членами клана
- Система приглашений

**Команды:**
- `/clan create <name>` - создать клан
- `/clan invite <player>` - пригласить
- `/clan territory` - управлять территорией
- `/clan info` - информация о клане

[📖 Подробнее](packages/clans/README.md)

---

### ⚡ Quests (0.1.0)
Система квестов с достижениями и наградами

**Функции:**
- Принятие и выполнение квестов
- Различные типы целей (убийства, блоки, сбор)
- Система наград (деньги, опыт, предметы)
- Отслеживание прогресса
- Повторяемые квесты

**Команды:**
- `/quests list` - список квестов
- `/quests accept <quest>` - принять квест
- `/quests info <quest>` - информация
- `/quests abandon <quest>` - отказаться

[📖 Подробнее](packages/quests/README.md)

---

### 🖥️ WebX Dashboard
Web-панель управления сервером

**Функции:**
- Мониторинг в реальном времени (CPU, RAM, игроки)
- Управление игроками
- Система разрешений
- REST API + WebSockets
- React + Next.js интерфейс

[📖 Подробнее](packages/webx-dashboard/README.md)

---

## 🚀 Быстрый старт

### Требования
- **Java**: 21+ (тестировано на Java 21)
- **Gradle**: 9.2.1+
- **Paper Server**: 1.21.11
- **Node.js**: 20+ (для Web Dashboard Panel, опционально)

### Сборка всех плагинов

```bash
# Windows
gradle buildAllPlugins

# Linux/Mac
./gradlew buildAllPlugins
```

Все JAR файлы будут скопированы в `out/plugins/`

### Сборка отдельного плагина

```bash
# Примеры
gradle :economy:build
gradle :shop:build
gradle :clans:build
gradle :webx-dashboard:build
```

### Установка на сервер

1. **Соберите плагины:**
   ```bash
   gradle buildAllPlugins
   ```

2. **Скопируйте JAR в папку plugins:**
   ```bash
   cp out/plugins/*.jar /path/to/server/plugins/
   ```

3. **Запустите/перезапустите сервер:**
   ```bash
   java -Xmx4G -Xms2G -jar paper-1.21.11.jar nogui
   ```

4. **Первый запуск** - плагины создадут конфиги в `plugins/<имя>/config.yml`

### Управление через Web Dashboard

1. **Откройте браузер:**
   ```
   http://localhost:8080
   ```

2. **Используйте Drawer меню** для навигации (≡ кнопка на мобильных)

3. **Доступные разделы:**
   - 📊 Home - основная информация
   - ⚙️ Server - управление сервером  
   - 👥 Players - управление игроками
   - 💰 Economy - система деньги
   - 🛒 Shop - управление магазинами
   - 👑 Clans - управление кланами
   - 📊 Leaderboards - таблицы лидеров
   - 🎮 + 10 других плагинов

---

## ⚙️ Конфигурация

Каждый плагин создаёт свой конфиг при первом запуске:
- `plugins/HomeTP/config.yml` - настройки домов
- `plugins/BackTP/config.yml` - настройки телепортации
- `plugins/DeathMark/config.yml` - настройки сундуков смерти
- `plugins/Abomination/config.yml` - настройки босса
- `plugins/Warps/config.yml` - настройки вarp точек
- `plugins/Economy/config.yml` - настройки экономики
- `plugins/Shop/config.yml` - настройки магазинов
- `plugins/Clans/config.yml` - настройки кланов
- `plugins/Quests/config.yml` - настройки квестов

Все сообщения поддерживают цветовые коды Minecraft (`&a`, `&c`, и т.д.)

## 📁 Структура проекта

```
my-polyglot-project/
├── packages/ (58+ плагинов)
│   ├── Core Plugins:
│   │   ├── home-tp/              # 🏠 Система домов телепортации
│   │   ├── back-tp/              # ⏪ Возврат и TPA система
│   │   ├── warps/                # 🌀 Глобальные точки телепортации
│   │   └── clans/                # 👑 Система кланов с территориями
│   │
│   ├── Economy & Shop:
│   │   ├── economy/              # 💰 Система экономики
│   │   └── shop/                 # 🛒 Магазины с GUI
│   │
│   ├── Progression:
│   │   ├── skills/               # 🎓 Система навыков
│   │   ├── leveling/             # ⬆️ Система уровней
│   │   ├── quests/               # ⚡ Система квестов
│   │   ├── missions/             # 🎯 Миссии и задачи
│   │   └── achievements/         # 🏆 Достижения
│   │
│   ├── Professions:
│   │   ├── mining/               # ⛏️ Майнинг профессия
│   │   ├── farming/              # 🌾 Ферма профессия
│   │   ├── fishing/              # 🎣 Рыбалка профессия
│   │   ├── woodcutting/          # 🪵 Лесоруб профессия
│   │   ├── cooking/              # 🍖 Кулинария профессия
│   │   └── enchanting/           # ✨ Зачаровка профессия
│   │
│   ├── Combat & Events:
│   │   ├── combat/               # ⚔️ Система боя
│   │   ├── pvp-base/             # PvP режимы (SkyWars, BedWars)
│   │   ├── abomination/          # 👹 Босс враги
│   │   ├── bedwarsevent/         # 🛏️ BedWars события
│   │   ├── pvpevents/            # ⚡ PvP события
│   │   ├── miningevents/         # 💥 События при майнинге
│   │   ├── dungeonraids/         # 🏰 Подземелья и рейды
│   │   └── tournaments/          # 🏅 Турниры
│   │
│   ├── Social & Utility:
│   │   ├── antispam/             # 🛡️ Защита от спама
│   │   ├── afk/                  # 💤 AFK система
│   │   ├── news/                 # 📰 Система новостей
│   │   ├── show-health/          # ❤️ Отображение здоровья
│   │   ├── petystem/             # 🐾 Система питомцев
│   │   ├── mounts/               # 🦄 Верховые животные
│   │   └── cosmetics/            # ✨ Косметика
│   │
│   ├── World & Management:
│   │   ├── death-mark/           # 💀 Защита вещей при смерти
│   │   ├── backups/              # 💾 Автоматические бэкапы
│   │   ├── claims/               # 🚩 Система приватов
│   │   ├── regionigroks-map/     # 🗺️ Регионы и минимап
│   │   ├── seasons/              # 🌍 Система сезонов
│   │   ├── randomizer/           # 🎲 Рандомизация
│   │   ├── speedrun/             # ⚡ Режим прохождения
│   │   └── home-tp & warps       # (уже выше)
│   │
│   ├── Markets & Trading:
│   │   ├── market/               # 📈 Торговый рынок
│   │   ├── marketplace/          # 🏪 Площадка торговли
│   │   ├── auction/              # 🔨 Аукцион система
│   │   ├── house-tp/             # 🏘️ Домовая система
│   │   └── shop/ & economy/      # (уже выше)
│   │
│   ├── Admin & Control:
│   │   ├── autoshutdown/         # 🛑 Авто-выключение сервера
│   │   ├── events/               # 🎉 Система событий сервера
│   │   ├── statistics/           # 📊 Статистика игроков
│   │   ├── insurance/            # 🛡️ Система страховки
│   │   ├── chatformatting/       # 💬 Форматирование чата
│   │   ├── levels/               # 📊 Система уровней
│   │   ├── jobs/                 # 💼 Профессия система
│   │   ├── feed/                 # 🍗 Команда для еды
│   │   ├── bounties/             # 💰 Система щитов
│   │   └── guilds/               # ⚔️ Гильдии
│   │
│   ├── Web & API:
│   │   ├── webx-dashboard/       # 🔧 REST API бэкенд (Javalin)
│   │   ├── webx-dashboard-panel/ # 🎨 React/Next.js фронтенд
│   │   └── ... (30+ плагинов еще)
│   │
│   └── build/                    # Скомпилированные классы
│
├── out/plugins/                  # 📦 Готовые JAR файлы (58+)
├── assets/                       # 📄 Ресурсы и документация
├── build.gradle.kts              # 🔨 Корневой скрипт сборки
├── settings.gradle.kts           # ⚙️ Настройки проекта
└── README.md                     # 📖 Этот файл
```

## 🏗️ Архитектура

### Система интеграции плагинов

```
┌─────────────────────────────────────────────────┐
│         Paper Server 1.21.11 + Java 21          │
├─────────────────────────────────────────────────┤
│                                                  │
│  ┌──────────────┐  ┌──────────────┐            │
│  │   Economy    │  │    Shop      │            │
│  │  Plugin      │  │  Plugin      │            │
│  │              │  │              │            │
│  │ - Accounts   │  │ - Items      │            │
│  │ - Balances   │  │ - Prices     │            │
│  │ - JSON Store │  │ - GUI        │            │
│  └──────────────┘  └──────────────┘            │
│         ▲                  ▲                    │
│         │                  │                    │
│         └────────┬─────────┘                    │
│                  │                              │
│            ┌─────▼──────┐                      │
│            │ Reflection │                      │
│            │ Bridge API │ (Loose Coupling)     │
│            └─────┬──────┘                      │
│                  │                              │
│  ┌───────────────┼───────────────┐            │
│  ▼               ▼               ▼            │
│┌─────────┐  ┌─────────┐  ┌──────────────┐   │
││ Clans   │  │ Combat  │  │WebX Dashboard│   │
││ Plugin  │  │ Plugin  │  │   Plugin     │   │
│└─────────┘  └─────────┘  └──────────────┘   │
│                                              │
└──────────────────────────────────────────────┘
```

**Ключевые особенности:**
- ✅ **Loose Coupling** - плагины не зависят друг от друга (импорты через reflection)
- ✅ **JSON Storage** - все данные сохраняются в `plugins/<name>/data/` в JSON формате
- ✅ **REST API** - управление через HTTP endpoints (Javalin framework)
- ✅ **Web UI** - React + Next.js интерфейс с drawer навигацией
- ✅ **Real-time** - WebSockets для live updates
- ✅ **Modularity** - каждый плагин может быть собран отдельно

---

## ⚙️ Конфигурация

### Глобальные настройки (config.yml)

```yaml
# plugins/Economy/config.yml
server:
  name: "Paper 1.21.11"
  version: "1.0.0"

economy:
  enabled: true
  currency-symbol: "$"
  starting-balance: 1000.0
  decimal-places: 2
  
database:
  type: "json"  # json или sqlite
  path: "plugins/Economy/data"
```

### Shop конфигурация (shop.json)

```json
[
  {
    "id": "diamond_sword",
    "name": "⚔️ Diamond Sword",
    "material": "DIAMOND_SWORD",
    "amount": 1,
    "buyPrice": 500.0,
    "sellPrice": 250.0,
    "lore": [
      "Острый меч из алмаза",
      "Наносит +5 урона"
    ],
    "permission": "shop.buy.weapons"
  },
  {
    "id": "golden_apple",
    "name": "🍎 Golden Apple",
    "material": "GOLDEN_APPLE",
    "amount": 1,
    "buyPrice": 100.0,
    "sellPrice": 50.0,
    "lore": ["Восстанавливает здоровье"],
    "permission": null
  }
]
```

### Клан система (clans.json)

```json
{
  "clans": [
    {
      "id": "warriors",
      "name": "Warriors",
      "tag": "[W]",
      "owner": "player-uuid-1",
      "officers": ["player-uuid-2"],
      "members": ["player-uuid-3"],
      "power": 150.0,
      "territory": [
        {"x": 0, "z": 0, "claimed": true},
        {"x": 1, "z": 0, "claimed": true}
      ],
      "balance": 50000.0,
      "createdAt": "2024-01-15T10:30:00Z"
    }
  ]
}
```

---

## 🔗 REST API Endpoints

### Players API
```
GET    /api/v1/players                    # Список всех игроков
GET    /api/v1/players/{uuid}             # Информация о игроке
POST   /api/v1/players/{uuid}/kick        # Кик игрока
POST   /api/v1/players/{uuid}/ban         # Бан игрока
```

### Economy API
```
GET    /api/v1/economy/player/{uuid}      # Баланс игрока
GET    /api/v1/economy/top                # Топ 10 богатых
POST   /api/v1/economy/pay                # Отправить деньги
{
  "from": "uuid-1",
  "to": "uuid-2",
  "amount": 100.0
}
```

### Shop API
```
GET    /api/v1/shop/items                 # Все товары в магазине
POST   /api/v1/shop/buy                   # Купить товар
{
  "playerId": "uuid",
  "itemId": "diamond_sword",
  "quantity": 1
}
```

### Clans API
```
GET    /api/v1/clans                      # Все кланы
GET    /api/v1/clans/{clanId}             # Информация о клане
GET    /api/v1/clans/leaderboard          # Топ кланов по power
POST   /api/v1/clans/create               # Создать клан
{
  "name": "Warriors",
  "tag": "[W]",
  "owner": "uuid"
}
```

### Leaderboards API
```
GET    /api/v1/leaderboards/players       # Топ игроков
GET    /api/v1/leaderboards/clans         # Топ кланов
GET    /api/v1/leaderboards/skills/{skill} # Топ по навыку
```

---

## 🛠️ Команды Gradle

```bash
# Основные команды
gradle buildAllPlugins           # Собрать все 58+ плагинов
gradle copyPlugins                # Скопировать JAR в out/plugins
gradle clean                      # Очистить все сборки
gradle listProjects               # Список всех подпроектов

# Сборка отдельного плагина
gradle :economy:build             # Собрать только Economy
gradle :shop:build                # Собрать только Shop
gradle :clans:build               # Собрать только Clans
gradle :webx-dashboard:build      # Собрать WebX Dashboard

# С параметрами
gradle buildAllPlugins -x test    # Пропустить тесты
gradle clean buildAllPlugins      # Чистая сборка
```

---

## 🚀 GitHub Actions CI/CD

### Автоматизированные Workflows

**Три готовых workflow:**

| Workflow | Триггер | Функции |
|----------|---------|---------|
| **Build & Deploy ZIP** | Push, Tag, PR | Собирает плагины → ZIP архив → Артефакты |
| **Deploy to Server** | Manual (workflow_dispatch) | Загружает плагины на сервер по SSH |
| **Create Release** | Manual (workflow_dispatch) | Создаёт GitHub Release с версией и SHA256 |

### Быстрый старт

**1. Автоматическая сборка при push:**
```bash
git push origin main
# GitHub Actions автоматически:
# ✅ Собирает все 59 плагинов
# ✅ Создаёт ZIP архив
# ✅ Загружает артефакт (90 дней)
```

**2. Создание Release при теге:**
```bash
git tag -a v1.0.0 -m "Release 1.0.0"
git push origin v1.0.0
# GitHub Actions автоматически создаст Release
```

**3. Развёртывание на сервер:**
```
GitHub Actions → Deploy to Server → Run workflow
→ Заполнить: hostname, deploy path, restart?
→ Workflow развернёт плагины на сервер
```

### Настройка Deploy на Server

**Требуемые Secrets:**
1. `DEPLOY_SSH_KEY` - SSH приватный ключ
2. `DEPLOY_USER` - SSH юзер

**Как добавить:**
```
Settings → Secrets and variables → Actions → New secret
```

[📖 Полная документация](.github/GITHUB_ACTIONS.md)

---

## 🔄 Cross-Plugin Communication (Рефлексия)

Плагины общаются через рефлексию без hard dependencies:

```java
// Пример: Shop получает баланс игрока из Economy
try {
    // Получаем EconomyService через рефлексию
    Plugin economyPlugin = Bukkit.getPluginManager().getPlugin("Economy");
    if (economyPlugin != null) {
        Class<?> serviceClass = Class.forName("com.webx.economy.services.EconomyService");
        Method getBalance = serviceClass.getMethod("getBalance", UUID.class);
        Object service = economyPlugin.getClass().getMethod("getInstance").invoke(null);
        double balance = (double) getBalance.invoke(service, playerUUID);
        System.out.println("Player balance: $" + balance);
    }
} catch (Exception e) {
    e.printStackTrace();
}
```

**Преимущества:**
- ✅ Плагины могут работать независимо
- ✅ Нет compile-time зависимостей
- ✅ Graceful degradation если плагин отсутствует
- ✅ Легко добавлять новые плагины

---

## 📊 Статистика проекта

| Метрика | Значение |
|---------|----------|
| **Всего плагинов** | 58+ |
| **Строк Java кода** | 50,000+ |
| **REST API endpoints** | 20+ |
| **JSON конфиг файлов** | 58+ |
| **Версия Paper** | 1.21.11 |
| **Версия Java** | 21+ |
| **Build система** | Gradle 9.2.1 |
| **Frontend** | React + Next.js 15 |
| **Web UI Components** | 40+ (shadcn/ui) |

---

## 🐛 Troubleshooting

### Ошибка: "Cannot find symbol"
```bash
# Решение: Очистить кэш и пересобрать
gradle clean
gradle buildAllPlugins
```

### Plugin не загружается
**Проверьте:**
1. Java версия: `java -version` → 21+
2. Paper версия: `1.21.11` (проверить в server.jar)
3. JAR файл скопирован в `plugins/`
4. Нет ошибок в консоли сервера (check logs)

### Web Dashboard недоступна
```bash
# Проверить порт
netstat -an | grep 8080

# Если занят, изменить в config:
# port: 8080 → port: 8081
```

### Economy плагин не сохраняет данные
**Проверьте:**
1. Папка `plugins/Economy/data/` существует
2. Права на запись в папку
3. Нет ошибок в логах сервера

---

## 📚 Дополнительная документация

| Файл | Описание |
|------|---------|
| [QUICK_START.md](QUICK_START.md) | Быстрый старт |
| [BUILD_ALL.md](BUILD_ALL.md) | Подробная сборка |
| [DOCUMENTATION_INDEX.md](DOCUMENTATION_INDEX.md) | Индекс документов |
| [IMPLEMENTATION_COMPLETE.md](IMPLEMENTATION_COMPLETE.md) | Список завершённых функций |
| [API_DOCUMENTATION.md](packages/webx-dashboard/API_DOCUMENTATION.md) | API endpoints |

---

## 📝 Лицензия

Все плагины разработаны для личного использования.

## 👨‍💻 Автор

WebX Development Team

---

**Последнее обновление**: Январь 2026  
**Версия**: 1.0.0  
**Статус**: ✅ Полностью функционален  
**Build**: ✅ BUILD SUCCESSFUL (190 tasks)
