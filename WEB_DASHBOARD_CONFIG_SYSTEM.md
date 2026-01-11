# Система управления плагинами через Web Dashboard

## Обзор
Все плагины теперь управляются через веб-панель dashboard. Настройки хранятся в JSON файлах в `plugins/WebX/configs/`.

## Архитектура

### Backend (webx-dashboard)

#### PluginConfigManager
Универсальный менеджер для загрузки/сохранения конфигураций:
- **Расположение**: `com.webx.dashboard.config.PluginConfigManager`
- **Методы**:
  - `loadConfig(pluginName, ConfigClass.class)` - загрузка из JSON
  - `saveConfig(pluginName, config)` - сохранение в JSON
  - `loadConfigAsMap(pluginName)` - загрузка как Map
  - `saveConfigFromMap(pluginName, map)` - сохранение из Map

#### Config Models
Модели конфигурации для каждого плагина:
- `WorldColorsConfig` - цвета игроков по мирам
- `AutoShutdownConfig` - авто-выключение сервера
- `SimpleHealConfig` - настройки лечения
- `DeathMessageConfig` - кастомные сообщения о смерти
- `MobCatchConfig` - ловля мобов
- `FriendFeedConfig` - кормление друзей

#### REST API Endpoints
**PluginConfigEndpoint** предоставляет API для всех плагинов:

```
GET  /api/config/worldcolors     - Получить конфиг WorldColors
PUT  /api/config/worldcolors     - Обновить конфиг WorldColors

GET  /api/config/autoshutdown    - Получить конфиг AutoShutdown
PUT  /api/config/autoshutdown    - Обновить конфиг AutoShutdown

GET  /api/config/simpleheal      - Получить конфиг SimpleHeal
PUT  /api/config/simpleheal      - Обновить конфиг SimpleHeal

GET  /api/config/deathmessage    - Получить конфиг DeathMessage
PUT  /api/config/deathmessage    - Обновить конфиг DeathMessage

GET  /api/config/mobcatch        - Получить конфиг MobCatch
PUT  /api/config/mobcatch        - Обновить конфиг MobCatch

GET  /api/config/friendfeed      - Получить конфиг FriendFeed
PUT  /api/config/friendfeed      - Обновить конфиг FriendFeed
```

### Frontend (webx-dashboard-panel)

#### Dashboard Tabs
Каждый плагин имеет свою вкладку в dashboard:

1. **WorldColors** (`worldcolors-tab.tsx`)
   - Управление цветами по мирам
   - Добавление/удаление миров
   - Настройка интервала обновления
   - Выбор из 16 доступных цветов

2. **AutoShutdown** (`autoshutdown-tab.tsx`)
   - Включение/выключение авто-выключения
   - Настройка таймаута (в минутах)
   - Управление предупреждениями
   - Кастомное сообщение при выключении

3. **SimpleHeal** (`simpleheal-tab.tsx`)
   - Настройка лечения (здоровье, голод, эффекты, огонь)
   - Настройка кулдауна
   - Кастомные сообщения

4. **DeathMessage** (`deathmessage-tab.tsx`)
   - Включение/выключение кастомных сообщений
   - Broadcast настройки
   - Показ координат и мира
   - Сообщения для разных причин смерти (PVP, падение, утопление и т.д.)

5. **MobCatch** (`mobcatch-tab.tsx`)
   - Список разрешенных мобов
   - Черный список мобов
   - Настройка прав доступа
   - Формат имени яйца

6. **FriendFeed** (`friendfeed-tab.tsx`)
   - Настройка восстановления голода/сатурации
   - Управление эффектами (тип, длительность, усиление)
   - Кулдаун и дальность
   - Настройка прав доступа

### Навигация
В `page.tsx` добавлено 16 вкладок:
- Home, Server, Players
- Economy, Shop, AFK
- **Colors** (WorldColors)
- **Shutdown** (AutoShutdown)
- **Heal** (SimpleHeal)
- **Death** (DeathMessage)
- **Mobs** (MobCatch)
- **Feed** (FriendFeed)
- Map, Config, Plugins, Settings

## Структура данных

### Пример конфига WorldColors (worldcolors.json)
```json
{
  "enabled": true,
  "worldColors": {
    "world": "GREEN",
    "world_nether": "RED",
    "world_the_end": "LIGHT_PURPLE"
  },
  "updateInterval": 20
}
```

### Пример конфига AutoShutdown (autoshutdown.json)
```json
{
  "enabled": true,
  "timeout": 10,
  "warnings": [5, 3, 1],
  "shutdownMessage": "§cServer shutting down due to inactivity..."
}
```

### Пример конфига SimpleHeal (simpleheal.json)
```json
{
  "healHealth": true,
  "healFood": true,
  "clearEffects": true,
  "clearFire": true,
  "cooldown": 60,
  "messages": {
    "healed-self": "§aYou have been healed!",
    "healed-other": "§a%player% has been healed!",
    "healed-by": "§aYou have been healed by %healer%!",
    "healed-all": "§aAll players have been healed!",
    "player-not-found": "§cPlayer not found!",
    "no-permission": "§cYou don't have permission!"
  }
}
```

## Плагины с JSON конфигурацией

Все плагины обновлены для чтения настроек из JSON вместо config.yml:

1. **WorldColors** - читает из `worldcolors.json`
   - Проверяет `enabled` перед обновлением цветов
   - Использует `updateInterval` из конфига
   - Автоматически создает дефолтный конфиг при первом запуске

2. **AutoShutdown** - *(требуется обновление)*
3. **SimpleHeal** - *(требуется обновление)*
4. **DeathMessage** - *(требуется обновление)*
5. **MobCatch** - *(требуется обновление)*
6. **FriendFeed** - *(требуется обновление)*

## Зависимости

Все плагины используют:
- **Paper API 1.20.4**
- **GSON 2.10.1** (для JSON парсинга)

## Использование

### Изменение настроек
1. Открыть веб-панель dashboard
2. Перейти на вкладку нужного плагина
3. Изменить настройки
4. Нажать "Save Changes"
5. Плагин автоматически перезагрузит конфигурацию при следующем обращении

### Программное изменение настроек
```java
// Загрузка
WorldColorsConfig config = PluginConfigManager.loadConfig("worldcolors", WorldColorsConfig.class);

// Изменение
config.worldColors.put("world_custom", "GOLD");

// Сохранение
PluginConfigManager.saveConfig("worldcolors", config);
```

## TODO
- [ ] Обновить остальные 5 плагинов для чтения из JSON
- [ ] Добавить WebSocket для live updates настроек
- [ ] Добавить валидацию входных данных на фронтенде
- [ ] Добавить toast уведомления об успешном сохранении
- [ ] Добавить кнопки "Reset to Default" для каждого плагина
