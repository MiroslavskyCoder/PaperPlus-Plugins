# Руководство по интеграции плагинов

## Обзор системы

Реализована полная интеграция между плагинами Economy, Combat, Shop, AFK и WebX Dashboard для управления игровой экономикой и настройками сервера.

## Функционал

### 1. 💰 Начисление монет за убийство монстров

**Плагины:** `Economy` + `Combat`

#### Как работает:
- При убийстве монстра игроком автоматически начисляется **+1 монета**
- Монеты НЕ начисляются за PvP (убийство игроков)
- Данные сохраняются в JSON файл: `plugins/Economy/accounts.json`

#### Файлы:
- [packages/combat/src/main/java/com/webx/combat/listeners/MobKillRewardListener.java](packages/combat/src/main/java/com/webx/combat/listeners/MobKillRewardListener.java)
- [packages/economy/src/main/java/com/webx/economy/managers/AccountManager.java](packages/economy/src/main/java/com/webx/economy/managers/AccountManager.java)

#### Пример работы:
```
[Player] убивает Zombie
[Server] → +1.0 coin
```

---

### 2. 🌐 Web Dashboard - Просмотр монет игрока

**Плагин:** `WebX Dashboard`

#### API Endpoints:

**Получить информацию о монетах игрока:**
```http
GET /api/player/{uuid}/coins
```

**Ответ:**
```json
{
  "success": true,
  "message": "Success",
  "data": {
    "uuid": "player-uuid-here",
    "coins": 1523.5,
    "bankBalance": 500.0,
    "total": 2023.5
  }
}
```

**Топ игроков по балансу:**
```http
GET /api/players/top?limit=10
```

#### Web интерфейс:
Откройте в браузере: `http://localhost:8080/dashboard`

На вкладке **👥 Players**:
1. Введите UUID игрока
2. Нажмите "Search"
3. Увидите баланс, банковский счет и общую сумму

---

### 3. 🛒 Управление Shop через Web Dashboard

**Плагин:** `WebX Dashboard` + `Shop`

#### Конфигурация:
Файл: `plugins/Shop/shop.json`

#### API Endpoints:

**Получить все товары:**
```http
GET /api/shop
```

**Добавить товар:**
```http
POST /api/shop
Content-Type: application/json

{
  "id": "unique-id",
  "name": "Diamond Sword",
  "material": "DIAMOND_SWORD",
  "price": 100.0,
  "icon": "https://example.com/icon.png"
}
```

**Обновить товар:**
```http
PUT /api/shop/{id}
Content-Type: application/json

{
  "name": "Updated Name",
  "material": "DIAMOND_SWORD",
  "price": 150.0,
  "icon": "https://example.com/icon.png"
}
```

**Удалить товар:**
```http
DELETE /api/shop/{id}
```

#### Web интерфейс:

На вкладке **🛒 Shop**:
1. Просмотр всех товаров магазина
2. Добавление нового товара:
   - Название предмета
   - Material (например, `DIAMOND_SWORD`, `IRON_CHESTPLATE`)
   - Цена в монетах
   - URL иконки (опционально)
3. Удаление товаров

#### Пример конфигурации shop.json:
```json
[
  {
    "id": "1",
    "name": "Diamond Sword",
    "material": "DIAMOND_SWORD",
    "price": 100.0,
    "icon": null
  },
  {
    "id": "2",
    "name": "Iron Armor Set",
    "material": "IRON_CHESTPLATE",
    "price": 50.0,
    "icon": null
  }
]
```

---

### 4. 💤 Управление AFK системой через Web Dashboard

**Плагин:** `WebX Dashboard` + `AFK`

#### Конфигурация:
Файл: `plugins/AFK/afk.json`

#### API Endpoints:

**Получить настройки AFK:**
```http
GET /api/afk
```

**Обновить настройки:**
```http
PUT /api/afk
Content-Type: application/json

{
  "timeout": 10,
  "kickEnabled": false,
  "prefix": "§7[AFK] ",
  "suffix": ""
}
```

#### Web интерфейс:

На вкладке **💤 AFK System**:
1. **AFK Timeout** - время неактивности в минутах (по умолчанию 10)
2. **Kick on AFK** - кикать ли игрока при AFK (да/нет)
3. **AFK Prefix** - префикс для AFK игроков (например, `[AFK]`)

#### Параметры:
- `timeout` (int) - минуты до перехода в AFK режим
- `kickEnabled` (boolean) - включить автокик при AFK
- `prefix` (string) - префикс в чате/табе
- `suffix` (string) - суффикс в чате/табе

---

## Установка и запуск

