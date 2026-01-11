# Интегрированная система: Кланы, Экономика и Web-Dashboard

## 📋 Обзор реализованных функций

Комплексная система интеграции кланов, экономики, магазина и web-управления для Minecraft сервера на Paper 1.21.11.

---

## 🏰 Клановая система

### Основные возможности
- ✅ **Создание и управление кланами** с уникальными тегами (до 4 символов)
- ✅ **Система ролей**: Leader (★), Deputy (⚡), Member
- ✅ **Clan Power** - автоматический расчет на основе суммы монет всех участников
- ✅ **Префиксы в чате и табе** с тегом клана и индикатором роли
- ✅ **JSON хранилище** для сохранения данных кланов

### Команды
- `/clan create <name>` - создать клан
- `/clan invite <player>` - пригласить игрока
- `/clan kick <player>` - исключить участника (Leader/Deputy)
- `/clan promote <player>` - назначить Deputy
- `/clan info [clan]` - информация о клане
- `/topclans [limit]` - топ кланов по силе (1-50)

### Clan Power
- Обновляется автоматически каждые 5 минут
- Сила клана = сумма монет всех участников
- Используется для рейтинга кланов

---

## 💰 Экономическая система

### Основные возможности
- ✅ **Начисление монет** за убийство мобов (+1 coin за моба)
- ✅ **JSON/MySQL/YAML хранилище** для балансов
- ✅ **Система транзакций** с историей
- ✅ **Банковская система** для депозитов

### Команды
- `/balance [player]` - проверить баланс
- `/pay <player> <amount>` - перевести деньги
- `/baltop [page]` - топ игроков по балансу
- `/topcoins [limit]` - топ игроков по монетам (1-50)
- `/eco <give|take|set> <player> <amount>` - управление балансами (admin)

### Интеграции
- Combat plugin → автоматическое начисление монет за убийство мобов
- Clans plugin → расчет Clan Power на основе балансов участников
- Shop plugin → покупки за монеты

---

## 🛒 Система магазина

### Основные возможности
- ✅ **GUI магазин** с визуальным интерфейсом
- ✅ **shop.json конфигурация** для товаров
- ✅ **Покупки за coins** с проверкой баланса
- ✅ **Система прав доступа** для VIP товаров
- ✅ **8 товаров по умолчанию** (мечи, броня, elytra и др.)

### Команды
- `/shop` - открыть магазин

### Товары (по умолчанию)
1. Diamond Sword - 50 coins
2. Iron Armor Set - 100 coins
3. Golden Apples (5x) - 25 coins
4. Ender Pearls (16x) - 30 coins
5. Enchanted Book - 75 coins
6. Elytra Wings - 500 coins (требует `shop.vip`)
7. Netherite Ingot - 200 coins
8. Totem of Undying - 300 coins

### Конфигурация товара
```json
{
  "id": "item_id",
  "name": "Display Name",
  "material": "MATERIAL_TYPE",
  "amount": 1,
  "price": 100.0,
  "lore": ["Description line 1", "Line 2"],
  "permission": "shop.vip" // optional
}
```

---

## 🌐 Web-Dashboard API

### Базовый URL
```
http://localhost:9092/api/v1
```

### Clan Endpoints
- `GET /api/v1/clans` - список всех кланов
- `GET /api/v1/clans/{name}` - информация о клане
- `GET /api/v1/clans/player/{uuid}` - клан игрока
- `GET /api/v1/leaderboards/clans?limit=10` - топ кланов

### Leaderboard Endpoints
- `GET /api/v1/leaderboards/players?limit=10` - топ игроков по монетам
- `GET /api/v1/leaderboards/stats` - общая статистика сервера

### Economy Endpoints
- `GET /api/v1/player/{uuid}/coins` - баланс игрока
- `GET /api/v1/players/top` - топ игроков

### Shop Endpoints
- `GET /api/v1/shop` - список товаров
- `POST /api/v1/shop` - добавить товар
- `PUT /api/v1/shop/{id}` - обновить товар
- `DELETE /api/v1/shop/{id}` - удалить товар

### WebSocket Endpoints
- `/metrics` - real-time метрики сервера (TPS, RAM, CPU)
- `/players/metrics` - метрики игроков (online count)

---

## 🔔 Система уведомлений

### Типы уведомлений
- **INFO** (синий) - информационные сообщения
- **SUCCESS** (зеленый) - успешные действия
- **WARNING** (желтый) - предупреждения
- **ERROR** (красный) - ошибки
- **CLAN** (золотой) - клановые события
- **ECONOMY** (желтый) - экономические транзакции
- **EVENT** (фиолетовый) - серверные события

### Способы отображения
1. Chat messages с префиксами
2. Title/Subtitle notifications
3. Action bar сообщения
4. Звуковые уведомления

---

## 🗂️ Структура файлов

