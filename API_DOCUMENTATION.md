# WebX Dashboard API - Clan & Leaderboard Endpoints

## Базовый URL
```
http://localhost:9092/api/v1
```

## Clan Endpoints

### GET /api/v1/clans
Получить список всех кланов

**Response:**
```json
{
  "clans": [
    {
      "id": "uuid",
      "name": "Warriors",
      "tag": "WAR",
      "leader": {
        "uuid": "player-uuid",
        "name": "PlayerName"
      },
      "memberCount": 5,
      "power": 1250.50,
      "level": 3,
      "experience": 150,
      "description": "Best clan",
      "createdAt": 1234567890,
      "members": [
        {
          "uuid": "player-uuid",
          "name": "PlayerName",
          "rank": "LEADER",
          "online": true
        }
      ]
    }
  ],
  "total": 10
}
```

### GET /api/v1/clans/{name}
Получить информацию о конкретном клане

**Parameters:**
- `name` - название клана

**Response:**
```json
{
  "id": "uuid",
  "name": "Warriors",
  "tag": "WAR",
  "leader": {
    "uuid": "player-uuid",
    "name": "PlayerName"
  },
  "memberCount": 5,
  "power": 1250.50,
  "level": 3,
  "experience": 150,
  "description": "Best clan",
  "createdAt": 1234567890,
  "members": [...]
}
```

### GET /api/v1/clans/player/{uuid}
Получить клан по UUID игрока

**Parameters:**
- `uuid` - UUID игрока

**Response:**
```json
{
  "id": "clan-uuid",
  "name": "Warriors",
  ...
}
```

**Error Response (404):**
```json
{
  "error": "Player not in any clan"
}
```

## Leaderboard Endpoints

### GET /api/v1/leaderboards/clans
Получить топ кланов по силе

**Query Parameters:**
- `limit` (optional, default: 10, max: 100) - количество кланов

**Response:**
```json
{
  "leaderboard": [
    {
      "position": 1,
      "id": "uuid",
      "name": "Warriors",
      "tag": "WAR",
      "power": 5000.00,
      "memberCount": 10,
      "leader": {
        "uuid": "player-uuid",
        "name": "PlayerName"
      }
    }
  ],
  "total": 10
}
```

### GET /api/v1/leaderboards/players
Получить топ игроков по монетам

**Query Parameters:**
- `limit` (optional, default: 10, max: 100) - количество игроков

**Response:**
```json
{
  "leaderboard": [
    {
      "position": 1,
      "uuid": "player-uuid",
      "name": "PlayerName",
      "balance": 1500.50,
      "online": true
    }
  ],
  "total": 10
}
```

### GET /api/v1/leaderboards/stats
Получить общую статистику сервера

**Response:**
```json
{
  "players": {
    "online": 25,
    "total": 1000
  },
  "clans": {
    "total": 15
  },
  "economy": {
    "richestPlayer": "PlayerName",
    "richestBalance": 5000.00
  }
}
```

## Economy Endpoints (существующие)

### GET /api/v1/player/{uuid}/coins
Получить баланс игрока

### GET /api/v1/players/top
Получить топ игроков (legacy endpoint)

## Shop Endpoints (существующие)

### GET /api/v1/shop
Получить список товаров в магазине

### POST /api/v1/shop
Добавить новый товар

### GET /api/v1/shop/{id}
Получить товар по ID

### PUT /api/v1/shop/{id}
Обновить товар

### DELETE /api/v1/shop/{id}
Удалить товар

## WebSocket Endpoints

### /metrics
Real-time метрики сервера (TPS, memory, CPU)

### /players/metrics
Real-time метрики игроков (online count, список игроков)

## Примеры использования

### JavaScript/Fetch
```javascript
// Получить топ кланов
fetch('http://localhost:9092/api/v1/leaderboards/clans?limit=10')
  .then(res => res.json())
  .then(data => console.log(data));

// Получить клан игрока
const playerUuid = 'uuid-here';
fetch(`http://localhost:9092/api/v1/clans/player/${playerUuid}`)
  .then(res => res.json())
  .then(clan => console.log(clan));

// WebSocket подключение для метрик
const ws = new WebSocket('ws://localhost:9092/metrics');
ws.onmessage = (event) => {
  const metrics = JSON.parse(event.data);
  console.log('Server TPS:', metrics.tps);
};
```

### Python
```python
import requests

# Получить общую статистику
response = requests.get('http://localhost:9092/api/v1/leaderboards/stats')
stats = response.json()
print(f"Online players: {stats['players']['online']}")
print(f"Total clans: {stats['clans']['total']}")
```

## Коды ответов

- `200 OK` - Успешный запрос
- `400 Bad Request` - Неверные параметры
- `404 Not Found` - Ресурс не найден
- `503 Service Unavailable` - Плагин не доступен