### 1. Компиляция плагинов

```bash
# Из корневой директории проекта
./gradlew build
```

Скомпилированные плагины будут в:
```
packages/economy/build/libs/economy-1.0.jar
packages/combat/build/libs/combat-1.0.jar
packages/shop/build/libs/shop-1.0.jar
packages/afk/build/libs/afk-1.0.jar
webx-dashboard/build/libs/webx-dashboard-1.0.jar
```

### 2. Установка на сервер

Скопируйте JAR файлы в папку `plugins/` вашего Minecraft сервера:
```
server/
  plugins/
    ├── economy-1.0.jar
    ├── combat-1.0.jar
    ├── shop-1.0.jar
    ├── afk-1.0.jar
    └── webx-dashboard-1.0.jar
```

### 3. Конфигурация WebX Dashboard

Файл: `plugins/WebXDashboard/config.yml`

```yaml
api:
  host: "0.0.0.0"
  port: 8080
  
security:
  enabled: false  # Включите для production!
```

### 4. Запуск сервера

1. Запустите Minecraft сервер
2. Проверьте логи:
```
[Economy] Economy plugin enabled!
[Combat] Combat plugin enabled!
[Combat] Mob kill rewards: +1 coin per kill
[Shop] Shop plugin enabled!
[AFK] AFK plugin enabled!
[WebXDashboard] Web Dashboard API started on 0.0.0.0:8080
```

### 5. Доступ к Dashboard

Откройте браузер: **http://localhost:8080/dashboard**

---

## Зависимости плагинов

```
WebX Dashboard (основной)
  ├── Economy (для API монет)
  ├── Shop (для управления магазином)
  └── AFK (для управления AFK настройками)

Combat
  └── Economy (для начисления монет)
```

### Порядок загрузки:
1. **Economy** (первым)
2. **Combat** (зависит от Economy)
3. **Shop**
4. **AFK**
5. **WebX Dashboard** (последним)

---

## Структура данных

### Economy - accounts.json
```json
{
  "accounts": [
    {
      "uuid": "player-uuid",
      "balance": 1523.5,
      "bankBalance": 500.0
    }
  ]
}
```

### Shop - shop.json
```json
[
  {
    "id": "1",
    "name": "Diamond Sword",
    "material": "DIAMOND_SWORD",
    "price": 100.0,
    "icon": null
  }
]
```

### AFK - afk.json
```json
{
  "timeout": 10,
  "kickEnabled": false,
  "prefix": "§7[AFK] ",
  "suffix": ""
}
```

---

## Примеры использования

### JavaScript (fetch)
```javascript
// Получить монеты игрока
const uuid = 'player-uuid-here';
const response = await fetch(`http://localhost:8080/api/player/${uuid}/coins`);
const data = await response.json();
console.log(`Player has ${data.data.coins} coins`);

// Добавить товар в магазин
await fetch('http://localhost:8080/api/shop', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    id: Date.now().toString(),
    name: 'Iron Sword',
    material: 'IRON_SWORD',
    price: 25.0,
    icon: null
  })
});

// Обновить AFK настройки
await fetch('http://localhost:8080/api/afk', {
  method: 'PUT',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    timeout: 15,
    kickEnabled: true,
    prefix: '[AFK] '
  })
});
```

### Python
```python
import requests

# Получить монеты
response = requests.get('http://localhost:8080/api/player/{uuid}/coins')
data = response.json()
print(f"Coins: {data['data']['coins']}")

# Добавить товар
requests.post('http://localhost:8080/api/shop', json={
    'id': '123',
    'name': 'Diamond',
    'material': 'DIAMOND',
    'price': 50.0
})
```

---

## Безопасность

⚠️ **ВАЖНО для production:**

1. Измените host на `127.0.0.1` в config.yml (доступ только с сервера)
2. Используйте reverse proxy (Nginx) с SSL
3. Добавьте аутентификацию (токены)
4. Настройте firewall правила

---

## Troubleshooting

### Монеты не начисляются
- Проверьте, что плагин **Economy** загружен первым
- Проверьте логи: `[Combat] Economy plugin not loaded`

### Dashboard не открывается
- Проверьте порт 8080 (может быть занят)
- Откройте порт в firewall: `netsh advfirewall firewall add rule name="WebX Dashboard" dir=in action=allow protocol=TCP localport=8080`

### Shop.json не сохраняется
- Проверьте права доступа к папке `plugins/Shop/`
- Проверьте логи на ошибки записи файла

---

## Поддержка

Для вопросов и багов создавайте Issue в репозитории проекта.

**API Documentation:** http://localhost:8080/api/health