### Clans Plugin
```
packages/clans/src/main/java/com/webx/clans/
├── ClansPlugin.java               # Главный класс плагина
├── models/
│   └── Clan.java                  # Модель клана с tag, power, deputies
├── managers/
│   ├── ClanManager.java           # JSON хранилище, CRUD операции
│   └── ClanPowerManager.java      # Автообновление clan power
├── commands/
│   └── TopClansCommand.java       # Команда /topclans
└── listeners/
    └── ClanDisplayListener.java   # Префиксы в чате/табе
```

### Economy Plugin
```
packages/economy/src/main/java/com/webx/economy/
├── EconomyPlugin.java
├── managers/
│   └── AccountManager.java        # Управление балансами
└── commands/
    └── TopCoinsCommand.java       # Команда /topcoins
```

### Shop Plugin
```
packages/shop/src/main/java/com/webx/shop/
├── ShopPlugin.java
├── gui/
│   └── ShopGUI.java              # GUI магазина с покупками
├── managers/
│   └── ShopManager.java          # Загрузка shop.json
└── listeners/
    └── InventoryClickListener.java # Обработка покупок
```

### Combat Plugin
```
packages/combat/src/main/java/com/webx/combat/
└── listeners/
    └── MobKillRewardListener.java # +1 coin за убийство моба
```

### WebX-Dashboard
```
packages/webx-dashboard/src/main/java/com/webx/
├── api/
│   ├── RouterProvider.java       # Регистрация всех маршрутов
│   └── services/
│       ├── ClanService.java      # REST API для кланов
│       └── LeaderboardService.java # REST API лидербордов
└── core/
    └── notifications/
        └── NotificationManager.java # Централизованные уведомления
```

---

## 🚀 Быстрый старт

### 1. Сборка проектов
```bash
./gradlew build
```

### 2. Копирование плагинов
Все JAR файлы из `packages/*/build/libs/` скопировать в `server/plugins/`

### 3. Запуск сервера
```bash
cd server
java -jar paper-1.21.11.jar
```

### 4. Первые команды
```
/clan create MyClans
/balance
/shop
/topclans
/topcoins
```

### 5. Доступ к Web-Dashboard
```
http://localhost:9092
```

---

## 🔧 Конфигурация

### Clan Power Update Interval
В `ClanPowerManager.java`:
```java
// Обновление каждые 5 минут
runTaskTimerAsynchronously(plugin, 20L * 60, 20L * 60 * 5);
```

### Starting Balance
В `AccountManager.java`:
```java
private static final double STARTING_BALANCE = 1000.0;
```

### Shop Items
Редактировать `packages/shop/src/main/resources/shop.json`

---

## 📊 Примеры использования API

### JavaScript
```javascript
// Получить топ кланов
fetch('http://localhost:9092/api/v1/leaderboards/clans?limit=5')
  .then(res => res.json())
  .then(data => {
    data.leaderboard.forEach(clan => {
      console.log(`#${clan.position}: [${clan.tag}] ${clan.name} - Power: ${clan.power}`);
    });
  });

// WebSocket метрики
const ws = new WebSocket('ws://localhost:9092/metrics');
ws.onmessage = (event) => {
  const metrics = JSON.parse(event.data);
  console.log(`TPS: ${metrics.tps}, RAM: ${metrics.memory.used}MB`);
};
```

### Python
```python
import requests

# Статистика сервера
response = requests.get('http://localhost:9092/api/v1/leaderboards/stats')
stats = response.json()
print(f"Players online: {stats['players']['online']}/{stats['players']['total']}")
print(f"Total clans: {stats['clans']['total']}")
print(f"Richest: {stats['economy']['richestPlayer']} ({stats['economy']['richestBalance']} coins)")
```

---

## 🎨 Префиксы и роли

### В чате/табе
- **[TAG]★** - Leader (красная звезда)
- **[TAG]⚡** - Deputy (желтая молния)
- **[TAG]** - Member

### Пример
```
[WAR]★ PlayerName: Hello!
[WAR]⚡ Deputy1: Hi!
[WAR] Member1: Hey!
```

---

## 📈 Будущие улучшения

- [ ] Clan wars/PvP система
- [ ] Клановые территории с защитой
- [ ] Clan bank для общих средств
- [ ] Clan missions/квесты
- [ ] Расширенная статистика в dashboard
- [ ] Discord бот интеграция
- [ ] Clan shop с уникальными товарами
- [ ] Система достижений кланов

---

## 🐛 Решение проблем

### Clan Power не обновляется
- Проверьте, что Economy plugin загружен раньше Clans
- Убедитесь, что оба плагина активны: `/plugins`

### Магазин не открывается
- Проверьте наличие `shop.json` в папке плагина
- Убедитесь, что Economy plugin активен

### API не отвечает
- Проверьте порт 9092: `netstat -an | findstr 9092`
- Убедитесь, что WebX-Dashboard запущен

---

## 📝 Лицензия

Proprietary - WebX Server Systems

---

## 👥 Контакты

Для вопросов и поддержки обращайтесь к администрации сервера.
