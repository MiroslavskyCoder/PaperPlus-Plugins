# Сборка всех плагинов

## Команды сборки

### Собрать все плагины одной командой:

```bash
# Полная команда
gradle buildAllPlugins

# Сокращение
gradle all
```

Эта команда:
1. ✅ Соберет все 70+ плагинов проекта
2. ✅ Скопирует готовые JAR файлы в `out/plugins/`
3. ✅ Покажет список собранных плагинов

### Собрать конкретный плагин:

```bash
gradle :achievements:build
gradle :warps:build
gradle :economy:build
# и т.д.
```

### Очистить все сборки:

```bash
gradle clean
```

### Другие полезные команды:

```bash
# Показать список всех задач
gradle tasks

# Показать список всех подпроектов
gradle listProjects

# Собрать без тестов
gradle all -x test

# Собрать с подробным выводом
gradle all --info
```

## Структура после сборки:

```
out/
└── plugins/
    ├── achievements-1.0.0.jar
    ├── warps-0.1.0.jar
    ├── economy-1.0.0.jar
    ├── shop-1.0.0.jar
    └── ... (все остальные плагины)
```

## Плагины включенные в сборку:

**Основные:**
- webx-dashboard
- regionigroks-map
- pvp-base
- warps, economy, shop, clans, quests

**Система игрока:**
- afk, leveling, statistics
- achievements, levels, skills
- pets, petsystem, mounts

**Геймплей:**
- feed, farming, fishing, cooking
- mining, woodcutting, combat
- customenchants, enchanting, potions

**Социальные:**
- chatformatting, partysystem
- guilds, guilds-advanced, clans

**Экономика:**
- marketplace, market, shop
- auction, vaults, insurance

**События и квесты:**
- events, pvpevents, miningevents
- jumpquests, missions, quests
- tournaments, bedwarsevent

**Утилиты:**
- home-tp, back-tp, homesextended
- claims, backups, news
- customitems, cosmetics

**И многие другие!**

Всего: **70+ плагинов**
